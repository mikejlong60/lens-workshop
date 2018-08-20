package lensworkshop.casestudy.filterlanguage

import org.scalacheck.Prop.forAll
import org.scalacheck.Properties

class AbstractFilterApplicativeTest extends Properties("AbstractFilter Applicative Laws test") {

  import ApplicativeInstances.filterApplicative._
  type INT = Int
  type DOUBLE = Double
  type STRING = String
  type INT_AGAIN = Int

  type TRIPLE = (String, String, String)

  val f: INT => DOUBLE = _ + 2.0
  val g: DOUBLE => STRING = _ + "hello"
  val h: STRING => INT_AGAIN = _.length

  val triplef: TRIPLE => Int = t => t._1.size + t._2.size + t._3.size

  val pureIdentity: AbstractFilter[TRIPLE => TRIPLE] = pure(identity)
  val pureF = pure(f)
  val toPureA = { a: INT => pure(a) }

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
  property("homomorphism law") = forAll { a: INT =>
    apply(pureF)(pure(a)) == pure(f(a))
  }

  // {x => pure(x)}(a) == pure(a)
  property("interchange law") = forAll { a: INT =>
    toPureA(a) == pure(a)
  }

  // pure(h o g o f) == ap(pure(h o g))(pure(f(a)))
  property("composition law") = forAll { a: INT =>
    val gH = g andThen h // (Double => String) => (String => Int)
    val fGH = f andThen gH //(Int => Double) => (Double => Int)
    val pureGH = pure(gH) //AbstractFilter[Double => Int]
    val pureFA = pure(f(a)) //AbstractFilter[Double]
    pure(fGH(a)) == apply(pureGH)(pureFA)
  }


  property("make your own filter") = forAll { a: INT =>
    val positive: Int => Boolean = _ > 0
    val negative: Int => Boolean = _ < 0
    val listOfF = List(pure(positive), pure(negative))
    val result = listOfF.map(f => apply(f)(pure(a)))
    println(s"value $a applied list of preds:"+result)



    val filter = Filter(predicateConjunctions = Map(
      "positive" -> PredicateDisjunction(predicates = List(PredicatePhrase(phrase = (x: Int) => x >  0))),
      "negative" -> PredicateDisjunction(predicates = List(PredicatePhrase(phrase = (x: Int) => x < 0))),
    ))

    val filterFuncs = filter.predicateConjunctions.values.toList///.map(fff => )//(fff => pure(fff._2))//.toList///.map
    val result2 = map(pure(-12))(positive)

    println("result:"+result)
    println("result2:"+result2)

    result == result2 //true

  }

}
