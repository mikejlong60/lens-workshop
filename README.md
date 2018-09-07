# Lenses – Laparoscopic Surgery for your Objects

This workshop will focus on practical problems that are elegantly solved using Lenses.  The workshop uses Scala and shows practical examples of how to make a Lens.  
You will learn how to make a Lens yourself (10 lines of Scala code) and will build several kinds of Lenses. This workshop purposely shies away from using a 3rd-party 
Lens library in order to facilitate a deeper understanding of how Lenses work. 

This workshop is not designed to be completed in the time we have here. Rather today we will complete the first module and then 
you should complete the other modules at your own pace, either at this conference or otherwise. 

This course consists of six modules which you should master in the following order:

## Course Modules
1. [Lenses](./src/main/scala/lensworkshop/lense/README.md)
1. [Isomorphisms(Iso)](./src/test/scala/lensworkshop/iso/README.md)
1. [Prisms](./src/test/scala/lensworkshop/prism/README.md)
1. [Traversals](./src/test/scala/lensworkshop/traversal/README.md)
1. [Folds](./src/test/scala/lensworkshop/fold/README.md)
1. [Case Study](./src/main/scala/lensworkshop/casestudy/filterlanguage/README.md) - A Filter Language Compiler - Requires mastery of the Functor and Applicative Functor as well as all the Lens types above.

After today's session on Lenses you can begin anywhere.  But try and gain some mastery of the material that precedes, especially the Functor and Applicative Functor.  
These two ideas from Category Theory are what gives the various abstractions in the Monocle Optics library their composability.  A Prism composes with a Traversal with an
Iso with a Lense ad infinitum. In my experience of the past 30 years reusability has been mostly an illusion until now.

The exercises are in the form of partially completed tests.  The actual implementation of these test is in the `answers` branch if
you get stuck for longer than you can stand.  But remember that if you go that route you will fail the exam;)

## Prerequisites
1. [JDK 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) 
1. [Git](https://git-scm.com/book/en/v2/Getting-Started-Installing-Git)
1. [SBT](https://www.scala-sbt.org/1.x/docs/Installing-sbt-on-Mac.html)
1. Working knowledge of Scala
1. Knowledge of the Principals of Functional Programming is helpful, in particluar Applicative Functors, Functors, 
and Abstract Data Types is essential to understanding Traversals and Folds. 
1. There are several papers and videos that you should study:
    1. [The Essence of the Iterator Pattern](https://www.cs.ox.ac.uk/jeremy.gibbons/publications/iterator.pdf)
    1. [Daniela Sfregola - A Pragmatic Introduction to Category Theory](https://speakerdeck.com/danielasfregola/scalaworld-2017-a-pragmatic-introduction-to-category-theory)
    1. [Edward Kmett](https://vimeo.com/56063074) provides the basis of the Monocle Lens library.


## Lens - A Simple Lense implementation
A Lens is a construct that lets you descend deeply into a  Product structure (e.g. Tuple, Case class, HList, Map) 
and replace part of it in a pure way. Lenses have two type parameters generally called S and A: Lens[S, A] where S 
represents the Product and A an element inside of S. 

1. Things you can do with Lenses
    1. View the sub-part
    1. Modify the whole by changing a sub-part
    1. Compose a Lens with another Lens to go deeper

1. Practical applications of Lenses
    1. The deep-copy problem -- This is the canonical Lens example that you will see in the literature. Recall that as 
    functional programmers we can only have pure functions.  So we cannot mutate anything.  And in the act of mutating it 
    we make a copy of it so as not to disturb the original.  However, consider how irritating it is to change something in 
    a deeply nested structure.  See example in DeepCopyLensTest.
    
1. Other important features 
    1. They are composable
    1. You can reason about them equationally.  If your Lens passes the Lens laws tests, it is correct
1. Lens Laws
    1. Get-Put – If you modify something by changing its subpart to exactly what it was before, then nothing happens.
    1. Put-Get – If you modify something by inserting a particular subpart,  then you get back exactly what you put.
    1. Put-Put – If you modify something by inserting a particular subpart and then modify it again,  its exactly as if you only made the second modification.
1. Lens Cache Example
    1. Sometimes a Lens does not need to be completely well-behaved to be useful.  In this case I have
    used a Lens backed by a map to implement a completely non-blocking, distributable  cache.  Why is this Lens not well-behaved?
    Is this a bug or is it OK?  

```
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
```
   
