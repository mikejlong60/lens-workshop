# Lenses – A Simple Implementation

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
1. Lens Cache Example
    1. 