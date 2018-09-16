package lensworkshop.iso

import monocle.{Fold, PTraversal, Traversal}
import org.scalacheck.Gen
import org.scalatest.prop.PropertyChecks
import org.scalatest.{Matchers, PropSpec}
import scalaz.{Compose, Monoid}

class IsoTraversalCompositionTest extends PropSpec with PropertyChecks with Matchers {

  case class Predicate(subject: String, predicate: String, argument: String)

  import monocle.Iso

  val predicateToTripleIso: Iso[Predicate, (String, String, String)] = Iso[Predicate, (String, String, String)](pred => ???) { case (subject, pred, argument) => ??? }
  val tripleToPredicateIso: Iso[(String, String, String), Predicate] = ???

  object IsoLaws {
    def roundTripOneWay[S, A](i: Iso[S, A], s: S): S = i.reverseGet(i.get(s))

    def roundTripOtherWay[S, A](i: Iso[S, A], a: A): A = i.get(i.reverseGet(a))
  }

  property("Test Iso laws") {
    forAll(Gen.alphaStr, Gen.alphaStr, Gen.alphaStr) { (subject, predicate, value) =>
      val pred = Predicate(subject, predicate, value)
      val triple = (subject, predicate, value)
      IsoLaws.roundTripOneWay(predicateToTripleIso, pred) should be(pred)
      IsoLaws.roundTripOtherWay(predicateToTripleIso, triple) should be(triple)

      IsoLaws.roundTripOneWay(tripleToPredicateIso, triple) should be(triple)
      IsoLaws.roundTripOtherWay(tripleToPredicateIso, pred) should be(pred)
    }
  }

  val genTriple =
    for {
      subject <- Gen.alphaStr
      predicate <- Gen.alphaStr
      argument <- Gen.alphaStr
    } yield (subject, predicate, argument)

  val genPredicate = for {
    triple <- genTriple
  } yield Predicate(triple._1, triple._2, triple._3)

  val genListOfTriples = for {
    triples <- Gen.listOf(genTriple)
  } yield triples

  val genListOfPredicates = for {
    predicates <- Gen.listOf(genPredicate)
  } yield predicates

  import scalaz.std.list._ // to get the Traverse instance for List

  object TraversalLaws {
    def modifyGetAll[S, A](t: Traversal[S, A], s: S, f: A => A): Boolean = {
      t.getAll(t.modify(f)(s)) == t.getAll(s).map(f)
    }
    def composeModify[S, A](t: Traversal[S, A], s: S, f: A => A, g: A => A): Boolean =
      t.modify(g)(t.modify(f)(s)) == t.modify(g compose f)(s)
  }

  val _predicateT: Traversal[List[Predicate], Predicate] = Traversal.fromTraverse[List, Predicate]
  val _predicateToTriple: PTraversal[List[Predicate], List[Predicate], (String, String, String), (String, String, String)] = ???

  val _tripleT: Traversal[List[(String, String, String)], (String, String, String)] = Traversal.fromTraverse[List, (String, String, String)]
  val _tripleToPredicate: PTraversal[List[(String, String, String)], List[(String, String, String)], Predicate, Predicate] = ???

  property("Test traversal laws with a composed traversal") {
    val f: (Predicate => Predicate) = { case pred => Predicate(pred.subject, pred.predicate, "really " + pred.argument) }
    val g: (Predicate => Predicate) = { case pred => Predicate(pred.predicate, "really " + pred.argument, pred.subject) }
    forAll(genListOfTriples) { triples =>
      TraversalLaws.modifyGetAll(_tripleToPredicate, triples, f)
      TraversalLaws.composeModify(_tripleToPredicate, triples, f, g)
    }
  }

  property("Test traversal laws with another composed traversal") {
    val f: (Tuple3[String, String, String] => Tuple3[String, String, String]) = { case (subject, pred, argument) => (subject, pred, "really " + argument) }
    val g: (Tuple3[String, String, String] => Tuple3[String, String, String]) = { case (subject, pred, argument) => ("really " + argument, subject, pred) }
    forAll(genListOfPredicates) { predicates =>
      TraversalLaws.modifyGetAll(_predicateToTriple, predicates, f)
      TraversalLaws.composeModify(_predicateToTriple, predicates, f, g)

      val getAll = _predicateToTriple.getAll(predicates)

      val headOption: Option[(String, String, String)] = _predicateToTriple.headOption(predicates)
      val find: Option[(String, String, String)] = _predicateToTriple.find(l => l._3.size > 3)(predicates)
      val all: Boolean = _predicateToTriple.all(l => l._3.size == 0)(predicates)
    }
  }

  property("Test traversal fold with a composed traversal") {
    def smasherF(l1: Predicate, l2: => Predicate): Predicate = {
      Predicate(l1.subject + l2.subject, l1.predicate + l2.predicate, l1.argument + l2.argument)
    }
    val zeroValue: Predicate = Predicate("", "", "")
    implicit val m3: Monoid[Predicate] = Monoid.instance(smasherF, zeroValue)
    val fold: Fold[List[(String, String, String)], Predicate] = ???
    forAll(genListOfTriples) { triples =>
      fold.fold(triples)(m3)
    }
  }

  import scalaz.std.anyVal._
  import scalaz.std.list._
  import scalaz.std.string._
  import scalaz.{-\/, Category, Choice, Compose, Monoid, Unzip}

  val eachLi: Fold[List[Int], Int] = Fold.fromFoldable[List, Int]
  def eachL2[A, B]: Fold[List[(A, B)], (A, B)] = Fold.fromFoldable[List, (A, B)]

  def nestedListFold[A] = new Fold[List[List[A]], List[A]]{
    def foldMap[M: Monoid](f: (List[A]) => M)(s: List[List[A]]): M =
      s.foldRight(Monoid[M].zero)((l, acc) => Monoid[M].append(f(l), acc))
  }

  // test implicit resolution of type classes

  property("Fold has a Compose instance") {
    Compose[Fold].compose(eachLi, nestedListFold[Int]).fold(List(List(1,2,3), List(4,5), List(6))) shouldEqual 21
  }

}
