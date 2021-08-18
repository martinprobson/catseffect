name := "catseffect"

version := "0.1"

scalaVersion := "2.13.6"

val catsEffectVersion = "3.2.2"

//idePackagePrefix := Some("net.martinprobson.catseffect")

libraryDependencies ++= Seq(
    "org.typelevel" %% "cats-effect" % catsEffectVersion
)
