import sbt._
import reaktor.scct.ScctProject

class SjqProject(info: ProjectInfo) extends DefaultProject(info) with ScctProject {
  //tests
  val scalacheck = "org.scala-tools.testing" % "scalacheck" % "1.5"
  val mockito = "org.mockito" % "mockito-core" % "1.7"
  val scalatest = "org.scalatest" % "scalatest" % "1.2"
  val junit = "junit" % "junit" % "4.4"
  val specs = "org.scala-tools.testing" % "specs_2.8.0" % "1.6.5"

  // mina/naggati
  val slf4j_api = "org.slf4j" % "slf4j-api" % "1.5.2"
  val slf4j_jdk14 = "org.slf4j" % "slf4j-jdk14" % "1.5.2"
  // use custom built one until new one is available: custom bult one is in lib
  //val naggati = "net.lag" % "naggati" % "0.8.0"
  val mina = "org.apache.mina" % "mina-core" % "2.0.0-M6"

  //http client
  val dispatch = "net.databinder" %% "dispatch-http" % "0.7.7"

  override def testClasspath = super.testClasspath +++  ("src" / "test" / "resources")
}
