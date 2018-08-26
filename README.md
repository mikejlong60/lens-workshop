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
1. Case Study - A Filter Language Compiler - Requires mastery of the Functor and Applicative Functor as well as all the Lens types above.

After today's session on Lenses you can begin anywhere.  But you should make sure you have mastered the material that precedes, especially the Functor and Applicative Functor.  
These two ideas from Category Theory are what gives the various abstractions in the Monocle Lense library their composablity.  A Prism composes with a Traversal with an
Iso with a Lense ad infinitum. Finally the elusive promise of software reuseablity comes to fruition.

The exercises are in the form of partially completed tests.  The actual implementation of these test is in the `answers` branch if
you get stuck for longer than you can stand.  But remember that if you go that route you will fail the exam;)


## Prerequisites
1. [JDK 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) 
1. [Git](https://git-scm.com/book/en/v2/Getting-Started-Installing-Git)
1. [SBT](https://www.scala-sbt.org/1.x/docs/Installing-sbt-on-Mac.html)
1. Working knowledge of Scala



TODO
  1. Completed
    1. Introduction
    1. Lens
    1. Iso
    1. Traversal
    
  1. Not Complete
    1. Prism
    1. Folds
    1. Case Study -  Once you have figured out how to fold over your traversal in tFilterOutTheTweet 
      1. In the README for this write about the importance of understanding Functors and Applicative Functors, referencing
      the `Essense of the Iterator Pattern` and the presentation/simple Category Theory library by Daniella Sfergola. 