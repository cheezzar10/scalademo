package com.onef.edu.akka.reqreply

import akka.actor.{Actor, ActorLogging}

class DeadLetterProcessor extends Actor with ActorLogging {
  def receive: Receive = {
    case deadLetter =>
      log.info("dead letter: " + deadLetter)
  }
}
