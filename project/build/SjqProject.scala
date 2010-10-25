import sbt._

class SmqProject(info: ProjectInfo) extends DefaultProject(info) {
  //tests
  val scalacheck = "org.scala-tools.testing" % "scalacheck" % "1.5"
  val mockito = "org.mockito" % "mockito-core" % "1.7"
  val scalatest = "org.scalatest" % "scalatest" % "1.2"
  val junit = "junit" % "junit" % "4.4"
  val specs = "org.scala-tools.testing" % "specs_2.8.0" % "1.6.5"

   override def testClasspath = super.testClasspath +++
                                  ("src" / "test" / "resources")
}
