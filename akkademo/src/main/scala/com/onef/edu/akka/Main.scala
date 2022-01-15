package com.onef.edu.akka

import com.typesafe.config.{Config, ConfigException, ConfigFactory, ConfigValue}
import java.util.{Map => JMap}

import akka.actor.ActorSystem

import scala.collection.JavaConversions._
import scala.collection.mutable

import scala.util.Try

object Main {
  def main(args: Array[String]): Unit = {
    val config = ConfigFactory.load()

    // dumpConfig(config)

    val path = Try { 
      config.getString("config.data-prep-path2")
    }.recover {
      case _: ConfigException.Missing => "default path"
    }

    println("path: " + path)

    // val system = ActorSystem("taskmanager")
  }

  private def dumpConfig(config: Config): Unit = {
    val confEntries: mutable.Set[JMap.Entry[String, ConfigValue]] = config.entrySet()

    for (confEntry <- confEntries) {
      println(s"configuration entry: '${confEntry.getKey}' = '${confEntry.getValue}'")
    }
  }
}
