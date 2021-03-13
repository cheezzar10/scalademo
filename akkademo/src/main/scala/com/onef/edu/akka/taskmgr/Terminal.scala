package com.onef.edu.akka.taskmgr

import java.io.PrintStream
import java.util.concurrent.atomic.AtomicBoolean

import akka.actor.{Actor, Props, Terminated}

class Terminal extends Actor {
  import Terminal._
  import CommandProcessor._

  val commandProcessor = context.actorOf(CommandProcessor.props(self), "command-processor")

  override def receive: Receive = {
    case Input(line) => commandProcessor ! Command(line)
    case Output(line) => {
      print(line)
    }
    case Terminated(`commandProcessor`) => {
      active.set(false)

      context.system.terminate()
    }
  }

  override def preStart(): Unit = {
    context.watch(commandProcessor)
  }

  override def postStop(): Unit = {
    println("terminal closed.")
  }
}

object Terminal {
  val active = new AtomicBoolean(true)

  def name = "terminal"

  case class Input(line: String)

  case class Output(line: String)
}