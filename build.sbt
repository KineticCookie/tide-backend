name := "tide"

version := "0.0.1"

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)

scalaVersion := "2.11.8"
// scalaz-bintray resolver needed for specs2 library
resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"

libraryDependencies ++= Seq(
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.0" % "test",
  "com.typesafe.akka" %% "akka-testkit" % "2.4.4" % Test,
  "com.typesafe.akka" %% "akka-stream-testkit" % "2.4.4" % Test,

  "org.mindrot" % "jbcrypt" % "0.3m",
  "com.pauldijou" %% "jwt-play" % "0.8.1",

  "com.typesafe.play" %% "play-slick" % "2.0.0",
  "com.typesafe.play" %% "play-slick-evolutions" % "2.0.0",
  "org.postgresql" % "postgresql" % "9.3-1100-jdbc4"
)

routesGenerator := InjectedRoutesGenerator
