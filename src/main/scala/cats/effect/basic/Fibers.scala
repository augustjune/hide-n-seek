package cats.effect.basic

import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._
import scala.concurrent.duration._

object Fibers extends IOApp {

  val launchMissiles2 = IO(println("Lounched")) *> IO.raiseError{println("Landed2"); new Exception("boom!")}
  val launchMissiles = IO(println("Lounched")) *> IO.raiseError{println("Landed"); new Exception("boom!")}
  val runToBunker = IO.sleep(5 second) *> IO(println("To the bunker!!!"))

  val sentence: IO[Unit] = (IO(println("First")), IO(println("Second"))).parTupled.map(_ => ())

  def run(args: List[String]): IO[ExitCode] = sentence.map(_ => ExitCode.Success)

  def run2(args: List[String]): IO[ExitCode] = for {
    fiber <- launchMissiles.start
    _ <- runToBunker.handleErrorWith { error =>
      // Retreat failed, cancel launch (maybe we should
      // have retreated to our bunker before the launch?)
      fiber.cancel *> IO.raiseError(error)
    }
    aftermath <- fiber.join
  } yield aftermath
}
