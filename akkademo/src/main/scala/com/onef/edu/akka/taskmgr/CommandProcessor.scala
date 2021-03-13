package com.onef.edu.akka.taskmgr

import akka.actor.{Actor, ActorRef, PoisonPill, Props}
import com.onef.edu.akka.taskmgr.Terminal.Output

class CommandProcessor(terminal: ActorRef) extends Actor {
  import CommandProcessor._

  override def receive: Receive = {
    case Command(command, args) => {
      handleCommand(command, args)
    }
  }

  private def handleCommand(command: String, args: Seq[String]): Unit = {
    if (command == "exit") {
      self ! PoisonPill
    } else {
      terminal ! Output("unknown command\n")
      terminal ! Output("> ")
    }
  }

  override def preStart(): Unit = {
    terminal ! Output("command processor started: " + self.path + "\n")
    terminal ! Output("> ")
  }

  override def postStop(): Unit = {
    terminal ! Output("command processor exited.\n")
  }
}

object CommandProcessor {
  def props(terminal: ActorRef) = Props(new CommandProcessor(terminal))

  case class Command(command: String, args: Seq[String] = Seq.empty)
}