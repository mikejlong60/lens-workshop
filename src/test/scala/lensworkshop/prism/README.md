# Prisms - A Prism is an optic that you can use to select a piece of a sum type

An example of a sum type in Scala is Either, Try, Maybe or an instance of a sealed trait or an enum.  Sum types
have a fixed constant structure, not like lists where the number of elements can vary.  Sum types
are also called Coproducts.

Like Lenses, Prisms use two type parameters, one representing the whole`S` and one representing
 the part`A`have two type parameters generally called S and A: Prism[S, A] where S represents 
 the Sum and A a part of the Sum.  But they differ from Lenses in that they pertain to Sum types 
 instead of Product types.  In the case of a Prism, the thing you are looking for may not exist 
 whereas a Lens requires the thing to exist.  You might be looking for something in an ADT that
 is not there. 
 
 
 [Monocle Prism Docs](https://www.scala-exercises.org/monocle/prism)
 

