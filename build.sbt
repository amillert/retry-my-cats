import Settings._

Global / onChangedBuildSource := ReloadOnSourceChanges

lazy val root = (project in file(".")).commonSettings
  .settings(name := "retry-my-cats", libraryDependencies ++= Dependencies.root)

addCommandAlias("ls", "projects")
addCommandAlias("cd", "project")

addCommandAlias("r", "; reload")
addCommandAlias("c", "; compile")
addCommandAlias("rc", "; r ; c")
addCommandAlias("rcc", "; r ; clean ; c")
