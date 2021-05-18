package benchmarks

import org.openjdk.jmh.annotations.Mode._
import org.openjdk.jmh.annotations._

import java.util.concurrent.TimeUnit
import scala.util.Random

// sbt "jmh:run ArraySortBenchmark"

@BenchmarkMode(Array(Throughput))
@Warmup(iterations = 3, time = 5, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 10, timeUnit = TimeUnit.SECONDS)
@Fork(value = 1, warmups = 1, jvmArgs = Array("-Xms1g", "-Xmx1g"))
class ArraySortBenchmark {
  import ArraySortBenchmark._

  @Benchmark
  def sortArray(state: ArraySortBenchmarkState): Array[Int] = {
    // println("sorting array: " + array.mkString("[", ",", "]"))
    val result = state.array.sorted
    // println("sorted array: " + result.mkString("[", ",", "]"))
    result
  }
}

object ArraySortBenchmark {
  // simplest possible setup, all iterations share the same array
  // val array = Random.shuffle((1 until 10).toVector).toArray

  // or the more elaborate one, with benchmark state passed to each benchmark
  @State(Scope.Benchmark)
  class ArraySortBenchmarkState {
    var array: Array[Int] = Array.empty

    @Param(Array("10", "100", "1000", "10000"))
    var arrayLength = 0

    @Setup(Level.Trial)
    def setup(): Unit = {
      array = Random.shuffle((0 until arrayLength).toVector).toArray
    }
  }
}
