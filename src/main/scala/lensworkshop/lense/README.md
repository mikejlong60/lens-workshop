# Lenses – Laparoscopic Surgery for your Objects

This workshop will focus on practical problems that are elegantly solved using Lenses.  The workshop uses Scala and shows practical examples of how to make a Lens.  
You will learn how to make a Lens yourself (10 lines of Scala code) and will build several kinds of Lenses. This workshop purposely shies away from using a 3rd-party 
Lens library in order to facilitate a deeper understanding of how Lenses work. 

This workshop is not designed to be completed in the time we have here. Rather today we will complete the first module and then 
you should complete the other modules at your own pace, either at this conference or otherwise. 

This course consists of five sections which you should master in the following order:

## Course Modules
1. Lenses
1. Isomorphisms(Iso)
1. Prisms
1. Traversals
1. Folds 
1. Case Study - A Filter Language Compiler - Requires mastery of the Functor and Applicative Functor as well as all the Lense types above.

After today's session you can begin anywhere.  But you should make sure you have mastered the material that precedes, especially the Functor and Applicative Functor.  
These two ideas from Category Theory are what gives the various abstractions in the Monocle Lense library their composablity.  A Prism composes with a Traversal with an
Iso with a Lense ad infinitum. Finally the elusive promise of software reuseablity comes to fruition.


## Prerequisites
1. [JDK 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) 
1. [Git](https://git-scm.com/book/en/v2/Getting-Started-Installing-Git)
1. [SBT](https://www.scala-sbt.org/1.x/docs/Installing-sbt-on-Mac.html)
1. Working knowledge of Scala

## Syllabus
1. Things you can do with Lenses
    1. View the subpart
    1. Modify the whole by changing a subpart
    1. Compose a Lens with another Lens to go deeper
1. Practical applications of Lenses
    1. The deep-copy problem
    1. A cache built using Lenses – A Lens backed by a Map
1. Other important features 
    1. They are composable
    2. You can reason about them equationally.  If your Lens passes the Lens laws tests, it is correct
1. Lens Laws
    1. Get-Put – If you modify something by changing its subpart to exactly what it was before, then nothing happens.
    1. Put-Get – If you modify something by inserting a particular subpart,  then you get back exactly what you put.
    1. Put-Put – If you modify something by inserting a particular subpart and then modify it again,  its exactly as if you only made the second modification.
1. Prisms – examples from real life
1. Traversals – examples from real life
