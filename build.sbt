resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

// unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )

lazy val commonSettings = Seq(
  version := "0.1",
  scalaVersion := "2.11.7",
  classpathTypes += "maven-plugin",
  libraryDependencies ++= Seq(
    "com.google.guava" % "guava" % "19.0",
    "javax.inject" % "javax.inject" % "1",
    "junit" % "junit" % "4.12",
    "log4j" % "log4j" % "1.2.17",
    "org.json4s" %% "json4s-jackson" % "3.4.2",
    "com.google.inject" % "guice" % "3.0",
    "com.fasterxml.jackson.core" % "jackson-databind" % "2.6.3",
    "org.clapper" %% "grizzled-slf4j" % "1.0.2",
    "com.typesafe.akka" %% "akka-cluster-tools" % "2.4.6"
  )
)


lazy val root = (project in file("."))
  .settings(commonSettings)
  .settings(name := "TicTacToe-root")
  .enablePlugins(PlayScala)


lazy val `web` = (project in file("web"))
  .settings(commonSettings)
  .settings(name := "TicTacToe-web")
  .enablePlugins(PlayScala)
  .dependsOn(`backend`)

lazy val `backend` = (project in file("backend"))
  .settings(commonSettings)
  .settings(name := "TicTacToe-backend")