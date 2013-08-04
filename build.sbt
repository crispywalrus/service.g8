import sbtprotobuf.ProtobufPlugin._

// new here's our project definition */
name := "console"

organization := "com.crispywalrus"

scalaVersion := "2.10.2"

crossScalaVersions := Seq("2.10.2")

// Shell */
shellPrompt := { state => System.getProperty("user.name") + "> " }

shellPrompt in ThisBuild := { state => Project.extract(state).currentRef.project + "> " }

// compiler setup */
scalacOptions in Test ++= Seq("-Yrangepos")

scalacOptions in Compile ++=Seq(
  "-encoding",
  "UTF-8",
  "-target:jvm-1.7",
  "-deprecation",
  "-unchecked",
  "-explaintypes",
  "-feature")

javacOptions in Compile ++= Seq(
  "-source","1.7",
  "-target","1.7",
  "-Xlint:unchecked",
  "-Xlint:deprecation",
  "-Xlint:-options")

javacOptions ++= Seq("-Xlint:unchecked")

javaOptions += "-Xmx1G"

autoCompilerPlugins := true

// resovlers make sure we can pull local builds from the
// .m2/repository local cache */
resolvers ++= Seq(
  "Sonatype OSS Releases" at "https://oss.sonatype.org/content/repositories/releases",
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
  "Twttr" at "http://maven.twttr.com/",
  "Local Maven" at "file://"+Path.userHome.absolutePath+"/.m2/repository"
)

// Dependencies */
libraryDependencies ++= Seq(
  "org.slf4j" % "slf4j-api" % "1.7.5",
  "com.lambdaworks" %% "jacks" % "2.2.2",
  "com.lambdaworks" % "lettuce" % "2.3.3",
  "org.apache.curator" % "curator-x-discovery" % "2.1.0-incubating",
  "com.netflix.hystrix" % "hystrix-core" % "1.3.0",
  "com.netflix.archaius" % "archaius-scala" % "0.5.11",
  "com.netflix.archaius" % "archaius-zookeeper" % "0.5.11",
  "ch.qos.logback" % "logback-classic" % "1.0.13", 
  "com.twitter" % "util-eval" % "6.3.8",
  "com.google.protobuf" % "protobuf-java" % "2.5.0",
  "org.specs2" %% "specs2" % "2.1.1" % "test",
  "org.apache.curator" % "curator-test" % "2.1.0-incubating" % "test"
)


// load and configure plugins

// dependency managment
net.virtualvoid.sbt.graph.Plugin.graphSettings

// style checker
org.scalastyle.sbt.ScalastylePlugin.Settings

// automated release steps
releaseSettings 

// build info configuration
buildInfoSettings

sourceGenerators in Compile <+= buildInfo

buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion, buildInfoBuildNumber)

buildInfoPackage := "com.crispywalrus.info"

// console configuration
initialCommands in console := "import com.crispywalrus._"

// protobuf config
seq(protobufSettings: _*)

version in protobufConfig := "2.5.0"
