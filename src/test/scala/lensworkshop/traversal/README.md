# Traversal – Using the Monocle library

A Traversal is a type of lens that has 0 to n targets, allowing you to focus from a type S into 0 to n values of type A.
The canonical example of a Traversal allows you to focus into all the elements inside a container such as a List, Vector, Option, or Map.
A Traversal is also a fold when used in conjunction with its corresponding Monoidal value.

TODO -- Reference the Monocle docs here. 

## Traversal Laws
  1. Modify - Get All -  Verifies that you can modify all elements targeted by the Traversal.
  1. Compose Modify or Fusion  - This law states that you can compose two functions `f` and `g` across a Traversal `t` over a list
  `t` in any order as shown here:
  
    
          val l = t.modify(g)(t.modify(f)(s))
          val r = t.modify(g compose f)(s)
          l == r
    
