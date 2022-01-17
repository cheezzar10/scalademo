package com.onef.edu.akka.reqreply

import akka.actor.{Actor, ActorLogging}

class ResponseActor extends Actor with ActorLogging {
  import Messages._

  override def receive: Receive = {
    case ping @ Ping(message) =>
      log.info("ping received: " + ping)
      Thread.sleep(200)
      val pong = Pong(message)
      log.info("replying with pong: " + pong)
      sender() ! pong
  }
}
