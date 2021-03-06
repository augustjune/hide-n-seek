package cats.effect.tutorial.part1

import java.io.File

import cats.effect.{ExitCode, IO, IOApp}

object Main extends IOApp {
  def run(args: List[String]): IO[ExitCode] =
    for {
      _ <-
        if (args.length < 2) IO.raiseError(new IllegalArgumentException("Need origin and destination files"))
        else IO.unit
      orig = new File(args(0))
      dest = new File(args(1))
      count <- Copy.copy(orig, dest)
      _ <- IO(println(s"$count bytes copied from ${orig.getPath} to ${dest.getPath}"))
    } yield ExitCode.Success
}
