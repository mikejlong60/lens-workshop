package lensworkshop.casestudy.filterlanguage

import org.scalacheck.Gen
import org.scalatest.prop.PropertyChecks
import org.scalatest.{Matchers, PropSpec}
import QueryLanguageParser._

class QueryLanguageParserTest extends PropSpec with PropertyChecks with Matchers {

  property("parse and validate filter language expression") {
    val query = "author[equals]fred;subject[equals]joe;subject[equals]simon;body[contains]guitars"
    val result = tokenize(query).map(parseAndValidate)
    result.exists(parsedAndValidated => (parsedAndValidated._2.isDefined)) should be(false)
  }

  property("parse an empty query") {
    val result = tokenize("").map(parseAndValidate)
    val numberOfErrors = result.foldLeft(0)((accum, parsedAndValidated) => if (parsedAndValidated._2.isDefined) accum + 1 else accum)
    numberOfErrors should be(0)
  }

  property("parse bad list element into parts") {
    val query = "author[quals]123;sourc[equals]fred;author[ddd]123"
    val result = tokenize(query).map(parseAndValidate)
    val numberOfErrors = result.foldLeft(0)((accum, parsedAndValidated) => if (parsedAndValidated._2.isDefined) accum + 1 else accum)
    numberOfErrors should be(3)
  }

  property("Ensure that your parser won't blow up with untokenizable strings") {
    forAll(Gen.alphaStr) { query =>
      tokenize(query).map(parseAndValidate)
    }
  }

  property("Make a Map of subject -> predicates from the list of valid triples") {
    val query = "author[equals]fred;author[equals]joe;subject[notequals]fred;subject[contains]guitar;body[contains]guitar"
    val triplesWithMaybeErrors = tokenize(query).map(parseAndValidate)
    val actual = triplesWithMaybeErrors.map(validTriples => validTriples._1).foldLeft(Map.empty[String, List[PredicatePhrase[(String, String, String)]]])((accum, triple) => accum + {
      val currentDisjunction = accum.getOrElse(triple._1, List.empty[PredicatePhrase[(String, String, String)]])
      triple._1 -> (PredicatePhrase(triple._1, triple._2, triple._3) :: currentDisjunction)
    })

    val expected = Map(
      "author" -> List(PredicatePhrase("author", "equals", "joe"), PredicatePhrase("author", "equals", "fred")),
      "subject" -> List(PredicatePhrase("subject", "contains", "guitar"), PredicatePhrase("subject", "notequals", "fred")),
      "body" -> List(PredicatePhrase("body", "contains", "guitar")))
    actual should be(expected)
  }

  property("Turn the query into a Filter") {
    val query = "author[equals]fred;author[equals]joe;subject[notequals]fred;subject[contains]guitar;body[contains]guitar"
    val triplesWithMaybeErrors = tokenize(query).map(parseAndValidate)
    val actual = makeFilter(triplesWithMaybeErrors)
    val expectedConjunctions = Map(
      "author" -> PredicateDisjunction(List(PredicatePhrase("author", "equals", "joe"), PredicatePhrase("author", "equals", "fred"))),
      "subject" -> PredicateDisjunction(List(PredicatePhrase("subject", "contains", "guitar"), PredicatePhrase("subject", "notequals", "fred"))),
      "body" -> PredicateDisjunction(List(PredicatePhrase("body", "contains", "guitar"))))
    actual should be(Right(Filter(predicateConjunctions = expectedConjunctions)))
  }

  property("Ensure that you can parse any string and you won't blow up with untokenizable strings") {
    forAll(Gen.alphaStr) { query =>
      query.size > 0
      val triplesWithMaybeErrors = tokenize(query).map(parseAndValidate)
      val actual = makeFilter(triplesWithMaybeErrors)
      actual shouldBe an[Either[List[String], Filter[(String, String, String)]]]
    }
  }
}
