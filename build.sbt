
resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"


lazy val commonSettings = Seq(
  version := "0.1",
  scalaVersion := "2.11.7",
  classpathTypes += "maven-plugin",
  resolvers += Resolver.jcenterRepo,
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
    //angular2 dependencies
    "org.webjars.npm" % "angular__common" % "2.2.0",
    "org.webjars.npm" % "angular__compiler" % "2.2.0",
    "org.webjars.npm" % "angular__core" % "2.2.0",
    "org.webjars.npm" % "angular__http" % "2.2.0",
    "org.webjars.npm" % "angular__forms" % "2.2.0",
    "org.webjars.npm" % "angular__router" % "3.2.0",
    "org.webjars.npm" % "angular__platform-browser-dynamic" % "2.2.0",
    "org.webjars.npm" % "angular__platform-browser" % "2.2.0",
    "org.webjars.npm" % "systemjs" % "0.19.40",
    "org.webjars.npm" % "rxjs" % "5.0.0-beta.12",
    "org.webjars.npm" % "reflect-metadata" % "0.1.8",
    "org.webjars.npm" % "zone.js" % "0.6.26",
    "org.webjars.npm" % "core-js" % "2.4.1",
    "org.webjars.npm" % "symbol-observable" % "1.0.1",
    "org.webjars.npm" % "typescript" % "2.1.4",

    "com.mohiva" %% "play-silhouette" % "4.0.0",
    "com.mohiva" %% "play-silhouette-password-bcrypt" % "4.0.0",
    "com.mohiva" %% "play-silhouette-crypto-jca" % "4.0.0",
    "com.mohiva" %% "play-silhouette-persistence" % "4.0.0",
    "com.mohiva" %% "play-silhouette-testkit" % "4.0.0" % "test",
    "com.mohiva" %% "play-silhouette-cas" % "4.0.0",
    "com.mohiva" %% "play-silhouette-persistence-reactivemongo" % "4.0.1",
    "com.typesafe.play" %% "play-mailer" % "5.0.0",
    "com.iheart" %% "ficus" % "1.4.0",


    //tslint dependency
    "org.webjars.npm" % "tslint-eslint-rules" % "3.1.0",
    "org.webjars.npm" % "tslint-microsoft-contrib" % "2.0.12",
    //   "org.webjars.npm" % "codelyzer" % "2.0.0-beta.1",
    "org.webjars.npm" % "types__jasmine" % "2.2.26-alpha" % "test"
  )
)


lazy val root = (project in file("."))
  .settings(commonSettings)
  .settings(name := "TicTacToe")
  .enablePlugins(PlayScala, SbtWeb)


lazy val `web` = (project in file("web"))
  .settings(commonSettings)
  .settings(name := "TicTacToe-web")
  .enablePlugins(PlayScala, SbtWeb)
  .dependsOn(`backend`)

lazy val `backend` = (project in file("backend"))
  .settings(commonSettings)
  .settings(name := "TicTacToe-backend")


dependencyOverrides += "org.webjars.npm" % "minimatch" % "3.0.0"

// use the webjars npm directory (target/web/node_modules ) for resolution of module imports of angular2/core etc
resolveFromWebjarsNodeModulesDir := true

// use the combined tslint and eslint rules plus ng2 lint rules
(rulesDirectories in tslint) := Some(List(
  tslintEslintRulesDir.value,
  ng2LintRulesDir.value
))

//logLevel in tslint := Level.Debug
routesGenerator := InjectedRoutesGenerator