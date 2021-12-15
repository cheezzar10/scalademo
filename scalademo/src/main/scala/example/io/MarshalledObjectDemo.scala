package example.io

import java.io.{FileInputStream, FileOutputStream, ObjectInputStream, ObjectOutputStream}
import java.net.{URL, URLClassLoader}
import java.rmi.MarshalledObject

/*
class ClockWorkPayload extends Serializable {
  private def readObject(in: ObjectInputStream): Unit = {
    in.defaultReadObject()
    println("exploded")
  }
}
 */

object MarshalledObjectDemo {
  def main(args: Array[String]): Unit = {
    // storePayload()
    plantPayload()
  }

  private def storePayload(): Unit = {
    val urlClassLoader =
      new URLClassLoader(Array(
        new URL("file:///Users/andrey.smirnov/Downloads/scala-demo-0.1.0-SNAPSHOT.jar")))

    val clockWorkPayloadClass = urlClassLoader.loadClass("example.io.ClockWorkPayload")
    val clockWorkPayload = clockWorkPayloadClass.newInstance()

    println("payload class: " + clockWorkPayload.getClass)

    val marshalledPayload = new MarshalledObject(clockWorkPayload)

    val out = new ObjectOutputStream(new FileOutputStream("payload.bin"))
    out.writeObject(marshalledPayload)
  }

  private def plantPayload(): Unit = {
    val in = new ObjectInputStream(new FileInputStream("payload.bin"))
    val payload = in.readObject().asInstanceOf[MarshalledObject[_]]

    println("payload planted: " + payload.get())
  }
}
