package com.onef.edu.akka.taskmgr

import akka.actor.Actor

// SessionScopedTaskManager - one actor per session, managed by SessionSupervisor ( enclave session )
class TaskRunner extends Actor {
  import TaskRunner._

  override def receive = {
    case RunTask(name) => println("running task: " + name)
  }
}

object TaskRunner {
  case class RunTask(name: String)
}
