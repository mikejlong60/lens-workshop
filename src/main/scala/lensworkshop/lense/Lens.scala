package lensworkshop.lense

//The Lens is a generally useful abstraction from category theory that lets you
//descend deeply into a structure and replace part of it in a pure way.
//Its useful for many other things such as caching where you would use a Map
//as the underlying structure.
//From Edward Kmett talk at: https://vimeo.com/56063074
//And from another Lens talk by Edward Kmett: https://www.youtube.com/watch?v=efv0SQNde5Q

case class Lens[WHOLE, PART](g: WHOLE => PART, s: (PART, WHOLE) => WHOLE) {
  def get(whole: WHOLE): PART = g(whole)
  def set(part: PART, whole: WHOLE): WHOLE = s(part, whole)
  def mod(f: PART => PART, whole: WHOLE): WHOLE = set(f(get(whole)), whole)
  def andThen[OTHERLENS](l: Lens[PART, OTHERLENS]): Lens[WHOLE, OTHERLENS] = Lens[WHOLE, OTHERLENS](
    (whole: WHOLE) => l.get(get(whole)),
    (otherlens: OTHERLENS, whole: WHOLE) => mod(part => l.set(otherlens, part), whole)
  )
  def compose[OTHERLENS](that: Lens[OTHERLENS, WHOLE]): Lens[OTHERLENS, PART] = that andThen this
}

object Lens {
  def member[K, V](k: K): Lens[Map[K, V], Option[V]] = Lens[Map[K, V], Option[V]](m => m get k, {
    case (Some(v), m) => m + (k -> v)
    case (None, m) => m - k
  })
}
