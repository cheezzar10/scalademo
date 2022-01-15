package com.onef.edu.akka.stream

import akka.actor.Actor

object SinkActor {
  case object StreamCompleted
}

class SinkActor extends Actor {
  import SinkActor._

  override def receive = {
    case  StreamCompleted => {
      println("stream completion message received.")
      // TODO use more graceful shutdown scheme ()
      context.system.terminate()
    }
    case msg => println("stream message received: " + msg)
  }
}
