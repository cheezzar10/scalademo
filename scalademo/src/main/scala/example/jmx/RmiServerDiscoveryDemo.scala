package example.jmx

import java.rmi.Naming
import javax.management.remote.rmi.{RMIConnector, RMIServer}

import scala.collection.JavaConverters._

object RmiServerDiscoveryDemo {
  def main(args: Array[String]): Unit = {
    val names = Naming.list("//localhost:9010")
    println("names: " + names.mkString(", "))

    val jmxObj = Naming.lookup("//localhost:9010/jmxrmi")
    println("jmx object class: " + jmxObj)

    val jmxObjInterfaces = jmxObj.getClass.getInterfaces
    println("jmx object interfaces: " + jmxObjInterfaces.mkString(", "))

    val jmxConnector = new RMIConnector(
      jmxObj.asInstanceOf[RMIServer],
      Map.empty[String, AnyRef].asJava)
    jmxConnector.connect()

    val jmxConnection = jmxConnector.getMBeanServerConnection
    println("mbean count: " + jmxConnection.getMBeanCount)
  }
}
