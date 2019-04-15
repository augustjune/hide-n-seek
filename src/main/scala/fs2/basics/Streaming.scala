package fs2.basics

import fs2.Stream
import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._
import scala.concurrent.duration._

object Streaming extends IOApp {


  val s = Stream(2, 3, 1, 5, 3, 4, 2, 1).zipWithIndex.evalMap {
    case (x, i) => IO.sleep(x.second) *> IO(println(s"Evaluated $i.: $x"))
  }

  def run(args: List[String]): IO[ExitCode] = for {
    _ <- s.parEvalMapUnordered(10)(_ => IO.unit).compile.drain
  } yield ExitCode.Success
}
