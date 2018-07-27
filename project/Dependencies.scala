import sbt._


object Dependencies {
  val monocleVersion = "1.5.0" // 1.5.0-cats based on cats 1.0.x

  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.0.5"
  lazy val scalaCheck = "org.scalacheck" %% "scalacheck" % "1.14.0"

  lazy val monocleCore = "com.github.julien-truffaut" %% "monocle-core" % monocleVersion
  lazy val monocleMacro = "com.github.julien-truffaut" %% "monocle-macro" % monocleVersion
  lazy val monocleLaw = "com.github.julien-truffaut" %% "monocle-law" % monocleVersion
}
