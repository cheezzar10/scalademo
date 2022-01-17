package com.onef.edu.akka.reqreply

import scala.concurrent.duration.FiniteDuration

object Messages {
  case class Ping(message: String)

  case class PingWithTimeout(message: String, timeout: FiniteDuration)

  case class Pong(message: String)
}
