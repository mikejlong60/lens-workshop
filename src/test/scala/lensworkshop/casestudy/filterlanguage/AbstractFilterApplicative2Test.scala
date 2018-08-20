package lensworkshop.casestudy.filterlanguage

import org.scalacheck.Prop.forAll
import org.scalacheck.Properties
import AbstractFilter._
class AbstractFilterApplicative2Test extends Properties("AbstractFilter2 Applicative Laws test") {

  import ApplicativeInstances.filterApplicative._

  type INT = Int
  type DOUBLE = Double
  type STRING = String
  type TRIPLE = (String, String, String)

  val triplef: TRIPLE => Int = t => t._1.size + t._2.size + t._3.size

  val pureIdentity: AbstractFilter[TRIPLE => TRIPLE] = pure(identity)

  val pureTripleF = pure(triplef)
  val toPureTriple = { a: TRIPLE => pure(a) }
  val f: TRIPLE => DOUBLE = { t: TRIPLE => t._1.size + t._2.size + t._3.size + 200.0 }
  val g: DOUBLE => STRING = { t => s"I am a double of - $t ,yeah!!!" }
  val h: STRING => INT = _.length

  property("map over it") = forAll(AbstractFilterGenerator.genFilter) { conjunctions =>
    val expectedFilter = Filter(predicateConjunctions = conjunctions)
    val mapped = map(expectedFilter)(triplef)
    //println("crap:" + mapped)
    true
  }

  // ap(id)(a) == a
  property("identity law") = forAll(AbstractFilterGenerator.genFilter) { conjunctions =>
    val expectedFilter = Filter(predicateConjunctions = conjunctions)
    apply(pureIdentity)(expectedFilter) == expectedFilter
  }

  // ap(pure(f))(pure(a)) == pure(f(a))
  property("homomorphism law") = forAll(AbstractFilterGenerator.genPredicate("date")) { conjunctions =>
    val triple = conjunctions.phrase
    apply(pureTripleF)(pure(triple)) == pure(triplef(triple))
  }

  // {x => pure(x)}(a) == pure(a)
  property("interchange law") = forAll(AbstractFilterGenerator.genPredicate("date")) { conjunctions =>
    val triple = conjunctions.phrase
    toPureTriple(triple) == pure(triple)
  }

  // pure(h o g o f) == ap(pure(h o g))(pure(f(a)))
  property("composition law") = forAll(AbstractFilterGenerator.genPredicate("date")) { conjunctions =>
    val triple = conjunctions.phrase
    val gH = g andThen h // (DOUBLE => STRING) => (STRING => INT)
  val fGH = f andThen gH //(TRIPLE => DOUBLE) => (DOUBLE => INT)
  val pureGH = pure(gH) //AbstractFilter[DOUBLE => INT]
  val pureFA = pure(f(triple)) //AbstractFilter[DOUBLE]
    pure(fGH(triple)) == apply(pureGH)(pureFA)
  }

  property("Try some stuff") = forAll(AbstractFilterGenerator.genFilter) { conjunctions =>
    import FunctorInstances.filterFunctor
    val boxOfAs = Filter(predicateConjunctions = conjunctions)
    //filter.predicateConjunctions.map(f => f._2 )
    val lamb = (t: TRIPLE) => (d: String) => t._1 == "fred"


    // val lambdaF: TRIPLE => (String => Boolean) =     (t: TRIPLE) => t._3 == t._3
    //   ??? ///{t => t._3 == t._3}
    val result = filterFunctor.map(boxOfAs)(lamb)
    val result2 = apply(result)(pure("fred"))
//    println("craphead:" + result) //== expectedFilter
//    println("craphead2:" + result2) //== expectedFilter
    true
  }

  property("maker your own filter") = forAll { a: INT =>
    val filter:AbstractFilter[Int => Boolean] = Filter(predicateConjunctions = Map(
      "biggerThanTwelve" -> PredicateDisjunction(predicates = List(PredicatePhrase(phrase = (x: Int) => x > 12))),
      "smallerThanTwelve" -> PredicateDisjunction(predicates = List(PredicatePhrase(phrase = (x: Int) => x > 12))),
    ))

    val ss = pure(filter)
    val aa = pure(a)
    val result = apply(filter)(aa)
    println("butt:"+result)
  //  val gH = g andThen h // (Double => String) => (String => Int)
  //val fGH = f andThen gH //(Int => Double) => (Double => Int)
  //val pureGH = pure(gH) //AbstractFilter[Double => Int]
  //val pureFA = pure(f(a)) //AbstractFilter[Double]
  //  pure(fGH(a)) == apply(pureGH)(pureFA)
  true
  }

}


  //(filter: EventPredicateResult) => {
  //  predicate.predicate match {
  //    case "equals" => EventPredicateResult(filter.event, filter.predicate || filter.event.event.source == predicate.argument)

//}
