package lensworkshop.casestudy.filterlanguage

object QueryLanguageParser {

  val parse: (String) => Tuple3[String, String, String] = { expression =>
    val (subject, subjectNdx) = {
      val n = expression.indexOf("[")
      if (n < 0) (s"missing left predicate delimiter for expression: $expression", n)
      else (expression.substring(0, n).toLowerCase, n)
    }
    val (value, valueNdx) = {
      val n = expression.indexOf("]")
      if (n < 0) (s"missing right predicate delimiter for expression: $expression", n)
      else (expression.substring(n + 1), n)
    }

    val predicate = {
      if (valueNdx > 0 && subjectNdx > 0 && (valueNdx - subjectNdx) > 0) expression.substring(subjectNdx + 1, valueNdx).toLowerCase
      else (s"invalid predicate expression: $expression")

    }
    (subject, predicate, value)
  }

  val validate: (Tuple3[String, String, String]) => (Tuple3[String, String, String], Option[String]) = { expressionTriple =>
    val allPredicates = List("equals", "notequals", "contains", "notcontains", "beginswith", "notbeginswith", "endswith", "notendswith")
    val maybeError = expressionTriple match {
      case ("author", predicate, _) if (allPredicates contains predicate) => None
      case ("author", predicate, _) => Some(s"an author query can only use predicates[$allPredicates] and it used[$predicate]")
      case ("body", predicate, _) if (allPredicates contains predicate) => None
      case ("body", predicate, _) => Some(s"a body query can only use predicates[$allPredicates] and it used[$predicate]")
      case ("subject", predicate, _) if (allPredicates contains predicate) => None
      case ("subject", predicate, _) => Some(s"a subject query can only use predicates[$allPredicates] and it used[$predicate]")
      case exp @ _ => Some(s"unknown query expression $exp")
    }
    (expressionTriple, maybeError)
  }

  val tokenize: (String => List[String]) = query => query.split(";").map(_.trim).filter(que => que.size > 0).toList

  val parseAndValidate = parse andThen validate

  def makeFilter(triplesWithMaybeErrors: List[((String, String, String), Option[String])]): Either[List[String], Filter[(String, String, String)]] = {
    val errors = triplesWithMaybeErrors.map(parsedAndValidated => parsedAndValidated._2).flatten
    val conjunctions = triplesWithMaybeErrors.foldLeft(Map.empty[String, PredicateDisjunction[(String, String, String)]])((accum, subjectPredicatePhrase) => {
      val subject = subjectPredicatePhrase._1._1
      val disj = accum.getOrElse(subject, PredicateDisjunction(List.empty[PredicatePhrase[(String, String, String)]]))
      val newd = PredicateDisjunction(PredicatePhrase(phrase = (subjectPredicatePhrase._1._1, subjectPredicatePhrase._1._2, subjectPredicatePhrase._1._3)) :: disj.predicates)
      accum + (subject -> newd)
    })
    if (!errors.isEmpty) Left(errors)
    else Right(Filter(predicateConjunctions = conjunctions))
  }
}
