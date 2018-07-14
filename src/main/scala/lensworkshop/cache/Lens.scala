package lensworkshop.cache

//The Lens is a generally useful abstraction from category theory that lets you
//descend deeply into a structure and replace part of it in a pure way.
//Its useful for many other things such as caching where you would use a Map
//as the underlying structure.
//From Edward Kmett talk at: https://vimeo.com/56063074
//Aand from another Lens talk by Edward Kmett: https://www.youtube.com/watch?v=efv0SQNde5Q

case class Lens[A, B](g: A => B, s: (B, A) => A) {
  def get(a: A): B = g(a)
  def set(b: B, a: A): A = s(b, a)
  def mod(f: B => B, a: A): A = set(f(get(a)), a)
  def andThen[C](l: Lens[B, C]): Lens[A, C] = Lens[A, C](
    (a: A) => l.get(get(a)),
    (c: C, a: A) => mod(b => l.set(c, b), a)
  )
  def compose[C](that: Lens[C, A]): Lens[C, B] = that andThen this
}

object Lens {
  def member[K, V](k: K): Lens[Map[K, V], Option[V]] = Lens[Map[K, V], Option[V]](m => m get k, {
    case (Some(v), m) => m + (k -> v)
    case (None, m) => m - k
  })
}
