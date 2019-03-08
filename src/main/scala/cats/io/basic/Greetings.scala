package cats.io.basic

import cats.effect.IO

import scala.io.StdIn

object Greetings extends App {

  def printLn(s: String): IO[Unit] = IO(println(s))
  def readLn: IO[String] = IO(StdIn.readLine())

  val greetings = for {
    _ <- printLn("What is your name?")
    name <- readLn
    _ <- printLn(s"Hello, $name")
  } yield ()

  greetings.unsafeRunSync
}
