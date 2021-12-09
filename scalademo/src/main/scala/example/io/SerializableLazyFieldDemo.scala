package example.io

import java.io.{ByteArrayInputStream, ByteArrayOutputStream, ObjectInputStream, ObjectOutputStream}

object SerializableLazyFieldDemo {
  def main(args: Array[String]): Unit = {
    val udf = UserDefinedFunction("hello, ")
    udf("galaxy")

    val baos = new ByteArrayOutputStream()
    val oos = new ObjectOutputStream(baos)

    try {
      oos.writeObject(udf)
    } finally {
      if (oos != null) oos.close()
    }

    val ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray))
    try {
      val readUdf = ois.readObject().asInstanceOf[UserDefinedFunction]

      val result = readUdf("world")
      println("result: " + result)
    } finally {
      ois.close()
    }
  }
}

class PrefixAdder(prefix: String) {
  def addPrefix(str: String): String =
    prefix + str
}

case class UserDefinedFunction(prefix: String) extends (String => String) {
  @transient
  private lazy val prefixAdder = {
    println("prefix adder initialization")
    new PrefixAdder(prefix)
  }

  def apply(str: String): String =
    prefixAdder.addPrefix(str)
}
