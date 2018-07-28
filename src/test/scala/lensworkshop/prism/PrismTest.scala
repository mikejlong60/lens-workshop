package lensworkshop.prism

import monocle.{Prism, Traversal}
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
    //jStr("hello") should be ("fred")

    jStr.getOption(JStr("Hello")) should be(Some("Hello"))
  }

}

