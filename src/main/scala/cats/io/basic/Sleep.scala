package cats.io.basic

import cats.effect.{IO, Timer}

import scala.concurrent.ExecutionContext

object Sleep extends App {
  val timer: Timer[IO] = IO.timer(ExecutionContext.global)

  val printTime: IO[Unit] = for {
    monotonic <- timer.clock.monotonic(concurrent.duration.SECONDS)
    realtime <- timer.clock.realTime(concurrent.duration.SECONDS)
    _ <- IO(println(s"Time from timer: monotonic time - $monotonic; real time - $realtime"))
  } yield ()

  printTime.unsafeRunSync()
  Thread.sleep(5000)
  printTime.unsafeRunSync()
}
