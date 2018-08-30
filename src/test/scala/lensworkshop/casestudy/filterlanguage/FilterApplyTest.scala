package lensworkshop.casestudy.filterlanguage

import lensworkshop.casestudy.filterlanguage.QueryLanguageParser.{makeFilter, parseAndValidate, tokenize}
import org.scalatest.{Matchers, PropSpec}
import org.scalatest.prop.PropertyChecks

class FilterApplyTest extends PropSpec with PropertyChecks with Matchers {

  import ApplicativeInstances.filterApplicative._
  import TweetFilter._


  //TODO Comment out the lines that test the outcome of the fold version of the query result interpreter.

  val filter = Filter(predicateConjunctions = Map(
    "author" -> PredicateDisjunction(predicates = List(
      PredicatePhrase(phrase = (t: Tweet) => t.author == "mike"),
      PredicatePhrase(phrase = (t: Tweet) => t.author.contains("mike")),
        PredicatePhrase(phrase = (t: Tweet) => t.author.contains("mike")),
      PredicatePhrase(phrase = (t: Tweet) => t.author.contains("mike"))
    )),
    "subject" -> PredicateDisjunction(predicates = List(PredicatePhrase(phrase = (t: Tweet) => t.subject.contains("Guitar")))),
    "body" -> PredicateDisjunction(predicates = List(PredicatePhrase(phrase = (t: Tweet) => t.body.contains("strings")), PredicatePhrase(phrase = (t: Tweet) => t.body.contains("xxxds")))),
    "timestamp" -> PredicateDisjunction(predicates = List(PredicatePhrase(phrase = (t: Tweet) => t.timestamp <= System.currentTimeMillis())))
  ))

  property("Apply the filter to a matching tweet") {
    val tweet = Tweet(author = "mike long", timestamp = System.currentTimeMillis() - 10000, subject = "Guitar Strings", body = "Luthier guitar strings are really great.")

    val expected = Filter(predicateConjunctions = Map(
      "author" -> PredicateDisjunction(predicates = List(PredicatePhrase(false), PredicatePhrase(true), PredicatePhrase(true), PredicatePhrase(true))),
      "subject" -> PredicateDisjunction(predicates = List(PredicatePhrase(true))),
      "body" -> PredicateDisjunction(predicates = List(PredicatePhrase(true), PredicatePhrase(false))),
      "timestamp" -> PredicateDisjunction(predicates = List(PredicatePhrase(true)))
    ))

    val result = ap(filter)(pure(tweet))
    filterOutTheTweet(result) should be(false)
    result should be(expected)
    val traverseMonoidWay = fold.fold(result.asInstanceOf[Filter[Boolean]])(moid)
    traverseMonoidWay should be (List(PredicatePhrase(true)))
  }

  property("Apply the filter to a non-matching tweet") {
    val tweet = Tweet(author = "bad author name", timestamp = System.currentTimeMillis() - 10000, subject = "Guitar Strings", body = "Luthier guitar strings are really great.")
    val result = ap(filter)(pure(tweet))

    filterOutTheTweet(result) should be(true)
    val traverseMonoidWay = fold.fold(result.asInstanceOf[Filter[Boolean]])(moid)
    traverseMonoidWay should be (List(PredicatePhrase(false)))
  }

  property("Generate the Filter from the query language string and then apply it to a Tweet") {
    val tweet = Tweet(author = "mike long", timestamp = System.currentTimeMillis() - 10000, subject = "Guitar Strings", body = "Luthier guitar strings are really great.")
    val query = "author[equals]mike long;author[equals]joe;subject[contains]Strings;subject[contains]guitar;body[contains]guitar"
    val triplesWithMaybeErrors = tokenize(query).map(parseAndValidate)
    val filterSourceCodeAndErrors = makeFilter(triplesWithMaybeErrors)

    val filterSourceCode = filterSourceCodeAndErrors.right.get
    val compiledFilter = FunctorInstances.filterFunctor.map(filterSourceCode)(tweetFilter)
    val result = ap(compiledFilter)(pure(tweet))
    filterOutTheTweet(result) should be(false)
    val traverseMonoidWay = fold.fold(result.asInstanceOf[Filter[Boolean]])(moid)
    traverseMonoidWay should be (List(PredicatePhrase(true)))

    val tweet2 = Tweet(author = "denise long", timestamp = System.currentTimeMillis() - 10000, subject = "Guitar Strings", body = "Luthier guitar strings are really great.")
    val filter2 = ap(compiledFilter)(pure(tweet2))

    filterOutTheTweet(filter2) should be(true)
    val traverseMonoidWay2 = fold.fold(filter2.asInstanceOf[Filter[Boolean]])(moid)
    traverseMonoidWay2 should be (List(PredicatePhrase(false)))
  }
}
