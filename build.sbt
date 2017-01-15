resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

// unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )

lazy val commonSettings = Seq(
  version := "0.1",
  scalaVersion := "2.11.7",
  classpathTypes += "maven-plugin",
  libraryDependencies ++= Seq(
    // "com.google.guava" % "guava" % "19.0",
    "junit" % "junit" % "4.12",
    "log4j" % "log4j" % "1.2.17",
    "org.mongodb" % "mongo-java-driver" % "3.3.0",
    "org.mongodb" % "bson" % "3.3.0",
    "org.json4s" %% "json4s-jackson" % "3.4.2",
    "net.codingwell" %% "scala-guice" % "4.0.1",
    "com.fasterxml.jackson.core" % "jackson-databind" % "2.6.3",
    "org.clapper" %% "grizzled-slf4j" % "1.0.2",
    "com.typesafe.akka" %% "akka-cluster-tools" % "2.4.6",
    "com.mohiva" %% "play-silhouette" % "4.0.0",
    "com.mohiva" %% "play-silhouette-password-bcrypt" % "4.0.0",
    "com.mohiva" %% "play-silhouette-crypto-jca" % "4.0.0",
    "com.mohiva" %% "play-silhouette-persistence" % "4.0.0",
    "com.mohiva" %% "play-silhouette-testkit" % "4.0.0" % "test",
    "com.mohiva" %% "play-silhouette-cas" % "4.0.0",
    "com.mohiva" %% "play-silhouette-persistence-reactivemongo" % "4.0.1",
    "com.typesafe.play" %% "play-mailer" % "5.0.0",
    "com.iheart" %% "ficus" % "1.4.0"
  )
)


lazy val root = (project in file("."))
  .settings(commonSettings)
  .settings(name := "TicTacToe")
  .enablePlugins(PlayScala)


lazy val `web` = (project in file("web"))
  .settings(commonSettings)
  .settings(name := "TicTacToe-web")
  .enablePlugins(PlayScala)
  .dependsOn(`backend`)

lazy val `backend` = (project in file("backend"))
  .settings(commonSettings)
  .settings(name := "TicTacToe-backend")