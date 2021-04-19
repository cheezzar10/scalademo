package com.onef

trait Monoid[A] {
  def op(l: A, r: A): A
  def zero: A
}

trait Monad[F[_]] {
  def unit[A](a: => A): F[A]
  def flatMap[A, B](a: F[A])(f: A => F[B]): F[B]
  def map[A, B](a: F[A])(f: A => B) = flatMap(a)(a => unit(f(a)))
}

object Monad {
  def apply[F[_] : Monad]: Monad[F] = implicitly[Monad[F]]

  object lowPriorityImplicits {
    implicit class MonadF[A, F[_] : Monad](value: F[A]) {
      private val M = implicitly[Monad[F]]

      def unit(a: A) = M.unit(a)

      def flatMap[B](f: A => F[B]): F[B] = M.flatMap(value)(f)

      def map[B](f: A => B): F[B] = M.map(value)(f)
    }
  }
}

final case class Boat(direction: Double, position: (Double, Double)) {
  def go(speed: Double, time: Double): Boat = {
    this
  }

  def turn(angle: Double): Boat = {
    this
  }
}

object Boat {
  def go[M[_] : Monad]: (Double, Double) => Boat => M[Boat] =
    (speed, time) => boat => Monad[M].unit(boat.go(speed, time))

  def turn[M[_] : Monad]: Double => Boat => M[Boat] =
    angle => boat => Monad[M].unit(boat.turn(angle))

  import Monad.lowPriorityImplicits._

  def move[A, M[_] : Monad](go: (Double, Double) => A => M[A], turn: Double => A => M[A])(boat: M[A]): M[A] = for {
    a <- boat
    b <- go(10.0, 5.0)(a)
    c <- go(20.0, 10.0)(b)
  } yield c
}

// perform a sequence of computations, writes to "records log" which forms a monoid each computation and
// returns a final result
final case class Writer[W : Monoid, A](run: (A, W)) {
  def compose[B](f: A => Writer[W, B]): Writer[W, B] = Writer {
    val (a, w) = run
    val (b, ww) = f(a).run

    // "appending" to "records log"
    val www = implicitly[Monoid[W]].op(w, ww)

    // returning computed value with new "log"
    (b, www)
  }
}

object Writer {
  // writer instance with initial value and empty "records log"
  def apply[W : Monoid, A](a: => A): Writer[W, A] = Writer((a, implicitly[Monoid[W]].zero))

  // you need kind projector sbt plugin to use syntax like the following instead of type lambda
  // implicit def writerMonad[W : Monoid] = new Monad[Writer[W, ?]]
}

object WriterMonadDemo {
  implicit def writerMonad[W : Monoid] = new Monad[({ type f[x] = Writer[W, x]})#f] {
    override def unit[A](a: => A): Writer[W, A] = Writer(a)

    override def flatMap[A, B](a: Writer[W, A])(f: A => Writer[W, B]): Writer[W, B] = a.compose(f)
  }

  implicit def vectorMonoid[A]: Monoid[Vector[A]] = new Monoid[Vector[A]] {
    override def op(l: Vector[A], r: Vector[A]): Vector[A] = l ++ r

    override def zero: Vector[A] = Vector.empty[A]
  }

  type WriterTracking[A] = Writer[Vector[(Double, Double)], A]

  private def go(speed: Double, time: Double)(boat: Boat): WriterTracking[Boat] =
    new Writer((boat.go(speed, time), Vector(boat.position)))

  def main(args: Array[String]): Unit = {
    import Boat.{move, turn}

    val boat = Boat(0.0, (0.0, 0.0))

    val result = move(go, turn[WriterTracking])(Writer(boat)).run

    println("result: " + result)
  }
}
