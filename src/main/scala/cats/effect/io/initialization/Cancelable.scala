package cats.effect.io.initialization

import java.io.{BufferedReader, File, FileInputStream, InputStreamReader}
import java.util.concurrent.atomic.AtomicBoolean

import cats.effect.IO

import scala.concurrent.ExecutionContext
import scala.util.control.NonFatal

object Cancelable {

  def unsafeFileToString(file: File, isActive: AtomicBoolean): String = {
    val in = new BufferedReader(
      new InputStreamReader(new FileInputStream(file), "utf-8"))

    try {
      // Interruptible loop
      val sb = new StringBuilder()
      var hasNext = true
      while (hasNext && isActive.get()) {
        hasNext = false
        val line = in.readLine()
        if (line != null) {
          hasNext = true
          sb.append(line)
        }
      }
      sb.toString
    } finally {
      in.close()
    }
  }

  def safeFileToString(file: File)(implicit ec: ExecutionContext): IO[String] =
    IO.cancelable[String] { cb =>
      val isActive = new AtomicBoolean(true)

      try {
        cb(Right(unsafeFileToString(file, isActive)))
      } catch {
        case NonFatal(e) =>
          cb(Left(e))
      }

      // On cancel, signal it
      IO(isActive.set(false))
    }
}
