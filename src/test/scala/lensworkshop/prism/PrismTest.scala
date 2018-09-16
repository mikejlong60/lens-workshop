package lensworkshop.prism

import monocle.Prism
import org.scalatest.{Matchers, PropSpec}
import org.scalatest.prop.PropertyChecks

/**
 * Show how could we use Optics to manipulate some Json AST
 */
class PrismTest extends PropSpec with PropertyChecks with Matchers {

  sealed trait Json

  case class JStr(s: String) extends Json
  case class JNum(n: Int) extends Json
  case class JArray(l: List[Json]) extends Json
  case class JObj(m: Map[String, Json]) extends Json

  val jStr = Prism[Json, String] {
    case JStr(s) => Some(s)
    case _ => None
  }(JStr.apply)

  property("Json Prism") {
    jStr.getOption(JStr("Hello")) should be(Some("Hello"))
  }

  val jStrP = Prism.partial[Json, String] { case JStr(v) => ??? }(???)

  val jNumP = Prism.partial[Json, Int] { case JNum(v) => ??? }(???)

  def partialRoundTripOneWay[S, A](p: Prism[S, A], s: S): Boolean =
    p.getOption(s) match {
      case None => true // nothing to prove
      case Some(a) => p.reverseGet(a) == s
    }

  def partialRoundTripOtherWay[S, A](p: Prism[S, A], a: A): Boolean =
    p.getOption(p.reverseGet(a)) == Some(a)

  property("Partial Round Trip One Way Law") {
    forAll { (s: String, x: Int) =>
      partialRoundTripOneWay(jStrP, JStr(s)) should be(true)
      partialRoundTripOneWay(jNumP, JNum(x)) should be(true)
    }
  }

  property("Partial Round Trip Other Way Law") {
    forAll { (s: String, x: Int) =>
      partialRoundTripOtherWay(jStrP, s) should be(true)
      partialRoundTripOtherWay(jNumP, x) should be(true)
    }
  }

}

