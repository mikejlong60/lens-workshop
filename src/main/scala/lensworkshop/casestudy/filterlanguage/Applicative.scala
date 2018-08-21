package lensworkshop.casestudy.filterlanguage

//Thanks to: https://speakerdeck.com/danielasfregola/scalaworld-2017-a-pragmatic-introduction-to-category-theory

trait Applicative[Box[_]] extends Functor[Box] {

  def pure[A](a: A): Box[A]

  def ap[A, B](boxF: Box[A => B])(boxA: Box[A]): Box[B]

  override def map[A, B](boxA: Box[A])(f: A => B): Box[B] =
    ap[A, B](pure(f))(boxA)

}

import FunctorInstances.filterFunctor

object ApplicativeInstances {

  val filterApplicative: Applicative[AbstractFilter] = new Applicative[AbstractFilter] {
    override def pure[A](a: A): AbstractFilter[A] = PredicatePhrase[A](a)

    override def ap[A, B](boxF: AbstractFilter[A => B])(boxA: AbstractFilter[A]): AbstractFilter[B] = (boxF, boxA) match {
      case (PredicatePhrase(f), PredicatePhrase(a)) => pure(f(a))
      case (PredicateDisjunction(predicatePhrases), p @ PredicatePhrase(_)) => PredicateDisjunction[B](predicatePhrases.map(function => ap(function)(p)))
      case (Filter(predicateConjunctions), p @ PredicatePhrase(_)) => Filter[B](predicateConjunctions.map(pair => {
        val (subject, predicateDisjunction) = pair
        subject -> ap(predicateDisjunction)(p)
      }))
      case (PredicatePhrase(f), filter @ Filter(_)) => filterFunctor.map(filter)(f)
      case (PredicatePhrase(f), dis @ PredicateDisjunction(_)) => filterFunctor.map(dis)(f)
      case _ => Filter(predicateConjunctions = Map.empty[String, AbstractFilter[B]])
    }
  }

}
