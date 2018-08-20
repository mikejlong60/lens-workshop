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

  // ap(id)(a) == a
  property("identity law") = forAll(AbstractFilterGenerator.genFilter) { conjunctions =>
    val expectedFilter = Filter(predicateConjunctions = conjunctions)
    ap(pureIdentity)(expectedFilter) == expectedFilter
  }

  // ap(pure(f))(pure(a)) == pure(f(a))
  property("homomorphism law") = forAll { a: INT =>
    ap(pureF)(pure(a)) == pure(f(a))
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
    pure(fGH(a)) == ap(pureGH)(pureFA)
  }


  property("test apply using a static filter") = forAll { a: INT =>
    val filter = Filter(predicateConjunctions = Map(
      "positive" -> PredicateDisjunction(predicates = List(PredicatePhrase(phrase = (x: Int) => x >  0))),
      "zero" -> PredicateDisjunction(predicates = List(PredicatePhrase(phrase = (x: Int) => x ==  0))),
      "negative" -> PredicateDisjunction(predicates = List(PredicatePhrase(phrase = (x: Int) => x < 0))),
    ))

    val expectedGTZero = Filter(predicateConjunctions = Map(
      "positive" -> PredicateDisjunction(predicates = List(PredicatePhrase(true))),
      "zero" -> PredicateDisjunction(predicates = List(PredicatePhrase(false))),
      "negative" -> PredicateDisjunction(predicates = List(PredicatePhrase(false))),
    ))

    val expectedEQZero = Filter(predicateConjunctions = Map(
      "positive" -> PredicateDisjunction(predicates = List(PredicatePhrase(false))),
      "zero" -> PredicateDisjunction(predicates = List(PredicatePhrase(true))),
      "negative" -> PredicateDisjunction(predicates = List(PredicatePhrase(false))),
    ))


    val expectedLTZero = Filter(predicateConjunctions = Map(
      "positive" -> PredicateDisjunction(predicates = List(PredicatePhrase(false))),
      "zero" -> PredicateDisjunction(predicates = List(PredicatePhrase(false))),
      "negative" -> PredicateDisjunction(predicates = List(PredicatePhrase(true))),
    ))

    val result = ap(filter)(pure(a))

    a match {
      case x if x > 0 => result == expectedGTZero
      case x if x == 0 => result == expectedEQZero
      case _ => result == expectedLTZero
    }
  }

  property("map over it") = forAll(AbstractFilterGenerator.genFilter) { conjunctions =>
    val filter = Filter(predicateConjunctions = conjunctions)
    val mapped = map(filter)(triplef)
    mapped.isInstanceOf[Filter[Int]]
  }

  property("map over it using a static filter") = forAll { (r1: Short, r2: Short, r3: Short) =>
    def repeatString(char:String, n: Short) = List.fill(n)(char).mkString

    val as = repeatString("a", r1)
    val bs = repeatString("b", r2)
    val cs = repeatString("c", r3)

    val filter = Filter(predicateConjunctions = Map(
      "foo" -> PredicateDisjunction(predicates = List(PredicatePhrase(as, bs, cs), PredicatePhrase("a","b","c"))),
      "bar" -> PredicateDisjunction(predicates = List(PredicatePhrase("a","b","c")))
    ))
    val actual = map(filter)(triplef)

    val expected = Filter(predicateConjunctions = Map(
      "foo" -> PredicateDisjunction(predicates = List(PredicatePhrase(as.size + bs.size + cs.size), PredicatePhrase(3))),
      "bar" -> PredicateDisjunction(predicates = List(PredicatePhrase(3)))
    ))
    actual == expected
  }

}
