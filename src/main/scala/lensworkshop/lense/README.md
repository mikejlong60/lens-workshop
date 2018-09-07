# Lenses – A Simple Implementation

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
    2. You can reason about them equationally.  If your Lens passes the Lens laws tests, it is correct
1. Lens Laws
    1. Get-Put – If you modify something by changing its subpart to exactly what it was before, then nothing happens.
    1. Put-Get – If you modify something by inserting a particular subpart,  then you get back exactly what you put.
    1. Put-Put – If you modify something by inserting a particular subpart and then modify it again,  its exactly as if you only made the second modification.
1. Lens Cache Example
    1. Sometimes a Lens does not need to be completely well-behaved to be useful.  In this case I have
    used a Lens backed by a map to implement a completely non-blocking, distributable  cache.  Why is this Lens not well-behaved?
    Is this a bug or is it OK?  
    
    
    
  [Monocle Lens Docs](https://www.scala-exercises.org/monocle/lens)
