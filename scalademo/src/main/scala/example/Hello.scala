package example

import scala.concurrent.Future

object Hello {
	def main(args: Array[String]): Unit = {
		try {
			perform()
		} catch {
			case ex: Exception => {
				print("failed: ")
				ex.printStackTrace(Console.out)
			}
		}

		try {
			performScan()
		} catch {
			case ex: Throwable => {
				print("background scan failed: ")
				ex.printStackTrace(Console.out)
			}
		}
	}

	private def perform(): Unit = {
		println("hello, world!")

		val ex = new RuntimeException("boom")

		throw ex
	}

	private def performScan(): Future[Unit] = ???
}
