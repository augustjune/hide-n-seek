package cats.effect.tutorial.part1

import java.io._
import cats.implicits._

import cats.effect.{IO, Resource}

object Copy {
  def copy(origin: File, destination: File): IO[Long] =
    inputOutputStreams(origin, destination).use {
      case (in, out) => transfer(in, out)
    }

  def transfer(origin: InputStream, destination: OutputStream): IO[Long] =
    for {
      buffer <- IO(new Array[Byte](1024 * 10))
      total <- transmit(origin, destination, buffer, 0L)
    } yield total

  def transfer2(origin: InputStream, destination: OutputStream): IO[Long] = {
    val buffer = new Array[Byte](1024 * 10)
    transmit(origin, destination, buffer, 0)
  }

  def transmit(origin: InputStream, destination: OutputStream, buffer: Array[Byte], acc: Long): IO[Long] =
    for {
      amount <- IO(origin.read(buffer))
      count <-
        if(amount > -1) IO(destination.write(buffer, 0, amount)) >> transmit(origin, destination, buffer, acc + amount)
        else IO.pure(acc)
    } yield count

  // Is there a need to wrap every buffering process into IO as it is shown above?
  def transmitPlain(origin: InputStream, destination: OutputStream, buffer: Array[Byte], acc: Long): IO[Long] = IO {
    val buf = new Array[Byte](1024 * 10)
    var count = 0L
    var amount = 0
    while(amount > -1) {
      amount = origin.read(buf)
      destination.write(buf, 0, amount)
      count += amount
    }
    count
  }

  def inputStream(f: File): Resource[IO, FileInputStream] =
    Resource.fromAutoCloseable(IO(new FileInputStream(f)))

  def outputStream(f: File): Resource[IO, FileOutputStream] =
    Resource.fromAutoCloseable(IO(new FileOutputStream(f)))

  def inputOutputStreams(in: File, out: File): Resource[IO, (InputStream, OutputStream)] =
    for {
      inStream <- inputStream(in)
      outStream <- outputStream(out)
    } yield (inStream, outStream)

}
