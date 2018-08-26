package lensworkshop.lense

//The Lens is a generally useful abstraction from category theory that lets you
//descend deeply into a structure and replace part of it in a pure way.
//Its useful for many other things such as caching where you would use a Map
//as the underlying structure.
//From Edward Kmett talk at: https://vimeo.com/56063074
//And from another Lens talk by Edward Kmett: https://www.youtube.com/watch?v=efv0SQNde5Q

case class Lens[S, A](g: S => A, s: (A, S) => S) {
  def get(whole: S): A = g(whole)
  def set(part: A, whole: S): S = s(part, whole)
  def mod(f: A => A, whole: S): S = set(f(get(whole)), whole)
  def andThen[OTHERLENS](l: Lens[A, OTHERLENS]): Lens[S, OTHERLENS] = Lens[S, OTHERLENS](
    (whole: S) => l.get(get(whole)),
    (otherlens: OTHERLENS, whole: S) => mod(part => l.set(otherlens, part), whole)
  )
  def compose[OTHERLENS](that: Lens[OTHERLENS, S]): Lens[OTHERLENS, A] = that andThen this
}
