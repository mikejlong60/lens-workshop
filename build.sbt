import Dependencies._
//import sbt.Keys.scalacOptions

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "lambdaworld",
      scalaVersion := "2.12.6",
      version      := "0.1.0-SNAPSHOT"
    )),
    name := "lens-workshop",
    libraryDependencies ++= List(scalaTest, scalaCheck)
  )
