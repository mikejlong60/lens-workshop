package lensworkshop.casestudy.filterlanguage

case class TweetPredicateResult(event: Tweet, predicate: Boolean)

trait ConcreteKafkaFilter
case class AllDisjunctions(filter: TweetPredicateResult, disjunctionsFunctor: Map[String, List[TweetPredicateResult => TweetPredicateResult]]) extends ConcreteKafkaFilter
case class ComposedDisjunctions(filter: TweetPredicateResult, disjunctionsFunctor: Map[String, TweetPredicateResult => TweetPredicateResult]) extends ConcreteKafkaFilter

//object ConcreteKafkaFilter {
//  //TODO replace the Tuple2 with following case class
//  //  case class ConcreteFilter(key: String, predicateDisjunction: PredicateDisjunction)
//  //  val concreteFilter: ConcreteFilter => List[KafkaFilterParms => KafkaFilterParms] = subjectPredicatesPair => {
//  val concreteFilter: Tuple2[String, PredicateDisjunction] => Tuple2[String, List[TweetPredicateResult => TweetPredicateResult]] = subjectPredicatesPair => {
//    val subject = subjectPredicatesPair._1
//    val predicateDisjunction = subjectPredicatesPair._2
//    val pair: Tuple2[String, List[TweetPredicateResult => TweetPredicateResult]] = {
//      val predicates = predicateDisjunction.predicates.map { predicate =>
//
//        subject match {
//          case "sources" =>
//            (filter: TweetPredicateResult) => {
//              predicate.predicate match {
//                case "equals" => TweetPredicateResult(filter.event, filter.predicate || filter.event.event.source == predicate.argument)
//                case "notequals" => TweetPredicateResult(filter.event, filter.predicate || filter.event.event.source != predicate.argument)
//                case "contains" => TweetPredicateResult(filter.event, filter.predicate || filter.event.event.source.contains(predicate.argument))
//                case "notcontains" => TweetPredicateResult(filter.event, filter.predicate || !filter.event.event.source.contains(predicate.argument))
//                case "beginswith" => TweetPredicateResult(filter.event, filter.predicate || filter.event.event.source.startsWith(predicate.argument))
//                case "notbeginswith" => TweetPredicateResult(filter.event, filter.predicate || !filter.event.event.source.startsWith(predicate.argument))
//                case "endswith" => TweetPredicateResult(filter.event, filter.predicate || filter.event.event.source.endsWith(predicate.argument))
//                case "notendswith" => TweetPredicateResult(filter.event, filter.predicate || !filter.event.event.source.endsWith(predicate.argument))
//                case _ => {
//                  println(s"falling through with sources[${predicate.predicate}].  This means there is an invalid predicate.  Its a parsing bug.  Should not have happened.  Fix you parsing routine")
//                  TweetPredicateResult(filter.event, false)
//                }
//              }
//            }
//          case "objecttypes" =>
//            (filter: TweetPredicateResult) => {
//              predicate.predicate match {
//                case "equals" => TweetPredicateResult(filter.event, filter.predicate || filter.event.event.objectType == predicate.argument)
//                case "notequals" => TweetPredicateResult(filter.event, filter.predicate || filter.event.event.objectType != predicate.argument)
//                case "contains" => TweetPredicateResult(filter.event, filter.predicate || filter.event.event.objectType.contains(predicate.argument))
//                case "notcontains" => TweetPredicateResult(filter.event, filter.predicate || !filter.event.event.objectType.contains(predicate.argument))
//                case "beginswith" => TweetPredicateResult(filter.event, filter.predicate || filter.event.event.objectType.startsWith(predicate.argument))
//                case "notbeginswith" => TweetPredicateResult(filter.event, filter.predicate || !filter.event.event.objectType.startsWith(predicate.argument))
//                case "endswith" => TweetPredicateResult(filter.event, filter.predicate || filter.event.event.objectType.endsWith(predicate.argument))
//                case "notendswith" => TweetPredicateResult(filter.event, filter.predicate || !filter.event.event.objectType.endsWith(predicate.argument))
//                case _ => {
//                  println(s"falling through with objecttypes[${predicate.predicate}].  This means there is an invalid predicate.  Its a parsing bug.  Should not have happened.  Fix you parsing routine")
//                  TweetPredicateResult(filter.event, false)
//                }
//              }
//            }
//          case "read" =>
//            (filter: TweetPredicateResult) => {
//              predicate.predicate match {
//                case "equals" => TweetPredicateResult(filter.event, filter.predicate || filter.event.status.read == true)
//                case "notequals" => TweetPredicateResult(filter.event, filter.predicate || filter.event.status.read == false)
//                case _ => {
//                  println(s"falling through with read[${predicate.predicate}].  This means there is an invalid predicate.  Its a parsing bug.  Should not have happened.  Fix you parsing routine")
//                  TweetPredicateResult(filter.event, false)
//                }
//              }
//            }
//          case "id" =>
//            (filter: TweetPredicateResult) => {
//              predicate.predicate match {
//                case "equals" => TweetPredicateResult(filter.event, filter.predicate || filter.event.id == predicate.argument)
//                case _ => {
//                  println(s"falling through with id[${predicate.predicate}].  This means there is an invalid predicate.  Its a parsing bug.  Should not have happened.  Fix you parsing routine")
//                  TweetPredicateResult(filter.event, false)
//                }
//              }
//            }
//          case key if key startsWith "event.attributes." =>
//            val mapKey = key.substring((key.lastIndexOf(".")) + 1)
//            (filter: TweetPredicateResult) => {
//              val attribute = filter.event.event.attributes.get(mapKey)
//              attribute.fold(TweetPredicateResult(filter.event, false)) { attr =>
//                predicate.predicate match {
//                  case "equals" => TweetPredicateResult(filter.event, filter.predicate || attr == predicate.argument)
//                  case "notequals" => TweetPredicateResult(filter.event, filter.predicate || attr != predicate.argument)
//                  case "contains" => TweetPredicateResult(filter.event, filter.predicate || attr.contains(predicate.argument))
//                  case "notcontains" => TweetPredicateResult(filter.event, filter.predicate || !attr.contains(predicate.argument))
//                  case "beginswith" => TweetPredicateResult(filter.event, filter.predicate || attr.startsWith(predicate.argument))
//                  case "notbeginswith" => TweetPredicateResult(filter.event, filter.predicate || !attr.startsWith(predicate.argument))
//                  case "endswith" => TweetPredicateResult(filter.event, filter.predicate || attr.endsWith(predicate.argument))
//                  case "notendswith" => TweetPredicateResult(filter.event, filter.predicate || !attr.endsWith(predicate.argument))
//                  case _ => {
//                    println(s"falling through with event.attributes.$attr[${predicate.predicate}].  This means there is an invalid predicate.  Its a parsing bug.  Should not have happened.  Fix you parsing routine")
//                    TweetPredicateResult(filter.event, false)
//                  }
//                }
//              }
//            }
//          case badSubjectKey @ _ => (filter: TweetPredicateResult) => {
//            println(s"falling through with subject [$badSubjectKey].  This means there is an invalid subject in the list of keys.  Its a parsing bug.  Should not have happened.  Fix you parsing routine")
//            TweetPredicateResult(filter.event, false)
//          }
//        }
//      }
//      (subject, predicates)
//    }
//    pair
//  }
//
//  val falsePred = (filter: TweetPredicateResult) => TweetPredicateResult(filter.event, false)
//  val truePred = (filter: TweetPredicateResult) => TweetPredicateResult(filter.event, true)
//
//  val composeDisjunctions: AllDisjunctions => ComposedDisjunctions = initialFilterAndDisjunctions => {
//    val composedDisjunctions = initialFilterAndDisjunctions.disjunctionsFunctor.map(subjectDisjunctions =>
//      (subjectDisjunctions._1, subjectDisjunctions._2.foldLeft(falsePred)((accum, orPredicate) => accum andThen orPredicate)))
//    ComposedDisjunctions(initialFilterAndDisjunctions.filter, composedDisjunctions)
//  }
//
//  val applyDisjunctions: ComposedDisjunctions => Map[String, TweetPredicateResult] = initialFilterAndFunctions => initialFilterAndFunctions.disjunctionsFunctor.map(subjectPredicateExpression => (subjectPredicateExpression._1, subjectPredicateExpression._2.apply(initialFilterAndFunctions.filter)))
//
//  val combineDisjunctions: Map[String, TweetPredicateResult] => Boolean = appliedDisjunctions => appliedDisjunctions.foldLeft(true)((passed, pred) => passed && pred._2.predicate)
//
//  val bigCompoundPredicate = composeDisjunctions andThen applyDisjunctions andThen combineDisjunctions
//
//}