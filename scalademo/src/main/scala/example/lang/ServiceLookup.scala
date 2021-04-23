package example.lang

import scala.reflect.ClassTag

class ApplicationRegistry {
    val services = Map[Class[_], AnyRef](classOf[Launcher] -> new Launcher())

    def lookup[T](implicit tag: ClassTag[T]): T = {
        val clazz = tag.runtimeClass

        println(s"performing lookup: $clazz")

        services(tag.runtimeClass).asInstanceOf[T]
    }
}

class Launcher {
    def launch(): Unit = {
        println("launching")
    }
}

trait Service {
    type S

    def perform(): Unit
}

class ServiceImpl(val appRegistry: ApplicationRegistry) extends Service {
    type S = Launcher

    def perform(): Unit = {
        println("using launcher")

        val launcher = appRegistry.lookup[S]
    }
}

object ServiceLookupDemo {
    def main(args: Array[String]): Unit = {
        println("started")

        val appRegistry = new ApplicationRegistry()

        val service = new ServiceImpl(appRegistry)
        service.perform()
    }
}
