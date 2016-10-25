resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

// unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )

lazy val commonSettings = Seq(
  version := "0.1",
  scalaVersion := "2.11.7",
  libraryDependencies ++= Seq(
    "com.typesafe.akka" %% "akka-cluster-tools" % "2.4.6"
  )
)


lazy val `TicTacToe` = (project in file("."))
  .settings(commonSettings)
  .enablePlugins(PlayScala)


lazy val `web` = (project in file("web"))
  .settings(commonSettings).settings(name := "TicTacToe-web")
  .enablePlugins(PlayScala).
  dependsOn(`backend`)

lazy val `backend` = (project in file("backend")).
  settings(commonSettings).
  settings(name := "TicTacToe-backend")