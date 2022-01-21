ThisBuild / version      := "0.0.1"
ThisBuild / organization := "amillert"
ThisBuild / scalaVersion := "3.1.0"

Global / onChangedBuildSource := ReloadOnSourceChanges

lazy val root = (project in file("."))
  .settings(name := "retry-my-cats", libraryDependencies ++= Dependencies.root)

addCommandAlias("ls", "projects")
addCommandAlias("cd", "project")

addCommandAlias("r", "; reload")
addCommandAlias("c", "; compile")
addCommandAlias("rc", "; r ; c")
addCommandAlias("rcc", "; r ; clean ; c")
