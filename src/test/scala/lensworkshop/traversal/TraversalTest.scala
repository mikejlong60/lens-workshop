package lensworkshop.traversal

//import monocle.macros.GenLens
import monocle.macros.GenLens
import monocle.{Lens, Prism, Traversal}
import org.scalatest.prop.PropertyChecks
import org.scalatest.{Matchers, PropSpec}

class TraversalTest extends PropSpec with PropertyChecks with Matchers {

  property("test custom Traversal over a Map") {
    import monocle.Traversal
    import scalaz.Applicative
    import scalaz.std.map._
    import scalaz.syntax.applicative._
    import scalaz.syntax.traverse._

    //A Traversal is a PTraversal and you can think of it as a POptional generalized to 0 to n targets.
    //n can be infinite. A Traversal is polymorphic.  It's set and modify methods change a type `A` to `B` and `S` to `T`
    //where `S` is the source of the traversal and `T` is the modified source of the traversal
    //and `A` is the target of the traversal and `B` is the modified target of the traversal.

    //PTraversal[S, S, A, A

    def filterKey[K, V](predicate: K => Boolean): Traversal[Map[K, V], V] =
      new Traversal[Map[K, V], V] {
        //modifyF defines the traversal in Monocle. All other traversal methods are written in terms of it.
        def modifyF[F[_]: Applicative](f: V => F[V])(s: Map[K, V]): F[Map[K, V]] =
          s.map {
            case (k, v) => k -> (if (predicate(k)) f(v) else v.pure[F])
          }.sequenceU
      }

    val m = Map(1 -> "one", 2 -> "two", 3 -> "three", 4 -> "four")

    val filterEven = filterKey[Int, String](key => key % 2 == 0)

    filterEven.modify(_.toUpperCase)(m) should be(Map(1 -> "one", 2 -> "TWO", 3 -> "three", 4 -> "FOUR"))
  }

  property("Fold over custom Traversal") {
    import monocle.Traversal
    import scalaz.Applicative
    import scalaz.std.map._
    import scalaz.syntax.applicative._
    import scalaz.syntax.traverse._

    //A Traversal is a PTraversal and you can think of it as a POptional generalized to 0 to n targets.
    //n can be infinite. A Traversal is polymorphic.  It's set and modify methods change a type `A` to `B` and `S` to `T`
    //where `S` is the source of the traversal and `T` is the modified source of the traversal
    //and `A` is the target of the traversal and `B` is the modified target of the traversal.

    //PTraversal[S, S, A, A

    def filterKey[K, V](predicate: K => Boolean): Traversal[Map[K, V], V] =
      new Traversal[Map[K, V], V] {
        //modifyF defines the traversal in Monocle. All other traversal methods are written in terms of it.
        def modifyF[F[_]: Applicative](f: V => F[V])(s: Map[K, V]): F[Map[K, V]] =
          s.map {
            case (k, v) => k -> (if (predicate(k)) f(v) else v.pure[F])
          }.sequenceU
      }

    val m = Map(1 -> "one", 2 -> "two", 3 -> "three", 4 -> "four")

    val filterEven = filterKey[Int, String](key => key % 2 == 0)

    filterEven.modify(_.toUpperCase)(m) should be(Map(1 -> "one", 2 -> "TWO", 3 -> "three", 4 -> "FOUR"))

    //def getOptionSet(s: S): Boolean =
    //  optional.getOrModify(s).fold(identity, optional.set(_)(s)) == s


    val m2 = Map(1 -> "one")
    //filterEven.fold(identity(m2))//,//.toString))//, (s => _))//(s =>)
  }


  property("Compose Traversal with a Lens to reach into an internal structure and selectively change something.") {
    case class Person(firstName: String, lastName: String, address: Address)
    case class Address(strNumber: Int, streetName: String, iPhones: List[String])

    import scalaz.std.list._ // to get the Traverse instance for List
    import monocle.Lens
    val strNumber = Lens[Address, Int](whole => whole.strNumber)(part => whole => whole.copy(strNumber = part))
    //val strNumber = GenLens[Address](_.strNumber)
    val address = Address(strNumber = 124, streetName = "Rock Court", iPhones = List("540-222-2222", "543-111-1111"))
    strNumber.get(address) should be (124)
    strNumber.set(145)(address) should be (Address(strNumber = 145, streetName = "Rock Court", iPhones = List("540-222-2222", "543-111-1111")))

    val personAddressLens = Lens[Person, Address](whole => whole.address)(part => whole => whole.copy(address = part) )
//    val personAddressLens = GenLens[Person](_.address)

    val personStreetLens = personAddressLens composeLens strNumber
    val person = Person(firstName = "Mike", lastName = "Long", address = address)

    personAddressLens.get(person) should be(address)
    val personWithNewAddress = personStreetLens.set(126)(person)
    val newAddress = Address(strNumber = 126, streetName = "Rock Court", iPhones = List("540-222-2222", "543-111-1111"))
    personWithNewAddress should be (Person(firstName = "Mike", lastName = "Long", address = newAddress))


    val addressPhoneLens = Lens[Address, List[String]](whole => whole.iPhones)(part => whole => whole.copy(iPhones = part))
    val eachPhone = Traversal.fromTraverse[List, String]

    val addressPhoneTrav = addressPhoneLens composeTraversal eachPhone

    val personPhoneTrav = personAddressLens composeLens addressPhoneLens composeTraversal eachPhone

    val changeAreaCodeIf543 = (phone: String) => phone match {
      case phone if phone.startsWith("543") => "443-111-1111"
      case _ => phone

    }

    val originalPhones = addressPhoneTrav.getAll(newAddress)
    originalPhones shouldBe(List("540-222-2222", "543-111-1111"))
    val addressWithNewPhones = addressPhoneTrav.modify(changeAreaCodeIf543)(newAddress)
    addressWithNewPhones shouldBe (Address(126,"Rock Court",List("540-222-2222", "443-111-1111")))

    val personWithNewPhones = personPhoneTrav.modify(changeAreaCodeIf543)(person)
    personWithNewPhones should be (Person("Mike","Long",Address(124,"Rock Court",List("540-222-2222", "443-111-1111"))))
  }

  property("Mess with ISO so you can use it in your compiler to turn a Predicate type into a predicate function") {
    import monocle.Iso

    case class Person(name: String, age: Int)

    val personToTuple = Iso[Person, (String, Int)](p => (p.name, p.age)) { case (name, age) => Person(name, age) }
    personToTuple.get(Person("Zoe", 12)) should be(("Zoe", 12))
    personToTuple.reverseGet(("Zoe", 12)) should be(Person("Zoe", 12))

    case class Dog(name: String, age: Int)
    val personToDog = Iso[Person, Dog](p => Dog (name = p.name, age = p.age * 7)) { case Dog(name, age) => Person(name = name, age = age / 7) }

    personToDog.get(Person("Zoe", 12)) should be(Dog(name = "Zoe", age = 84))
    personToDog.reverseGet(Dog(name = "Zoe", age = 84)) should be(Person(name = "Zoe", age = 12))


    //TODO Now compose it with your traversal and lens to reach deep down into a structure, change it, and return it as something else
    case class Kennel(name: String, inmates: List[Dog])

    case class Prison(name: String, inmates: List[Person])

    val kennelInmatesLens = GenLens[Kennel](_.inmates)
    val dogLens = GenLens[Dog](_.name)


//    val addressPhoneTrav = addressPhoneLens composeTraversal eachPhone
//
//    val personPhoneTrav = personAddressLens composeLens addressPhoneLens composeTraversal eachPhone
//
//    val changeAreaCodeIf543 = (phone: String) => phone match {
//      case phone if phone.startsWith("543") => "443-111-1111"
//      case _ => phone
//
//    }

    val kennelToPrison = Iso[Kennel, Prison](kennel => Prison(name = kennel.name, inmates = List.empty[Person])) {case Prison(nam, inmates) => Kennel(name = nam, inmates = List.empty[Dog])}

    //val kennelDogs = kennelInmatesLens composeIso personToDog// kennelInmatesLens //composeLens dogLens




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

