# Lenses – Laparoscopic Surgery for your Objects

This workshop will focus on practical problems that are elegantly solved using Lenses.  The workshop uses Scala and shows practical examples of how to make a Lens.  
You will learn how to make a Lens yourself (10 lines of Scala code) and will build several kinds of Lenses. This workshop purposely shies away from using a 3rd-party 
Lens library in order to facilitate a deeper understanding of how Lenses work. 

This workshop is not designed to be completed in the time we have here. Rather today we will complete the first module and then 
you should complete the other modules at your own pace, either at this conference or otherwise. 

This course consists of six modules which you should master in the following order:

## Course Modules
1. [Lenses](./src/main/scala/lensworkshop/lense/README.md) - A Lens is an optic that lets you descend deeply into a Product structure (e.g. Tuple, Case class, HList, Map) 
                                                             and replace part of it in a pure way.
1. [Isomorphisms(Iso)](./src/test/scala/lensworkshop/iso/README.md)  - An Iso is an optic which converts elements of type S into elements of type A without loss
1. [Prisms](./src/test/scala/lensworkshop/prism/README.md) - A Prism is an optic that you can use to select a piece of a sum (co-product) type. Examples from Scala include a sealed trait, a Try, an Either, an Enum
1. [Traversals](./src/test/scala/lensworkshop/traversal/README.md)  - A Traversal is a type of optic that has 0 to n targets, allowing you to focus from a type S into 0 to n values of type A.
1. [Folds](./src/test/scala/lensworkshop/fold/README.md) - Traversables are also Folds. In combination with a Monoid they allow you to crush/reduce ... the elements in a traversal
1. [Case Study](./src/main/scala/lensworkshop/casestudy/filterlanguage/README.md) - This case study is an abstract filtering language. The requirement driving its design is that there is a data structure, a Tweet, 
                                                                                    that resides in many places,  in a relational database,  in a stream, in Cassandra, in an Elastic Search index, etc.  And 
                                                                                    users want to be able to filter out Tweets using the same query language regardless of their source.  
                                                                                    
After today's session on Lenses you can begin anywhere.  But try and gain some mastery of the material that precedes, especially the Functor and Applicative Functor.  
These two ideas from Category Theory are what gives the various abstractions in the Monocle Optics library their composability.  A Prism composes with a Traversal with an
Iso with a Lens ad infinitum. In my experience of the past 30 years reusability has been mostly an illusion until now.

The exercises are in the form of partially completed code.  The actual implementation of some of the code is missing but is in the `answers` branch if
you get stuck for longer than you can stand.  But remember that if you go that route you will fail the exam;)

## Prerequisites
1. [JDK 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) 
1. [Git](https://git-scm.com/book/en/v2/Getting-Started-Installing-Git)
1. [SBT](https://www.scala-sbt.org/1.x/docs/Installing-sbt-on-Mac.html)
1. Working knowledge of Scala
1. Knowledge of the Principals of Functional Programming is helpful, in particular Applicative Functors, Functors, 
and Abstract Data Types is essential to understanding Traversals and Folds. 
1. There are several papers and videos that you should study:
    1. [The Essence of the Iterator Pattern](https://www.cs.ox.ac.uk/jeremy.gibbons/publications/iterator.pdf)
    1. [Daniela Sfregola - A Pragmatic Introduction to Category Theory](https://speakerdeck.com/danielasfregola/scalaworld-2017-a-pragmatic-introduction-to-category-theory)
    1. [Edward Kmett](https://vimeo.com/56063074) provides the basis of the Monocle Lens library.
    1. [Bartosz Milewski - A Crash Course in Category Theory](https://www.youtube.com/watch?v=JH_Ou17_zyU)

