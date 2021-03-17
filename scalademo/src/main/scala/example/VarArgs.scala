package example

class VarArgs {
    def invoke(args: String*) {
        val argsStr = args.mkString(",")
        println(s"method called with parameters: $argsStr")
    }
}

object VarArgs {
    def main(args: Array[String]): Unit = {
        val va = new VarArgs()

        va.invoke()
    }
}