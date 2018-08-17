package lensworkshop.casestudy.filterlanguage

import lensworkshop.casestudy.filterlanguage.FunctorInstances.filterFunctor
import org.scalacheck.Prop.forAll
import org.scalacheck.Properties

class AbstractFilterFunctorTest extends Properties("AbstractFilter Functor Laws test") {

  val f: Tuple3[String, String, String] => Boolean = pred => pred._2 == pred._3
  val g: Boolean => String = pred => pred.toString
  val h: String => Int = _.length
  val fG = f andThen g
  val mapFG: AbstractFilter[(String, String, String)] => AbstractFilter[String] = filterFunctor.map(_)(fG)
  val mapF: AbstractFilter[(String, String, String)] => AbstractFilter[Boolean] = filterFunctor.map(_)(f)
  val mapG: AbstractFilter[Boolean] => AbstractFilter[String] = filterFunctor.map(_)(g)

  property("identity") = forAll(AbstractFilterGenerator.genFilter) { predicateConjunctions =>
    val expected = Filter(predicateConjunctions)
    filterFunctor.map(expected)(identity) == expected
  }

  property("composition") = forAll(AbstractFilterGenerator.genFilter) { predicateConjunctions =>
    val expected = Filter(predicateConjunctions)
    mapFG(expected) == (mapF andThen mapG)(expected)
  }

  property("associativity") = forAll(AbstractFilterGenerator.genFilter) { predicateConjunctions =>
    val expected = Filter(predicateConjunctions)
    val gH = g andThen h
    val mapGH: AbstractFilter[Boolean] => AbstractFilter[Int] = filterFunctor.map(_)(gH)
    val mapH: AbstractFilter[String] => AbstractFilter[Int] = filterFunctor.map(_)(h)
    (mapF andThen mapGH)(expected) == (mapFG andThen mapH)(expected)
  }

}

