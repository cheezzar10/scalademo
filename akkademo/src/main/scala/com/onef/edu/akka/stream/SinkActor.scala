package com.onef.edu.akka.stream

import akka.actor.Actor

object SinkActor {
  case object StreamCompleted
}

class SinkActor extends Actor {
  import SinkActor._

  override def receive = {
    case  StreamCompleted => {
      println("stream completion message received by actor: " + self.path)
      // TODO use more graceful shutdown scheme ()
      // context.system.terminate()
      context.stop(context.parent)
    }
    case msg => println("stream message received: " + msg)
  }
}
