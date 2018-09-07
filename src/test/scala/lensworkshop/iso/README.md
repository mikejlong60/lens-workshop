# Isomorphism(Iso) – Using the Monocle library

An Iso is an optic which converts elements of type S into elements of type A without loss.  Like all types in the 
Monocle library you can compose Isos with any other Lens type using `composeIso`.  

 [Monocle Iso Docs](https://www.scala-exercises.org/monocle/iso)

## Iso Laws
  1. get - Where types A and B are Isomorphic, this law verifies that you can convert any A into a B without loss 
  1. reverse-get -  Where the same types A and B are Isomorphic, this law verifies that you can convert from any B to any A without loss.
  
