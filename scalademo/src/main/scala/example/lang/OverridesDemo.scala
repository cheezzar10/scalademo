package example.lang

import java.util.UUID

class Frame[F <: Frame[F]](val rows: Vector[Map[String, Any]] = Vector.empty) {
  override def toString = rows.toString
}

trait FrameService {
  def process[F <: Frame[F]](frame: Frame[F], id: Int = 1): Int
}

class FrameServiceImpl(val uid: String = UUID.randomUUID().toString()) extends FrameService {
  override def process[F <: Frame[F]](frame: Frame[F], id: Int): Int = {
    println(s"processing frame: $frame with id: $id")

    0
  }
}

object OverridesDemo {
  def main(args: Array[String]): Unit = {
    val service = new FrameServiceImpl()

    val frame = new Frame()
    service.process(frame)
  }
}
