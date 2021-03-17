package example

import sys.process.{ Process => OsProcess, ProcessBuilder }
import scala.concurrent.Future
import scala.concurrent.ExecutionContext
import java.util.concurrent.Executors
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import java.util.concurrent.TimeUnit

object Processes {
    private implicit lazy val execContext = ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(8))

    def main(args: Array[String]): Unit = {
        val proc = OsProcess(Seq("bash", "-c", "sleep 1; echo 'completed'"))

        val procs = Seq.fill(10)(proc)

        // runProcs(procs)

        try {
            val start = System.currentTimeMillis()

            val exitCodes = runProcsInParallel(procs)

            println(s"all processes completed in ${System.currentTimeMillis() - start} ms")

            val failuresExist = exitCodes.exists(_ != 0)
            if (failuresExist) {
                println("process failures detected")
            }
        } finally {
            println("execution context shutdown sequence started")

            execContext.shutdown()
            execContext.awaitTermination(600, TimeUnit.SECONDS)

            println("execution context shutdown sequence completed")
        }
    }

    private def runProcs(procs: Seq[ProcessBuilder]): Unit = {
        val start = System.currentTimeMillis()

        for ((p, i) <- procs.zipWithIndex) {
            println(s"attempt to start process #${i+1}")

            // TODO use process logger for process output capture
            val exitCode = p.!<

            println(s"process completed with exit code: $exitCode")
        }

        println(s"all processes completed in ${System.currentTimeMillis() - start} ms")
    }

    private def runProcsInParallel(procs: Seq[ProcessBuilder])(implicit execContext: ExecutionContext): Seq[Int] = {
        val exitCodes = Future.traverse(procs.zipWithIndex)(procWithId => Future {
            val (proc, procId) = procWithId

            println(s"attempt to start process #${procId+1}")

            val exitCode = proc.!<

            println(s"process completed with exit code: $exitCode")

            exitCode
        })

        Await.result(exitCodes, Duration.Inf)
    }
}
