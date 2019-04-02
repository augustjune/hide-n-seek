package cats.effect.basic

import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._
import concurrent.duration._

object ParallelExecution extends IOApp {
  val start = System.currentTimeMillis()

  def runAsyncWithTimer: IO[ExitCode] = for {
    _ <- (timer.sleep(5 seconds) *> IO(println(s"[Time ${(System.currentTimeMillis() - start)/1000.0}s]: 1"))).start
    _ <- (timer.sleep(1 seconds) *> IO(println(s"[Time ${(System.currentTimeMillis() - start)/1000.0}s]: 2"))).start
    _ <- (timer.sleep(10 seconds) *> IO(println(s"[Time ${(System.currentTimeMillis() - start)/1000.0}s]: 3"))).start
    res <- runAsyncWithTimer
  } yield res

  def runAsync: IO[ExitCode] = for {
    _ <- IO{Thread.sleep(5000); println(s"[Time ${(System.currentTimeMillis() - start)/1000.0}s]: 1")}.start
    _ <- IO{Thread.sleep(1000); println(s"[Time ${(System.currentTimeMillis() - start)/1000.0}s]: 2")}.start
    _ <- IO{Thread.sleep(10000); println(s"[Time ${(System.currentTimeMillis() - start)/1000.0}s]: 3")}.start
    res <- runAsync
  } yield res

  def runSyncWithTimer: IO[ExitCode] = for {
    _ <- timer.sleep(5 seconds) *> IO(println(s"[Time ${(System.currentTimeMillis() - start)/1000.0}s]: 1"))
    _ <- timer.sleep(1 seconds) *> IO(println(s"[Time ${(System.currentTimeMillis() - start)/1000.0}s]: 2"))
    _ <- timer.sleep(10 seconds) *> IO(println(s"[Time ${(System.currentTimeMillis() - start)/1000.0}s]: 3"))
    _ <- IO(println("Loop"))
    res <- runSyncWithTimer
  } yield res

  def runSync: IO[ExitCode] = for {
    _ <- IO{Thread.sleep(5000); println(s"[Time ${(System.currentTimeMillis() - start)/1000.0}s]: 1")}
    _ <- IO{Thread.sleep(1000); println(s"[Time ${(System.currentTimeMillis() - start)/1000.0}s]: 2")}
    _ <- IO{Thread.sleep(10000); println(s"[Time ${(System.currentTimeMillis() - start)/1000.0}s]: 3")}
    res <- runSync
  } yield res

  def run(args: List[String]): IO[ExitCode] = runSyncWithTimer
}
