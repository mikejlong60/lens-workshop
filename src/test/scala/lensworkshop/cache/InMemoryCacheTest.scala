package lensworkshop.cache

import lensworkshop.model.{UserGroupKey, Users}
import org.scalatest.prop.PropertyChecks
import org.scalatest.{Matchers, _}

class InMemoryCacheTest extends PropSpec with PropertyChecks with Matchers {

  property("Retrieve from Cache. First it looks in cache, then in the source of the cache.  If found in source of the cache " +
    "it adds it to cache. Then it returns the Users object.  Otherwise it " +
    "returns None.") {
    forAll(Generator.genOverlappingGroupMaps, workers(4)) { rawData: (Map[UserGroupKey, Users], Map[UserGroupKey, Users], UserGroupKey) =>
      val cache = InMemoryCache(cache = rawData._1, diasCache = rawData._2)
      val key = rawData._3
      val a = cache.retrieve(key)

      val v = rawData._1.get(key).fold(rawData._2.get(key))(v => Some(v))
      v match {
        case None => a should be(empty)
        case Some(d) => a should be(Some(d))
      }
    }
  }

  property("Delete from Cache. After that the cache will look in the source of the cache and return " +
    "what is there if it exists and update the cache accordingly.  " +
    "Otherwise it will just return what's in cache.") {
    forAll(Generator.genOverlappingGroupMaps, workers(4)) { rawData: (Map[UserGroupKey, Users], Map[UserGroupKey, Users], UserGroupKey) =>
      val cache = InMemoryCache(cache = rawData._1, diasCache = rawData._2)
      val p = rawData._1
      val key = p.head._1
      p should contain(key -> p.head._2)
      val a = cache.replace(key, None)
      a should be(empty)
      val a2 = cache.retrieve(key)
      rawData._2.get(key) match {
        case None => a2 should be(empty)
        case Some(d) => a2 should be(Some(d))
      }
    }
  }

  property("Replace in Cache.  Cache always wins. The mechanism will not replace what's in cache with whats in the source of the cache. " +
    "The business rule says that replace does not look in the source of the cache.   It assumes the caller knows " +
    "what he is doing.") {
    forAll(Generator.genOverlappingGroupMaps, workers(4)) { rawData: (Map[UserGroupKey, Users], Map[UserGroupKey, Users], UserGroupKey) =>
      val cache = InMemoryCache(cache = rawData._1, diasCache = rawData._2)
      val p = rawData._1
      val key = p.head._1
      p should contain(key -> p.head._2)
      val newV = Some(Users(startTime = System.currentTimeMillis(), timeToLive = 10000, userTokens = List("I like to fish")))
      val a = cache.replace(key, newV)
      a should be(newV)
      cache.retrieve(key) should be(newV)
    }
  }
}
