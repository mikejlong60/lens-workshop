package lensworkshop.cache

import lensworkshop.cache.Lens.member
import lensworkshop.model.{UserGroupKey, Users}

trait Cache[K, V] {
  def retrieve(k: K): V

  def replace(k: K, v: V): V
}

case class InMemoryCache(var cache: Map[UserGroupKey, Users], diasCache: Map[UserGroupKey, Users])
  extends Cache[UserGroupKey, Option[Users]] {

  //@volatile var threadVisibleCache = cache

  def retrieve(k: UserGroupKey): Option[Users] =
    member(k).get(cache).fold(diasCache.get(k).flatMap(users => replace(k, Some(users))))(users => Some(users))

  def replace(k: UserGroupKey, maybeUsers: Option[Users]): Option[Users] = {
    cache = member(k).set(maybeUsers, cache)
    maybeUsers
  }
}
