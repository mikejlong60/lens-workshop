package lensworkshop.casestudy.filterlanguage

import lensworkshop.cache.Lens

trait AbstractFilter[+A]

case class PredicatePhrase[A](phrase: A) extends AbstractFilter[A]

case class PredicateDisjunction[A](predicates: List[AbstractFilter[A]]) extends AbstractFilter[A]

case class Filter[A](predicateConjunctions: Map[String, AbstractFilter[A]]) extends AbstractFilter[A]

object AbstractFilter {
  object Lenses {
//    val globalEventDotNotification = Lens[GlobalEvent[Notification], Notification](a => a.payload, (b, a) => a.copy(payload = b))
//    val notificationDotEvent = Lens[Notification, Event](a => a.event, (b, a) => a.copy(event = b))
//    val eventDotSource = Lens[Event, String](e => e.source, (b, a) => a.copy(source = b))
//    val eventDotObjectType = Lens[Event, String](e => e.objectType, (b, a) => a.copy(objectType = b))
//    val notificationEventDotStatus = Lens[NotificationEvent, Status](e => e.status, (b, a) => a.copy(status = b))
//    val statusDotRead = Lens[Status, Boolean](e => e.read, (b, a) => a.copy(read = b))
//    val globalEventDotSource: Lens[GlobalEvent[Notification], String] = globalEventDotNotification.andThen(notificationDotEvent).andThen(eventDotSource)
//    val notificationEventDotEvent = Lens[NotificationEvent, Event](a => a.event, (b, a) => a.copy(event = b))
//    val notificationEventDotSource = notificationEventDotEvent.andThen(eventDotSource)
//    val notificationEventDotRead = notificationEventDotStatus.andThen(statusDotRead)
//
//    val eventDotAttributes = Lens[Event, Map[String, String]](a => a.attributes, (b, a) => a.copy(attributes = b))
//
//    val notificationEventDotAttributes = notificationEventDotEvent.andThen(eventDotAttributes)
//    val notificationEventDotObjectType = notificationEventDotEvent.andThen(eventDotObjectType)
//
//    val notificationEventNotificationEventUI = Iso[NotificationEvent, NotificationEventUi](ne => {
//      NotificationEventUi(id = ne.id, userToken = ne.userToken, timestamp = ne.timestamp,
//        sink = ne.sink, title = ne.title, description = ne.description, acm = ne.acm, status = ne.status, event = ne.event, url = "")//TODO Fix this stupid URL thing
//    }
//    ) {
//      case ne => NotificationEvent(id = ne.id, userToken = ne.userToken, timestamp = ne.timestamp,
//        sink = ne.sink, title = ne.title, description = ne.description, acm = ne.acm, status = ne.status, event = ne.event)
//    }
//
  }



}