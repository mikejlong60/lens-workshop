package lensworkshop.casestudy.filterlanguage

//Thanks to: https://speakerdeck.com/danielasfregola/scalaworld-2017-a-pragmatic-introduction-to-category-theory

trait Applicative[Box[_]] extends Functor[Box] {

  def pure[A](a: A): Box[A]

  def ap[A, B](boxF: Box[A => B])(boxA: Box[A]): Box[B]

  override def map[A, B](boxA: Box[A])(f: A => B): Box[B] =
    ap[A, B](pure(f))(boxA)

}