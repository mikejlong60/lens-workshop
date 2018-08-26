package lensworkshop.lense.cache

import lensworkshop.lense.Lens

object MapLens {
  def member[K, V](k: K): Lens[Map[K, V], Option[V]] = Lens[Map[K, V], Option[V]](m => m get k, {
    case (Some(v), m) => m + (k -> v)
    case (None, m) => m - k
  })
}
