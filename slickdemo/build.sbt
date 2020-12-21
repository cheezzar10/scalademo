ThisBuild / scalaVersion := "2.11.12"
ThisBuild / organization := "com.onef"
ThisBuild / version := "0.1-SNAPSHOT"

// TODO move to project/Dependencies.scala
val slickVersion = "3.3.3"
val h2Version = "1.4.200"
val logbackVersion = "1.2.3"

lazy val root = (project in file("."))
    .settings(
        name := "slickdemo",
        
        libraryDependencies ++= Seq(
            "com.typesafe.slick" %% "slick" % slickVersion,
            "com.h2database" % "h2" % h2Version,
            "ch.qos.logback" % "logback-classic" % logbackVersion
        )
    )

// TODO also it's possible to pass -target:jvm-1.8
scalacOptions ++= Seq("-encoding", "UTF-8", "-deprecation", "-feature", "-Xlint", "-Xfatal-warnings")
