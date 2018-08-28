# Case Study – A Filtering Language
 
## Case Study Requirements

This case study is an abstract filtering language. The requirement driving its design is that there is a data structure, a Tweet, 
that resides in many places,  in a relational database,  in a stream, in Cassandra, in an Elastic Search index, etc.  And 
users want to be able to filter out tweets using the same query language regardless of their source.  Of course we want as much reuse as possible.  

There is an Abstract Data Type(ADT) called `AbstractFilter` that describes a filter in terms of `ands` and `ors`.  It has three concrete
types:
  1. `Filter`
  1. `PredicateDisjunction`
  1. `PredicatePhrase`
  

There is also a Functor and Applicative Functor for the `AbstractFilter`. 


A Filter contains a Map of named PredicateDisjunctions(`ors`) that are joined together with an `and` to determine whether or not
a Tweet meets the filter criteria.

There is a text-based language with which the user or API interacts with the following BNF grammar:

```
<filter> ::= <filter-expression> <expression-separator> <filter-expression>*
<expression-separator> ::= "~"
<filter-expression> ::= <subject-part> <[predicate-part]> <value>
<subject-part> ::= "author" | "subject" | "body"
<predicate-part> ::= "equals" | "notequals" | "beginswith" | "notbeginswith" | "contains" | "notcontains" | "endswith" | "notendswith" 
```

Where there are repeated `<subject-part>` entries with the same value they are `ored` together.   And the sets of matching
`<subject-part>` values are `anded` together, producing a result.  That result might be a Boolean which indicates that
the Tweet meets the filter criteria or some other type such as an SQL query that gets applied to a database.  But regardless,
the Filter is the same for any source of the Tweet.

Here is an example: `filter=author[contains]twain~body[contains]injun~body[contains]huck~body[contains]sid~body[contains]tom`


In the preceding example the filter would find Tweets where the `author` contains `twain` and the body contains `injun` or `huck` or `sid` or `tom`. 
This particular query would remove the tweets that came from Shania Twain, finding only those of Mark Twain.
 
## Applicability to the Monocle Library and Lenses, Functors and Applicative Functors

This project demonstrates the usefulness of the Monocle Lens library as well as several important concepts
from Category Theory, the Functor and the Applicative Functor.  Functors and Applicative Functors are critical to your 
understanding of Lenses, the later giving the various kinds of Lenses in Monocle their composability. Applicative Functors 
are everywhere in the tools we use as Scala programmers. SBT, the build tool is essentially an Applicative Functor.
Scalaz Validator is another example.  Applicative Functors, unlike Monads do not stop when you hit an error.  Applicative
Functors and Functors have algebraic properties that you can easily verify with unit tests.  You will see those tests in
this case study taken almost directly from Daniela's examples from her presentation below.


Before reading much further you should read this paper: [The Essence of the Iterator Pattern](https://www.cs.ox.ac.uk/jeremy.gibbons/publications/iterator.pdf)

And also watch this talk on Category Theory by Daniela Sfregola as I make liberal use of her examples in this project: [A Pragmatic Introduction to Category Theory](https://speakerdeck.com/danielasfregola/scalaworld-2017-a-pragmatic-introduction-to-category-theory)


Finally, do not despair. This is hard work and takes several months of hard study to understand.  Daniella said it took 6 months
for her.  And it has taken several years for me to reach my current state and I still have a long way to go. 

But the effort is well worth it.