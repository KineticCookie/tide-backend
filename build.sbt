name := "tide"

version := "0.0.1"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.8"

// scalaz-bintray resolver needed for specs2 library
resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"

libraryDependencies ++= Seq(
  ws, // Play's web services module
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.0" % "test",
  "com.typesafe.akka" %% "akka-testkit" % "2.4.4" % Test,
  "com.typesafe.akka" %% "akka-stream-testkit" % "2.4.4" % Test,
  "org.webjars" % "bootstrap" % "3.3.6",
  "org.webjars" % "flot" % "0.8.3",
  "com.typesafe.play" %% "play-slick" % "2.0.0",
  "com.typesafe.play" %% "play-slick-evolutions" % "2.0.0"
)

routesGenerator := InjectedRoutesGenerator
