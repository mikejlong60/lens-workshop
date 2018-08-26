package lensworkshop.iso

import org.scalacheck.Gen
import org.scalatest.prop.PropertyChecks
import org.scalatest.{Matchers, PropSpec}
import monocle.Iso

class IsoLawsTest extends PropSpec with PropertyChecks with Matchers {

  case class Predicate(predicate: String, argument: String)

  val predicateToTuple = Iso[Predicate, (String, String)](pred => (pred.predicate, pred.argument)){ case (pred, value) => Predicate(pred, value) }

  val predicateToTriple = Iso[Predicate, (String, String, String)](pred => ("big daddy", pred.predicate, pred.argument)){ case (_, pred, value) => Predicate(pred, value) }

  def roundTripOneWay[S, A](i: Iso[S, A], s: S): S = i.reverseGet(i.get(s))

  def roundTripOtherWay[S, A](i: Iso[S, A], a: A): A =  i.get(i.reverseGet(a))

  property("Test ISO Laws.  Why is this busted?") {
    forAll(Gen.alphaStr, Gen.alphaStr, Gen.alphaStr) { (subject, predicate, value) =>
      val s = Predicate(predicate, value)
      val a = (subject, predicate, value)
      roundTripOneWay(predicateToTriple, s) should be(s)
      roundTripOtherWay(predicateToTriple, a) should be(a)
    }
  }

  property("Test ISO laws - Round Trip One Way") {
    forAll(Gen.alphaStr, Gen.alphaStr) { (predicate, value) =>
      val s = Predicate(predicate, value)
      roundTripOneWay(predicateToTuple, s) should be(s)
    }
  }

  property("Test ISO laws - Round Trip Other Way") {
    forAll(Gen.alphaStr, Gen.alphaStr) { (predicate, value) =>
      val a = (predicate, value)
      roundTripOtherWay(predicateToTuple, a) should be(a)
    }
  }
}
