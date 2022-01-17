package com.onef.edu.akka.reqreply

import akka.actor.{ActorSystem, DeadLetter, Props}

import scala.concurrent.duration._

object Main {
  def main(args: Array[String]): Unit = {
    val system = ActorSystem()

    val deadLetterProcessor = system.actorOf(Props[DeadLetterProcessor], "dead-letter-processor")
    system.eventStream.subscribe(deadLetterProcessor, classOf[DeadLetter])

    val ponger = system.actorOf(Props[ResponseActor], "ponger")
    val pinger = system.actorOf(Props(classOf[RequestActor], ponger), "pinger")

    pinger ! Messages.Ping("hello")
    pinger ! Messages.PingWithTimeout("hey", 100.millis)

    Thread.sleep(5000)

    system.terminate()
  }
}
