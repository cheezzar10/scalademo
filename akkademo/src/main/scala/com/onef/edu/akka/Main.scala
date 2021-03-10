package com.onef.edu.akka

import com.typesafe.config.{Config, ConfigFactory, ConfigValue}

import java.util.{Map => JMap}
import scala.collection.JavaConversions._
import scala.collection.mutable

object Main {
  def main(args: Array[String]): Unit = {
    val config = ConfigFactory.load()

    dumpConfig(config)


  }

  private def dumpConfig(config: Config): Unit = {
    val confEntries: mutable.Set[JMap.Entry[String, ConfigValue]] = config.entrySet()

    for (confEntry <- confEntries) {
      println(s"configuration entry: '${confEntry.getKey}' = '${confEntry.getValue}'")
    }
  }
}
