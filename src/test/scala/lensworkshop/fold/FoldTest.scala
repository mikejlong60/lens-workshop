package lensworkshop.fold

import monocle.{Fold, Lens, PTraversal, Traversal}
import org.scalacheck.Gen
import org.scalatest.prop.PropertyChecks
import org.scalatest.{Matchers, PropSpec}
import scalaz.Monoid

class FoldTest extends PropSpec with PropertyChecks with Matchers {

  case class Query(conjunctions: List[Pair])
  case class Pair(subject: String, disjunctions: List[Predicate])
  case class Predicate(passed: Boolean)

  import scalaz.std.list._

  val l1: Lens[Query, List[Pair]] =  ???
  val t1: Traversal[List[Pair], Pair] = ???
  val l2: Lens[Pair, List[Predicate]] = ???
  val t2: Traversal[List[Predicate], Predicate] = ???


  val predGen = Gen.oneOf(Predicate(true), Predicate(false))

  def genPair = for {
    subject <- Gen.alphaStr
    disjunctions <- Gen.listOf(predGen)
  } yield Pair(subject, disjunctions)

  property("Test Fold over nested structure at the last branch before a leaf level") {

    forAll(Gen.listOf(genPair)) { pairs =>
      val t3: PTraversal[Query, Query, Pair, Pair] = ???
      val q: Query = Query(pairs)

      def crush(l1: Pair, l2: => Pair): Pair = ???
      val zeroValue: Pair = Pair("result", List(Predicate(true)))
      val moid: Monoid[Pair] = ???
      val fold: Fold[Query, Pair] = t3.asFold
      val traverseMonoidWay: Pair = fold.fold(q)(moid)

      val stupidWay: Boolean = q.conjunctions.foldLeft(true)((accum, p) => accum && p.disjunctions.exists(s => s.passed))
      traverseMonoidWay.disjunctions should have size (1)
      stupidWay shouldBe traverseMonoidWay.disjunctions.head.passed
    }
  }
}


