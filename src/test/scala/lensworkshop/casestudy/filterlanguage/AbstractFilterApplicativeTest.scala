package lensworkshop.casestudy.filterlanguage

import org.scalacheck.Prop.forAll
import org.scalacheck.Properties

class AbstractFilterApplicativeTest extends Properties("AbstractFilter Applicative Laws test") {

  import ApplicativeInstances.filterApplicative._
  type A = Int
  type B = Double
  type C = String
  type D = Int

  type TRIPLE = (String, String, String)

  val f: A => B = _ + 2.0
  val g: B => C = _ + "hello"
  val h: C => D = _.length

  val triplef: TRIPLE => Int = t => t._1.size + t._2.size + t._3.size

  val pureIdentity: AbstractFilter[TRIPLE => TRIPLE] = pure(identity)
  val pureF = pure(f)
  val toPureA = { a: A => pure(a) }

  property("map over it") = forAll(AbstractFilterGenerator.genFilter) { conjunctions =>
    val expectedFilter = Filter(predicateConjunctions = conjunctions)
    val mapped = map(expectedFilter)(triplef)
    println("crap:" + mapped)
    true
  }

  // ap(id)(a) == a
  property("identity law") = forAll(AbstractFilterGenerator.genFilter) { conjunctions =>
    val expectedFilter = Filter(predicateConjunctions = conjunctions)
    apply(pureIdentity)(expectedFilter) == expectedFilter
  }

  // ap(pure(f))(pure(a)) == pure(f(a))
  property("homomorphism law") = forAll { a: A =>
    apply(pureF)(pure(a)) == pure(f(a))
  }

  // {x => pure(x)}(a) == pure(a)
  property("interchange law") = forAll { a: A =>
    toPureA(a) == pure(a)
  }

  // pure(h o g o f) == ap(pure(h o g))(pure(f(a)))
  property("composition law") = forAll { a: A =>
    val gH = g andThen h
    val fGH = f andThen gH
    val pureGH = pure(gH)
    val pureFA = pure(f(a))
    pure(fGH(a)) == apply(pureGH)(pureFA)
  }
}
