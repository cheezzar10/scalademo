package com.onef.edu.akka.stream

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.{Done, NotUsed}
import akka.stream.scaladsl.{Keep, RunnableGraph, Sink, Source}

import scala.concurrent.Future

object Main {
  def main(args: Array[String]): Unit = {
    theMostBasicStream()

    println("completed")
  }

  private def theMostBasicStream(): Unit = {
    // NotUsed is like Unit
    val source: Source[Int, NotUsed] = Source(1 to 5)

    // Done is like Unit also ( for completed stream result )
    // val sink: Sink[Int, Future[Done]] = Sink.foreach[Int](println)

    val sink: Sink[Int,  Future[Int]] = Sink.fold(0)(_ + _)


    implicit val system = ActorSystem()
    implicit val mat = ActorMaterializer()
    implicit val ec = system.dispatcher

    // to() is the shorthand for toMat(x, Keep.left)
    // val dataflow: RunnableGraph[NotUsed] = source.to(sink)
    // val result = dataflow.run

    val dataflow = source.toMat(sink)(Keep.right)
    val f: Future[Int] = dataflow.run

    // println("result: " + result)
    f.onComplete { r =>
      println("result: " + r)

      system.terminate()
    }
  }
}
