package lensworkshop.fold

import monocle.{Fold, Lens, Traversal}
import org.scalacheck.Gen
import org.scalatest.prop.PropertyChecks
import org.scalatest.{Matchers, PropSpec}
import scalaz.Monoid

class FoldTest extends PropSpec with PropertyChecks with Matchers {

  case class Query(conjunctions: List[Pair])
  case class Pair(subject: String, disjunctions: List[Predicate])
  case class Predicate(passed: Boolean)

  import scalaz.std.list._

  val l1 = Lens[Query, List[Pair]](whole => whole.conjunctions)(part => whole => whole.copy(conjunctions = part))
  val t1 = Traversal.fromTraverse[List, Pair]
  val l2 = Lens[Pair, List[Predicate]](whole => whole.disjunctions)(part => whole => whole.copy(disjunctions = part))
  val t2 = Traversal.fromTraverse[List, Predicate]


  val predGen = Gen.oneOf(Predicate(true), Predicate(false))

  def genPair = for {
    subject <- Gen.alphaStr
    disjunctions <- Gen.listOf(predGen)
  } yield Pair(subject, disjunctions)

  property("Test Fold over nested structure at the last branch before a leaf level") {

    forAll(Gen.listOf(genPair)) { pairs =>
      val t3 = l1 composeTraversal t1
      val q = Query(pairs)

      def crush(l1: Pair, l2: => Pair): Pair = Pair("result", List(Predicate(l1.disjunctions.exists(p => p.passed) && l2.disjunctions.exists(p => p.passed))))
      val zeroValue = Pair("result", List(Predicate(true)))
      val moid: Monoid[Pair] = Monoid.instance(crush, zeroValue)
      val fold: Fold[Query, Pair] = t3.asFold
      val traverseMonoidWay: Pair = fold.fold(q)(moid)

      val stupidWay = q.conjunctions.foldLeft(true)((accum, p) => accum && p.disjunctions.exists(s => s.passed))
      traverseMonoidWay.disjunctions should have size (1)
      stupidWay shouldBe traverseMonoidWay.disjunctions.head.passed
    }
  }
}


