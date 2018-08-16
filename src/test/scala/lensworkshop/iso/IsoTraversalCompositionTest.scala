package lensworkshop.iso

import monocle.{Fold, Traversal}
import org.scalacheck.Gen
import org.scalatest.prop.PropertyChecks
import org.scalatest.{Matchers, PropSpec}
import scalaz.{Compose, Monoid}

class IsoTraversalCompositionTest extends PropSpec with PropertyChecks with Matchers {

  case class Predicate(subject: String, predicate: String, argument: String)

  import monocle.Iso

  val predicateToTripleIso = Iso[Predicate, (String, String, String)](pred => (pred.subject, pred.predicate, pred.argument)) { case (subject, pred, argument) => Predicate(subject, pred, argument) }
  val tripleToPredicateIso = Iso[(String, String, String), Predicate](triple => Predicate(triple._1, triple._2, triple._3)) { case pred => (pred.subject, pred.predicate, pred.argument) }

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

  val _predicateT = Traversal.fromTraverse[List, Predicate]
  val _predicateToTriple = _predicateT composeIso predicateToTripleIso

  val _tripleT = Traversal.fromTraverse[List, (String, String, String)]
  val _tripleToPredicate = _tripleT composeIso tripleToPredicateIso

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

      println("============" + getAll)

      val headOption = _predicateToTriple.headOption(predicates) // should be()
      println("++++++++++++++" + headOption)

      val find = _predicateToTriple.find(l => l._3.size > 3)(predicates) //should be()
      println("--------" + find)

      val all = _predicateToTriple.all(l => l._3.size == 0)(predicates) // should be()
      println("&&&&&&&&&&&&&&" + all) //t: => Long
      //      //val sss: (Tuple3[String,String, String], => Tuple3[String,String, String]) => List[Tuple3[String, String, String]] =  (l1:Tuple3[String, String, String], l2: => Tuple3[String, String, String]) => List(l1)
      //      def sss2(l1: List[Tuple3[String,String, String]], l2: =>List[Tuple3[String,String, String]]): List[Tuple3[String, String, String]] =  l2//(l2: => Tuple3[String, String, String]) => List(l2)
      //
      //      def sss3(l1: Option[Tuple3[String,String, String]], l2: =>Option[Tuple3[String,String, String]]): Option[Tuple3[String, String, String]] =  l2//List(l2)//(l2: => Tuple3[String, String, String]) => List(l2)
      //    def sss4(l1: Predicate, l2: => Predicate): Predicate =  {
      //      Predicate(l1.predicate + l2.predicate, l1.predicate + l2.predicate, l1.predicate + l2.predicate)
      //    }//List(l2)//(l2: => Tuple3[String, String, String]) => List(l2)
      //      //implicit val m = Monoid.instance(sss2, List.empty[Tuple3[String,String, String]])
      //      implicit val m2 = Monoid.instance(sss3, None)
      //      implicit val m3 = Monoid.instance(sss4, Predicate("","",""))
      //      val fold = _tripleToPredicate.asFold//.all(l => l._3.size  == 0)(predicates)// should be()
      //      val predicates2 = List(("d","df","asdfas"))
      //      val rrr = fold.fold(predicates2)(m3)
      //      println("rrr" + rrr)
      //      //fold.fold(s => )
    }
  }

  property("Test traversal fold with a composed traversal") {
    def smasherF(l1: Predicate, l2: => Predicate): Predicate = {
      Predicate(l1.subject + l2.subject, l1.predicate + l2.predicate, l1.argument + l2.argument)
    }
    val zeroValue = Predicate("", "", "")
    implicit val m3 = Monoid.instance(smasherF, zeroValue)
    val fold = _tripleToPredicate.asFold
    forAll(genListOfTriples) { triples =>
      val rrr = fold.fold(triples)(m3)
      println("rrr" + rrr)
    }
  }

//  sealed trait AbstractFilter
//
//  case class Predicate2(subject: String, predicate: String, argument: String) extends AbstractFilter
//
//  case class PredicateDisjunction(predicates: List[Predicate2]) extends AbstractFilter
//
//  case class Filter(predicateConjunctions: Map[String, PredicateDisjunction]) extends AbstractFilter
//
//  property("Test traversal fold with anphe composed traversal") {
//    def smasherF(l1: Filter, l2: => AbstractFilter): AbstractFilter = {
//      l2 match {
//        case p:Predicate2 => {
//          val res = l1.predicateConjunctions.getOrElse(p.subject, PredicateDisjunction(List.empty[Predicate2]))
//          val preds  = p +: res.predicates
//          PredicateDisjunction(preds)
//        }
//      }// Predicate(l1.subject + l2.subject, l1.predicate + l2.predicate, l1.argument + l2.argument)
//    }
//    val zeroValue = Filter(Map.empty[String, PredicateDisjunction])
//    implicit val m3 = Monoid.instance(smasherF, zeroValue)
//    val fold = _tripleT.asFold
//    forAll(genListOfTriples) { triples =>
//      val rrr = fold.fold(triples)(m3)
//      println("rrr" + rrr)
//    }
//  }
//

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
