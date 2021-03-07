package com.onef.edu.akka

import com.typesafe.config.{ Config, ConfigFactory }

object Main {
  def main(args: Array[String]): Unit = {
    println("started")

    val config = ConfigFactory.load()
  }
}
