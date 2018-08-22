package lensworkshop.casestudy.filterlanguage

import lensworkshop.casestudy.filterlanguage
import monocle.{Iso, Lens, PTraversal, Traversal}
import scalaz.Monoid

object TweetFilter {
  val tweetFilter: Tuple3[String, String, String] => (Tweet => Boolean) = predicatePhrase => {
    val (subject, predicate, value) = predicatePhrase
    subject match {
      case "author" =>
        (t: Tweet) => {
          predicate match {
            case "equals" => t.author == value
            case "notequals" => t.author != value
            case "contains" => t.author.contains(value)
            case "notcontains" => !t.author.contains(value)
            case "beginswith" => t.author.startsWith(value)
            case "notbeginswith" => !t.author.startsWith(value)
            case "endswith" => t.author.endsWith(value)
            case "notendswith" => !t.author.endsWith(value)
            case _ => {
              println(s"falling through with author[${predicate}].  This means there is an invalid predicate.  Its a parsing bug.  Should not have happened.  Fix you parsing routine")
              false
            }
          }
        }
      case "subject" =>
        (t: Tweet) => {
          predicate match {
            case "equals" => t.subject == value
            case "notequals" => t.subject != value
            case "contains" => t.subject.contains(value)
            case "notcontains" => !t.subject.contains(value)
            case "beginswith" => t.subject.startsWith(value)
            case "notbeginswith" => !t.subject.startsWith(value)
            case "endswith" => t.subject.endsWith(value)
            case "notendswith" => !t.subject.endsWith(value)
            case _ => {
              println(s"falling through with subject[${predicate}].  This means there is an invalid predicate.  Its a parsing bug.  Should not have happened.  Fix you parsing routine")
              false
            }
          }
        }
      case "body" =>
        (t: Tweet) => {
          predicate match {
            case "equals" => t.body == value
            case "notequals" => t.body != value
            case "contains" => t.body.contains(value)
            case "notcontains" => !t.body.contains(value)
            case "beginswith" => t.body.startsWith(value)
            case "notbeginswith" => !t.body.startsWith(value)
            case "endswith" => t.body.endsWith(value)
            case "notendswith" => !t.body.endsWith(value)
            case _ => {
              println(s"falling through with body[${predicate}].  This means there is an invalid predicate.  Its a parsing bug.  Should not have happened.  Fix you parsing routine")
              false
            }
          }
        }
      case badSubjectKey @ _ => (t: Tweet) => {
        println(s"falling through with subject [$badSubjectKey].  This means there is an invalid subject in the list of keys.  Its a parsing bug.  Should not have happened.  Fix you parsing routine")
        false
      }
    }
  }

  //TODO Make this function a Monocle Traversal/Fold
  val filterOutTheTweet: AbstractFilter[Boolean] => Boolean = result => result.asInstanceOf[Filter[Boolean]]
    .predicateConjunctions.map(subjectAndCompoundPredicate => subjectAndCompoundPredicate._2).
    map(predicateDisjunction => predicateDisjunction.asInstanceOf[PredicateDisjunction[Boolean]]
      .predicates.exists(predicatePhrase => predicatePhrase.asInstanceOf[PredicatePhrase[Boolean]].phrase == true))
    .exists(subjectPredicate => subjectPredicate == false)

  import scalaz.std.list._ // to get the Traverse instance for List

  val tFilterToBooleans: PTraversal[Filter[Boolean], Filter[Boolean], List[AbstractFilter[Boolean]], List[AbstractFilter[Boolean]]] =
    Lens[Filter[Boolean], Map[String, AbstractFilter[Boolean]]](whole => whole.asInstanceOf[Filter[Boolean]].predicateConjunctions)(part => whole => whole.copy(predicateConjunctions = part)) composeIso
    Iso[Map[String, AbstractFilter[Boolean]], List[(String, AbstractFilter[Boolean])]](conjunctions => conjunctions.toList) { conjunctions =>
      conjunctions.foldLeft(Map.empty[String, AbstractFilter[Boolean]])((accum, fff) => {
        val (subject, disjunctions) = fff
        accum + (subject -> disjunctions)
      })
    } composeTraversal
    Traversal.fromTraverse[List, (String, AbstractFilter[Boolean])] composeLens
    Lens[(String, AbstractFilter[Boolean]), List[AbstractFilter[Boolean]]](whole => whole._2.asInstanceOf[PredicateDisjunction[Boolean]].predicates.asInstanceOf[List[PredicatePhrase[Boolean]]])(part => whole => (whole._1, PredicateDisjunction(part)))

  //TODO The following would be better as a Fold on the traversal
  def filterOutTheTweet2(filter: Filter[Boolean]) = tFilterToBooleans.getAll(filter).foldLeft(List.empty[Boolean])((accum, as) => {
    val predPhraseList = as.asInstanceOf[List[PredicatePhrase[Boolean]]]
    val oneTrue = predPhraseList.exists(pred => pred.phrase == true)
    oneTrue :: accum
  }).exists(p => p == false)
}