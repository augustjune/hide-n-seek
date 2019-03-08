package akka.streams

import java.nio.file.Paths

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{FileIO, Framing, Sink, Source}
import akka.util.ByteString

import scala.concurrent.Await


object FileListening extends App {
  implicit val system = ActorSystem()
  implicit val mat = ActorMaterializer()
  import system.dispatcher

  val inputFile = Paths.get("C:\\Users\\slinkyur\\Personal\\projects\\play-and-run\\src\\main\\resources\\akka\\streams\\input.txt")
  println(inputFile.toFile.exists())

  FileIO.fromPath(inputFile)
    .via(Framing.delimiter(ByteString(System.lineSeparator), Int.MaxValue, allowTruncation = true))
    .map(_.utf8String + "!!!")
    .map(println)
    .to(Sink.ignore)
    .run()

}
