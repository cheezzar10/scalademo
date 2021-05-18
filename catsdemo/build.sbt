name := "catsdemo"
version := "1.0"

scalaVersion := "2.13.3"

libraryDependencies += "org.typelevel" %% "cats-core" % "2.1.0"

lazy val root = Project(id = "root", file(".")).enablePlugins(JmhPlugin)
 
scalacOptions ++= Seq("-Xfatal-warnings")
