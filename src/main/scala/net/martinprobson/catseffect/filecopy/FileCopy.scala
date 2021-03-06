package net.martinprobson.catseffect.filecopy

import cats.data.EitherT
import cats.effect.{ExitCode, IO, IOApp, Resource}
import cats.effect.std.Console
import net.martinprobson.catseffect.filecopy.DomainError.{SourceAndDestinationTheSame, SourceFileDoesNotExist, WrongArgumentCount}

import java.io._

object FileCopy extends IOApp {

    def run(args: List[String]): IO[ExitCode] = {
        runCopy(args).flatMap({
            case Left(err) => Console[IO].errorln(err) >> IO(ExitCode.Error)
            case Right((orig, dest, count)) => IO(println(s"$count bytes copied from ${orig.getPath} to ${dest.getPath}")) >> IO(ExitCode.Success)
        })
    }

    def runCopy(args: List[String]): IO[Either[DomainError, (File, File, Long)]] =
        (for {
            _ <- EitherT(validate(args))
            orig = new File(args.head)
            dest = new File(args.tail.head)
            count <- EitherT(copy(orig, dest))
        } yield (orig, dest, count)).value

    def validate(args: List[String]): IO[Either[DomainError, Unit]] = {
        if (args.length != 2)
            IO(Left(WrongArgumentCount(s"Expected two arguments but got ${args.length}")))
        else if (args.head == args.tail.head)
            IO(Left(SourceAndDestinationTheSame))
        else {
            val source = new File(args.head)
            if (!source.exists()) IO(Left(SourceFileDoesNotExist))
            else
                IO(Right(()))
        }
    }

    def copy(origin: List[File], dest: File): IO[Either[DomainError, Long]] = origin match {
        case h :: tl => copy(h, new File(dest.getAbsolutePath + File.separator + h.getName)) >> copy(tl, dest)
        case Nil => IO(Right(0L))
    }

    def mkdir(dir: File): IO[Either[DomainError, Unit]] = {
        IO.pure(dir.mkdir()).flatMap(r =>
            if (r)
                IO(Right(()))
            else {
                IO.raiseError(new Exception(s"Cannot make directory ${dir.getPath}"))
            })
    }

    def copy(origin: File, destination: File): IO[Either[DomainError, Long]] = {
        if (origin.isDirectory)
                mkdir(destination) >> copy(origin.listFiles().toList, destination)
        else {
            inputOutputStreams(origin, destination).use { case (in, out) =>
                transfer(in, out)
            }
        }
    }

    def transfer(origin: InputStream, destination: OutputStream): IO[Either[DomainError, Long]] =
        transmit(origin, destination, new Array[Byte](1024 * 10), 0)

    def transmit(origin: InputStream, destination: OutputStream, buffer: Array[Byte], acc: Long): IO[Either[DomainError, Long]] =
        for {
            amount <- IO.blocking(origin.read(buffer, 0, buffer.length))
            count <- if (amount > -1) IO.blocking(destination.write(buffer, 0, amount)) >>
                    transmit(origin, destination, buffer, acc + amount)
            else IO(Right(acc))
        } yield count

    def inputStream(f: File): Resource[IO, FileInputStream] =
        Resource.make {
            IO.blocking(new FileInputStream(f))                        // Acquire
        } { inStream =>
            IO.blocking(inStream.close()).handleErrorWith(_ => IO.unit)   // Release
        }

    def outputStream(f: File): Resource[IO, FileOutputStream] =
        Resource.make {
            IO.blocking(new FileOutputStream(f))
        } { outStream =>
            IO.blocking(outStream.close()).handleErrorWith(_ => IO.unit)
        }

    def inputOutputStreams(in: File, out: File): Resource[IO, (InputStream, OutputStream)] =
        for {
            inStream <- inputStream(in)
            outStream <- outputStream(out)
        } yield (inStream, outStream)
}
