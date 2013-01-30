
net.virtualvoid.sbt.graph.Plugin.graphSettings

org.scalastyle.sbt.ScalastylePlugin.Settings

/** Project */
name := "console"

version := "0.1"

organization := "com.crispywalrus"

scalaVersion := "2.10.0"

crossScalaVersions := Seq("2.10.0","2.9.2","2.9.1")

/** Shell */
shellPrompt := { state => System.getProperty("user.name") + "> " }

shellPrompt in ThisBuild := { state => Project.extract(state).currentRef.project + "> " }

resolvers ++= Seq(
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
  "Maven" at "http://repo2.maven.org/maven2",
  "Local Maven" at "file://"+Path.userHome.absolutePath+"/.m2/repository"
)

/** Dependencies */
libraryDependencies ++= Seq(
  "org.scalaz" %% "scalaz-core" % "7.0-SNAPSHOT",
  "net.databinder.dispatch" %% "dispatch-core" % "0.9.4",
  "com.lambdaworks" %% "jacks" % "2.1.2",
  "io.backchat.inflector" %% "scala-inflector" % "1.3.5",
  "org.hbase" % "asynchbase" % "1.3.2",
  "com.lambdaworks" % "lettuce" % "2.1.0",
  "org.scribe" % "scribe" % "1.3.0",
  "com.twitter" % "util-core" % "5.3.6",
  "com.twitter" % "util-eval" % "5.3.6",
  "com.typesafe" % "config" % "1.0.0",
  "com.google.protobuf" % "protobuf-java" % "2.4.1",
  "com.basho.riak" % "riak-client" % "1.0.7",
  "joda-time" % "joda-time" % "2.1",
  "org.joda" % "joda-money" % "0.7",
  "org.joda" % "joda-convert" % "1.2",
  "com.google.guava" % "guava" % "13.0.1",
  "javax.mail" % "mail" % "1.4.5",
  "org.scalatest" %% "scalatest" % "1.9.1" % "test"
)

/** Compilation */
javacOptions ++= Seq("-Xlint:unchecked")

javaOptions += "-Xmx1G"

autoCompilerPlugins := true

scalacOptions ++= Seq("-deprecation", "-unchecked")

pollInterval := 1000

logBuffered := false

cancelable := true

/** Console */
initialCommands in console := "import com.crispywalrus._"

seq(lessSettings:_*)

seq(jsSettings : _*)


