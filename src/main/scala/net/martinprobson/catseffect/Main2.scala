package net.martinprobson.catseffect

import cats.effect.{IO, IOApp}
import org.typelevel.log4cats.SelfAwareStructuredLogger
import org.typelevel.log4cats.slf4j.Slf4jLogger

object Main2 extends IOApp.Simple {

  def log: SelfAwareStructuredLogger[IO] = Slf4jLogger.getLogger[IO]

  override def run: IO[Unit] = for {
    _ <- log.info("Start")
    result <- IO.pure(true).ifM(IO.pure("I succeeded!"), IO.pure("Failure"))
    _ <- log.info(s"Result is $result")
    result <- IO.pure(false).ifM(IO.pure("I succeeded!"), IO.pure("Failure"))
    _ <- log.info(s"Result is $result")
    _ <- log.info("End")
  } yield ()
}
