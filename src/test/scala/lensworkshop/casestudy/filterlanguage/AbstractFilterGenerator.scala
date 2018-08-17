package lensworkshop.casestudy.filterlanguage

import org.scalacheck.Gen

object AbstractFilterGenerator {

  def genPredicate(subject: String) = for {
    predicate <- Gen.oneOf(List("equals", "notequals", "contains", "notcontains", "endswith", "notendswith", "beginswith", "notbeginswith"))
    argument <- Gen.alphaStr
  } yield PredicatePhrase(phrase = (subject, predicate, argument))

  def genPredicateDisjunction(subject: String) = for {
    predicateDisjunction <- Gen.listOf(genPredicate(subject))
  } yield PredicateDisjunction(predicates = predicateDisjunction)

  val nonAttr = List("read", "sources", "id", "objecttypes")
  def genFilter = for {
    attributes <- Gen.nonEmptyListOf(Gen.alphaStr).map(attrs => attrs.map(attr => s"event.attributes.$attr"))
    subjects <- Gen.listOf(Gen.oneOf(nonAttr ++ attributes))
  } yield subjects.foldLeft(Map.empty[String, PredicateDisjunction[(String, String, String)]])((accum, subject) => {
    accum + (subject -> genPredicateDisjunction(subject).sample.get)
  })
}
