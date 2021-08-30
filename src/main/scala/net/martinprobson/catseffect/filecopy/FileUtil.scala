package net.martinprobson.catseffect.filecopy

import cats.effect.{ExitCode, IO, IOApp, Resource}

import scala.io.{BufferedSource, Source}

object FileUtil extends IOApp {
    def inputFile(name: String): Resource[IO, BufferedSource] =
        Resource.make {
            IO.blocking(Source.fromFile(name))                             // Acquire
        } { bufferedSource =>
            IO.blocking(bufferedSource.close()).handleErrorWith(t => IO.println(t))   // Release
        }

    def readFile(source: BufferedSource): IO[String] =
        IO.blocking(source.getLines().mkString("\n"))

    override def run(args: List[String]): IO[ExitCode] =
        for {
            _ <- if (args.length < 1) IO.raiseError(new IllegalArgumentException("Need filename"))
            else IO.unit
            contents <- inputFile(args.head).use( bs => readFile(bs))
            _ <- IO.println(contents)
        } yield ExitCode.Success
}
