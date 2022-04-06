val catsEffectVersion = "3.2.9"

val catseffect = Seq(
  "org.typelevel" %% "cats-effect" % catsEffectVersion withSources () withJavadoc ()
)

val logging = Seq(
  "org.slf4j" % "slf4j-api" % "2.0.0-alpha4",
  "ch.qos.logback" % "logback-classic" % "1.3.0-alpha10",
  "ch.qos.logback" % "logback-core" % "1.3.0-alpha10",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.4"
)

val config = Seq("com.typesafe" % "config" % "1.4.1", "com.github.andr83" %% "scalaconfig" % "0.7")

val test = Seq(
  "org.scalactic" %% "scalactic" % "3.2.10" % Test,
  "org.scalatest" %% "scalatest" % "3.2.10" % Test,
  "commons-io" % "commons-io" % "2.8.0" % Test,
  "org.typelevel" %% "cats-effect-testing-scalatest" % "1.2.0" % Test withSources () withJavadoc ()
)

lazy val cats_effect = (project in file("."))
  .settings(
    name := "cats_effect",
    version := "0.0.1-SNAPSHOT",
    libraryDependencies ++= logging,
    libraryDependencies ++= catseffect,
    libraryDependencies ++= config,
    libraryDependencies ++= test,
    scalaVersion := "2.13.6"
  )

scalacOptions ++= Seq(
  "-deprecation", // Emit warning and location for usages of deprecated APIs.
  "-explaintypes", // Explain type errors in more detail.
  "-Xfatal-warnings", // Fail the compilation if there are any warnings.
  "-Xsource:3", // Warn for Scala 3 features
  "-Ywarn-dead-code" // Warn when dead code is identified.
)

javacOptions ++= Seq("-source", "17", "-target", "17", "-Xlint")

assembly / assemblyMergeStrategy := {
  case PathList("META-INF", xs @ _*)       => MergeStrategy.discard
  case n if n.startsWith("reference.conf") => MergeStrategy.concat
  case _                                   => MergeStrategy.first
}
