# Traversal – Using the Monocle library

A Traversal is a type of lens that has 0 to n targets, allowing you to focus from a type S into 0 to n values of type A.
The canonical example of a Traversal allows you to focus into all the elements inside a container such as a List, Vector, Option, or Map.
A Traversal is also a fold when used in conjunction with its corresponding Monoidal value.

[Monocle Traversal Docs](https://www.scala-exercises.org/monocle/traversal)

## Traversal Laws
  1. Traverse applied the to identity functor is just fmap.  
  1. The fusion law - You can compose two traversals into one. traverse (f . g) = traverse f . traverse g 
  1. Purity law - traverse pure = pure
  1. Modify - Get All -  Verifies that you can modify all elements targeted by the Traversal.

## Other Important Facts about Traversals

  1. Traversals do not allow you to skip elements.  They preserve shape. You can collapse things (see fold/crush) but you 
  cannot skip elements.
  1. Applicative Functors in the context of Traversals have both monadic and monoidal properties.  The improved composability
   of Applicative Functors makes them more useful for traversals than Monads. 
