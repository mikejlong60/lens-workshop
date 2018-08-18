package lensworkshop.casestudy.filterlanguage

//Thanks to: https://speakerdeck.com/danielasfregola/scalaworld-2017-a-pragmatic-introduction-to-category-theory

trait Functor[Box[_]] {
  def map[A, B](boxA: Box[A])(f: A => B): Box[B]
}

object FunctorInstances {

  val filterFunctor: Functor[AbstractFilter] = new Functor[AbstractFilter] {
    override def map[A, B](boxA: AbstractFilter[A])(f: A => B): AbstractFilter[B] = boxA match {
      case PredicatePhrase(p) => PredicatePhrase(f(p))
      case PredicateDisjunction(predicates) => PredicateDisjunction(predicates.map(predicate => map(predicate)(f)))
      case Filter(conjunctions) => Filter(conjunctions.map(subjectFilter => subjectFilter._1 -> map(subjectFilter._2)(f)))
    }
  }
}

