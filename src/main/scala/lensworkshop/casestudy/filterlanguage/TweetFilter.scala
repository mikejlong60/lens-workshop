package lensworkshop.casestudy.filterlanguage

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
  val filterItOut: AbstractFilter[Boolean] => Boolean = result => result.asInstanceOf[Filter[Boolean]]
    .predicateConjunctions.map(subjectAndCompoundPredicate => subjectAndCompoundPredicate._2).
    map(predicateDisjunction => predicateDisjunction.asInstanceOf[PredicateDisjunction[Boolean]]
      .predicates.exists(predicatePhrase => predicatePhrase.asInstanceOf[PredicatePhrase[Boolean]].phrase == true))
    .exists(subjectPredicate => subjectPredicate == false)

}