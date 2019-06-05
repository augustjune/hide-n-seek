package cats.effect.io.initialization

import cats.effect.IO

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

object Async {

  /**
    * Kind of callback trait, where one of the methods is called after the operation finished
    * either with success of type `T` or failure (Throwable)
    */
  trait ResultHandler[T] {
    def onError(t: Throwable): Unit
    def onSuccess(t: T): Unit
  }

  /**
    * Asynchronous channel
    * Each method returns unit immediately and handles
    * the result of execution with provided instance of `ResultHandler`
    */
  trait Channel {
    def sendBytes(chunk: Array[Byte], resultHandler: ResultHandler[Unit]): Unit

    def receiveBytes(resultHandler: ResultHandler[Array[Byte]]): Unit
  }

  def send(c: Channel, chunk: Array[Byte]): IO[Unit] = {
    IO.async { cb =>
      c.sendBytes(chunk, new ResultHandler[Unit] {
        def onError(t: Throwable): Unit = cb(Left(t))

        def onSuccess(v: Unit): Unit = cb(Right(v))
      })
    }
  }

  def receive(c: Channel): IO[Array[Byte]] = {
    IO.async { cb =>
      c.receiveBytes(new ResultHandler[Array[Byte]] {
        def onError(t: Throwable): Unit = cb(Left(t))

        def onSuccess(t: Array[Byte]): Unit = cb(Right(t))
      })
    }
  }
}
