package com.onef.edu.akka.reqreply

import akka.actor.{Actor, ActorLogging, ActorRef, Status}
import akka.pattern.{AskTimeoutException, ask, pipe}
import akka.util.Timeout

class RequestActor(target: ActorRef) extends Actor with ActorLogging {
  import Messages._

  override def receive: Receive = {
    case ping: Ping =>
      log.info("sending ping: " + ping)
      target ! ping
    case PingWithTimeout(message, timeout) =>
      log.info("sending ping with timeout")

      implicit val t: Timeout = timeout
      // implicit ExecutionContext required for pipe() conversion
      implicit val ec = context.dispatcher

      target.ask(Ping(message)).pipeTo(self)
    case Status.Failure(timeoutEx: AskTimeoutException) =>
      log.info("ping request timed out: " + timeoutEx.getMessage)
    case Pong(message) =>
      log.info("pong received: " + message)
  }

  override def unhandled(message: Any): Unit = {
    log.info(s"unhandled message $message of class ${message.getClass}")
  }

  override def postStop(): Unit = {
    log.info("pinger actor stopped")
  }
}
