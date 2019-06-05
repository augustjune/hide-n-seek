package cats.effect.io.running

import java.util.concurrent.{Executors, ScheduledExecutorService, TimeUnit}

import cats.effect.{ExitCode, IO, IOApp}

import scala.concurrent.duration._

object Cancelable extends IOApp {

  class InterruptedException extends Throwable

  val ec = Executors.newSingleThreadScheduledExecutor()

  def interestingIO(ec: ScheduledExecutorService): IO[Unit] = IO.cancelable { cb =>
    // callback function will only be called once
    val handle = ec.scheduleAtFixedRate(() => cb(Right(println("Tick"))), 0, 1, TimeUnit.SECONDS)

    IO {
      handle.cancel(true)
      println("Ticking was cancelled")
    }
  }

  def run(args: List[String]): IO[ExitCode] = for {
    canc <- interestingIO(ec).runCancelable {
      case Left(t) => IO(println("Exception occurred"))
      case Right(v) => IO(println("Ended gracefully"))
    }.toIO

    _ <- IO(println("Going to wait a couple of sec (5)"))
    _ <- IO.sleep(5 seconds)
    _ <- canc
    _ <- IO(println("Going to wait a couple of sec (5)"))
    _ <- IO.sleep(5 seconds)
    _ <- IO(ec.shutdown())
  } yield ExitCode.Success
}
