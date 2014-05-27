import io.Source
import java.io.PrintWriter
import sbtrelease._
import ReleasePlugin._
import ReleaseKeys._
import sbt._
import sbt.Extracted

object ReleaseSteps {

  val readme = "README.md"

  class StateW(st: State) {
    def extract = Project.extract(st)
  }

  implicit def stateW(st: State): StateW = new StateW(st)


  private def vcs(st: State): Vcs = {
    st.extract.get(versionControlSystem).getOrElse(sys.error("Aborting release. Working directory is not a repository of a recognized VCS."))
  }

  lazy val setReadmeReleaseVersion: ReleaseStep = { st: State =>
    val releaseVersions = getReleasedVersion(st)
    updateReadme(st, releaseVersions._1)
    commitReadme(st, releaseVersions._1)
    st
  }

  private def getReleasedVersion(st: State): (String, String) = {
    st.get(versions).getOrElse(sys.error("No versions are set."))
  }

  private def updateReadme(st: State, newVersion: String) {
    val newmanRegex = """\d+\.\d+\.\d+""".r
    val oldReadme = Source.fromFile(readme).mkString
    val out = new PrintWriter(readme, "UTF-8")
    try {
      val newReadme = newmanRegex.replaceAllIn(oldReadme, "%s".format(newVersion))
      newReadme.foreach(out.write(_))
    } finally {
      out.close()
    }
  }

  private def commitReadme(st: State, newVersion: String) {
    val vcs = Project.extract(st).get(versionControlSystem).getOrElse(sys.error("Unable to get version control system."))
    vcs.add(readme) !! st.log
    vcs.commit("README.md updated to %s".format(newVersion)) ! st.log
  }

  lazy val resetBuildnumber: ReleaseStep = ReleaseStep(resetNumber)

  private def resetNumber = { st: State =>
    val buildNumProp = new java.util.Properties
    buildNumProp.load(new java.io.FileInputStream("buildinfo.properties"))
    buildNumProp.setProperty("buildnumber","0")
    buildNumProp.store(new java.io.FileOutputStream("buildinfo.properties"),null)
    st
  }

  lazy val startNextIteration = ReleaseStep(nextVersion)

  private def nextVersion = { st: State =>
    vcs(st).add("version.sbt") !! st.log
    vcs(st).add("buildinfo.properties") !! st.log
    val status = (vcs(st).status !!) trim

    val newState = if (status.nonEmpty) {
      val (state, msg) = st.extract.runTask(commitMessage, st)
      vcs(state).commit(msg) ! st.log
      state
    } else {
      // nothing to commit. this happens if the version.sbt file hasn't changed.
      st
    }
    newState
  }

}
