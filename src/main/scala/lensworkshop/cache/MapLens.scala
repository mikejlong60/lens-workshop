package lensworkshop.cache

//From Lens talk by Edward Kmett: https://www.youtube.com/watch?v=efv0SQNde5Q

case class Lens[S, A](g: S => A, s: (A, S) => S) {
  def get(a: S): A = g(a)

  def set(b: A, a: S): S = s(b, a)

  def mod(f: A => A, a: S): S = set(f(get(a)), a)

  def andThen[C](l: Lens[A, C]) = Lens[S, C](
    (a: S) => l.get(get(a)),
    (c: C, a: S) => mod(b => l.set(c, b), a)
  )

  def compose[C](that: Lens[C, S]) = that andThen this
}

object Lens {
  def member[K, V](k: K) = Lens[Map[K, V], Option[V]](m => m get k, {
    case (Some(v), m) => m + (k -> v)
    case (None, m) => m - k
  })
}
