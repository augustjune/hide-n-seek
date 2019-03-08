package cats.io.typeclasses

import cats.effect.IO

import scala.util.{Random, Try}


object Async extends App {

  def divide(divisor: Int)(base: Int): Int =
    if (base % divisor == 0) base / divisor
    else throw new IllegalArgumentException(s"Base [$base] is not dividable by divisor [$divisor]")

  def randomInt(): Int = Random.nextInt()

  val asyncIO = IO.async[Int] { cbr =>
    val n = randomInt()
    val devisor = 2
    println(s"Deviding $n by $devisor")
    cbr(Try(divide(devisor)(n)).toEither)
  }

  asyncIO.unsafeRunAsync {
    case Left(e) => println("Exception raised!!! " + e)
    case Right(v) => println(s"Hello, $v")
  }


  println("Works")
}
