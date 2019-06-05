package fs2.basics

import fs2.{Pure, Stream}

object Flatmap extends App {

  def flatMapRepeat[F[_], A](base: Stream[F, A]): Stream[F, A] =
    base.flatMap(Stream(_).repeat)

  def empty: Stream[Pure, Int] = flatMapRepeat(Stream.empty)

  def singleElement: Stream[Pure, Int] = flatMapRepeat(Stream(9))

  def multiElement: Stream[Pure, Int] = flatMapRepeat(Stream(1, 2))

  println(multiElement.find(_ != 1).toList)
}
