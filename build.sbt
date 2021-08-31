name := "catseffect"

version := "0.1"

scalaVersion := "2.13.6"

val catsEffectVersion = "3.2.2"

libraryDependencies ++= Seq(
    "org.typelevel" %% "cats-effect" % catsEffectVersion withSources() withJavadoc(),
    "org.scalactic" %% "scalactic" % "3.2.9" withSources() withJavadoc(),
    "org.scalatest" %% "scalatest" % "3.2.9" % "test" withSources() withJavadoc(),
    "org.typelevel" %% "cats-effect-testing-scalatest" % "1.2.0" % "test" withSources() withJavadoc(),
    "commons-io" % "commons-io" % "2.8.0" % "test"
)

scalacOptions ++= Seq(
    "-feature",
    "-deprecation",
    "-unchecked",
    "-language:postfixOps"
)
