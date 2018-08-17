package lensworkshop.casestudy.filterlanguage

import lensworkshop.casestudy.filterlanguage.FunctorInstances.filterFunctor

object ApplicativeInstances {

  val filterApplicative: Applicative[AbstractFilter] = new Applicative[AbstractFilter] {
    override def pure[A](a: A): AbstractFilter[A] = PredicatePhrase[A](a)

    override def ap[A, B](boxF: AbstractFilter[A => B])(boxA: AbstractFilter[A]): AbstractFilter[B] = (boxF, boxA) match {
      case (PredicatePhrase(f), PredicatePhrase(a)) => pure(f(a))
//      case (PredicatePhrase(f), filter @ Filter(p)) => map(filter)(f)
      case (PredicatePhrase(f), filter @ Filter(p)) => filterFunctor.map(filter)(f)
      case _ => Filter(predicateConjunctions = Map.empty[String, AbstractFilter[B]])
    }
  }
}