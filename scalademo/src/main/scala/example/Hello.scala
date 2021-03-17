package example

object Hello {
	def main(args: Array[String]): Unit = {
		perform()
	}

	private def perform(): Unit = {
		println("hello, world!")

		val ex = new RuntimeException("boom")

		throw ex
	}
}
