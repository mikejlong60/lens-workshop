package lensworkshop.casestudy.filterlanguage


object FunctorInstances {

  val filterFunctor: Functor[AbstractFilter] = new Functor[AbstractFilter] {
    override def map[A, B](boxA: AbstractFilter[A])(f: A => B): AbstractFilter[B] = boxA match {
      case PredicatePhrase(p) => PredicatePhrase(f(p))
      case PredicateDisjunction(predicates) => {
        val result = predicates.map(predicate => map(predicate)(f))
        PredicateDisjunction(result)
      }
      case Filter(conjunctions) => {
        val result = conjunctions.map(subjectFilter => subjectFilter._1 -> map(subjectFilter._2)(f))
        Filter(result)
      }
    }
  }
}

