package example.io

import java.io.ObjectInputStream
import java.util
import javax.naming.Context
import javax.naming.directory.{DirContext, InitialDirContext}

/*
class Payload extends Serializable {
  println("payload loaded")

  private def readObject(in: ObjectInputStream): Unit = {
    in.defaultReadObject()

    println("boom")
  }
}
 */

object LdapDemo {
  def main(args: Array[String]): Unit = {
    val env = new util.Hashtable[String, String]()
    env.put(Context.SECURITY_PRINCIPAL, "cn=Directory Manager")
    env.put(Context.SECURITY_CREDENTIALS, "password")

    val initialContext = new InitialDirContext(env)
    val context = initialContext.lookup("ldap://localhost:1389/dc=springrecipes,dc=com").asInstanceOf[DirContext]

    // val payload = new Payload()
    // context.rebind("cn=Payload2,dc=springrecipes,dc=com", payload)

    val plantedPayload = initialContext.lookup("ldap://localhost:1389/cn=Payload,dc=springrecipes,dc=com")
    println("planted payload class: " + plantedPayload.getClass)

    val entries = context.list("")

    while (entries.hasMore) {
      val entry = entries.next()
      println("entry: " + entry)

      val obj = context.lookup(entry.getName)
    }

    // val attrs = context.getAttributes("o=example")
    // val attrsEnum = attrs.getAll
  }
}
