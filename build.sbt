name := "catseffect"

version := "0.1"

scalaVersion := "2.13.6"

val catsEffectVersion = "3.2.2"

libraryDependencies ++= Seq(
    "org.typelevel" %% "cats-effect" % catsEffectVersion withSources() withJavadoc(),
    "org.typelevel" %% "cats-mtl" % "1.2.1" withSources() withJavadoc()
)

scalacOptions ++= Seq(
    "-feature",
    "-deprecation",
    "-unchecked",
    "-language:postfixOps"
)
