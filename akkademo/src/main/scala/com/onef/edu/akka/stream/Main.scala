package com.onef.edu.akka.stream

import akka.actor.{ActorSystem, Props}
import akka.stream.ActorMaterializer
import akka.{Done, NotUsed}
import akka.stream.scaladsl.{Keep, RunnableGraph, Sink, Source}

import scala.concurrent.Future

object Main {
  def main(args: Array[String]): Unit = {
    // theMostBasicStream()
    dummyStream()
  }

  private def dummyStream(): Unit = {
    val source = Source(1 to 5)
    // val sink = Sink.foreach(println)
    implicit val system = ActorSystem()

    val sinkActor = system.actorOf(Props[SinkActor])
    val sink = Sink.actorRef(sinkActor, SinkActor.StreamCompleted)
    val runnableGraph = source.to(sink)

    implicit val mat = ActorMaterializer()
    implicit val ec = system.dispatcher

    // TODO detect stream completion
    val result = runnableGraph.run()
    println("runnable graph result: " + result)
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
