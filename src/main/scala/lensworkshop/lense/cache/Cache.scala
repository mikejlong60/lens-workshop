package lensworkshop.lense.cache

import lensworkshop.lense.cache.MapLens.member

trait Cache[K, V] {
  def retrieve(k: K): V

  def replace(k: K, v: V): V
}

case class InMemoryCache(var cache: Map[UserGroupKey[String], Users[String]], cacheSource: Map[UserGroupKey[String], Users[String]])
  extends Cache[UserGroupKey[String], Option[Users[String]]] {

  def retrieve(k: UserGroupKey[String]): Option[Users[String]] =
    member(k).get(cache).fold(cacheSource.get(k).flatMap(users => replace(k, Some(users))))(users => Some(users))

  def replace(k: UserGroupKey[String], maybeUsers: Option[Users[String]]): Option[Users[String]] = {
    cache = member(k).set(maybeUsers, cache)
    maybeUsers
  }
}
