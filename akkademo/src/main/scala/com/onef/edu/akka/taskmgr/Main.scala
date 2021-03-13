package com.onef.edu.akka.taskmgr

import akka.actor.{ActorSystem, Props}
import com.onef.edu.akka.taskmgr.Terminal._

import scala.io.StdIn

object Main {
  def main(args: Array[String]): Unit = {
    val system = ActorSystem("uos")

    val terminal = system.actorOf(Props[Terminal], Terminal.name)

    while (Terminal.active.get) {
      // standard input stream can be wrapped with InterruptibleChannel

      val command = StdIn.readLine()

      terminal ! Input(command)
    }
  }
}
