package lensworkshop.casestudy.filterlanguage

trait AbstractFilter[+A]

case class PredicatePhrase[A](phrase: A) extends AbstractFilter[A]

case class PredicateDisjunction[A](predicates: List[AbstractFilter[A]]) extends AbstractFilter[A]

case class Filter[A](predicateConjunctions: Map[String, AbstractFilter[A]]) extends AbstractFilter[A]

