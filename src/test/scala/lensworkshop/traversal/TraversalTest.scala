package lensworkshop.traversal

import monocle.{Prism, Traversal}
import org.scalatest.prop.PropertyChecks
import org.scalatest.{Matchers, PropSpec}

class TraversalTest extends PropSpec with PropertyChecks with Matchers {

  property("test custom Traversal over a Map") {
    import monocle.Traversal
    import scalaz.Applicative
    import scalaz.std.map._
    import scalaz.syntax.applicative._
    import scalaz.syntax.traverse._

    def filterKey[K, V](predicate: K => Boolean): Traversal[Map[K, V], V] =
      new Traversal[Map[K, V], V] {
        def modifyF[F[_]: Applicative](f: V => F[V])(s: Map[K, V]): F[Map[K, V]] =
          s.map {
            case (k, v) => k -> (if (predicate(k)) f(v) else v.pure[F])
          }.sequenceU
      }

    val m = Map(1 -> "one", 2 -> "two", 3 -> "three", 4 -> "four")

    val filterEven = filterKey[Int, String](key => key % 2 == 0)

    filterEven.modify(_.toUpperCase)(m) should be(Map(1 -> "one", 2 -> "TWO", 3 -> "three", 4 -> "FOUR"))
  }

  property("test Traversal modifyGetAll law") { // to get the Traverse instance for List
    import scalaz.std.list._ // to get the Traverse instance for List
    forAll { list: List[Int] =>

      def modifyGetAllLaw[S, A](t: Traversal[S, A], s: S, f: A => A): Boolean = {
        val l = t.getAll(t.modify(f)(s))
        val r = t.getAll(s).map(f)
        println(l)
        println(r)
        l == r
      }

      val eachL = Traversal.fromTraverse[List, Int]

      modifyGetAllLaw(eachL, list, (x: Int) => x + 1) should be(true)
    }
  }

  property("test Traversal composeModify law") { // to get the Traverse instance for List
    import scalaz.std.list._ // to get the Traverse instance for List

    forAll { list: List[Int] =>

      def composedModifyLaw[S, A](t: Traversal[S, A], s: S, f: A => A, g: A => A): Boolean = {
        val l = t.modify(g)(t.modify(f)(s))
        val r = t.modify(g compose f)(s)
        println(l)
        println(r)
        l == r
      }

      val eachL = Traversal.fromTraverse[List, Int]

      composedModifyLaw(eachL, list, (x: Int) => x + 1, (y: Int) => y + 2) == (true)

    }
  }
}

