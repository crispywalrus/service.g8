import sbt._
import Keys._
import sbtbuildinfo.Plugin._
import org.ensime.sbt.util.SExp._
import org.ensime.sbt.Plugin.Settings.ensimeConfig
import sbtrelease.ReleasePlugin._
import net.virtualvoid.sbt.graph.Plugin.graphSettings
import sbtrelease._
import ReleaseStateTransformations._
import ReleaseKeys._
import ReleaseSteps._
import com.github.retronym.SbtOneJar.oneJarSettings
import sbtclosure.SbtClosurePlugin._

object build extends Build {

  val Organization = "$organization$.$name$"
  val ScalaVersion = "2.10.3"
  val AkkaVersion  = "2.2.3"
  val Slf4jVersion = "1.7.5"

  lazy val $name$Settings: Seq[Setting[_]] = Defaults.defaultSettings++buildInfoSettings++releaseSettings++graphSettings++Seq(
    organization := Organization,
    scalaVersion := ScalaVersion,
    scalacOptions in Compile ++= Seq(
      "-encoding","UTF-8",
      "-target:jvm-1.7",
      "-deprecation",
      "-unchecked",
      "-explaintypes",
      "-feature",
      "-language:higherKinds",
      "-language:implicitConversions",
      "-language:postfixOps"
    ),

    javacOptions in Compile ++= Seq(
      "-source","1.7",
      "-target","1.7",
      "-Xlint:unchecked",
      "-Xlint:deprecation",
      "-Xlint:-options"
    ),

    javaOptions += "-Xmx1G",

    resolvers ++= Seq(
      "Maven" at "http://repo2.maven.org/maven2",
      "Sonatype OSS Releases" at "https://oss.sonatype.org/content/repositories/snapshots",
      "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
    ),

    autoCompilerPlugins := true,

    sourceGenerators in Compile <+= buildInfo,
    buildInfoKeys ++= Seq[BuildInfoKey](
      name,
      version,
      scalaVersion,
      buildInfoBuildNumber
    ),

    libraryDependencies ++= Seq(
      "org.slf4j" % "slf4j-api" % Slf4jVersion,
      "org.slf4j" % "jcl-over-slf4j" % Slf4jVersion,
      "com.typesafe.akka" %% "akka-actor" % AkkaVersion,
      "javax.annotation" % "jsr250-api" % "1.0",
      "javax.inject" % "javax.inject" % "1"
    ),

    releaseProcess := Seq[ReleaseStep](
      checkSnapshotDependencies,
      inquireVersions,
      runTest,
      setReleaseVersion,
      commitReleaseVersion,
      setReadmeReleaseVersion,
      tagRelease,
      publishArtifacts,
      setNextVersion,
      resetBuildnumber,
      startNextIteration,
      pushChanges
      ),

    ensimeConfig := sexp(
      key(":formatting-prefs"), sexp(
        key(":alignParameters"), true,
        key(":alignSingleLineCaseStatements"), true,
        key(":rewriteArrowSymbols"), true,
        key(":compactStringConcatenation"), true,
        key(":indentWithTabs"), false
      )
    ),

    shellPrompt := { state => "sbt:$name$> " },

    buildInfoPackage := "$organization$.$name$.build"
  )

  lazy val testingDependencies = Seq(
    "org.slf4j" % "slf4j-simple" % Slf4jVersion % "it,test",
    "org.mockito" % "mockito-all" % "1.9.5" % "it,test",
    "org.scalatest" %% "scalatest" % "2.1.7" % "it,test"
  )

  lazy val web$name$Settings: Seq[Setting[_]] = $name$Settings++closureSettings

  lazy val service = Project("$name$",file("."))
    .settings($name$Settings: _*)
    .configs(IntegrationTest)
    .settings(Defaults.itSettings: _*)
    .settings( libraryDependencies ++= testingDependencies )

}
