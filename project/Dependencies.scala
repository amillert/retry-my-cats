import sbt._

object Dependencies {

  private object Versions {
    val catsCore   = "2.7.0"
    val catsEffect = "3.3.4"
  }

  private val retry = Seq(
    "org.typelevel" %% "cats-core"   % Versions.catsCore,
    "org.typelevel" %% "cats-effect" % Versions.catsEffect
  )

  val root = retry
}
