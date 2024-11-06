package net.martinprobson.catseffect

import cats.effect.{IO, IOApp}
import org.typelevel.log4cats.SelfAwareStructuredLogger
import org.typelevel.log4cats.slf4j.Slf4jLogger

import java.util.concurrent.{Executors, ThreadFactory, TimeUnit}
import scala.concurrent.{ExecutionContextExecutor, Future}

object Test extends IOApp.Simple {

  def log: SelfAwareStructuredLogger[IO] = Slf4jLogger.getLogger[IO]

  private val scheduler = Executors.newScheduledThreadPool(1, (r: Runnable) => {
    val t = new Thread(r)
    t.setDaemon(true)
    t
  })
  implicit val ec: ExecutionContextExecutor = scala.concurrent.ExecutionContext.global

  lazy val sleep: Future[Unit] = {
    Future(Thread.sleep(5000L))
  }

  override def run: IO[Unit] = for {
    _ <- log.info("Start")
    _ <- IO.blocking(Thread.sleep(5000L))
    _ <- log.info("Thread.sleep done")
    _ <- IO.async_[Unit] { cb =>
      scheduler.schedule(new Runnable {
        def run(): Unit = {
          println(Thread.currentThread().getName)
          cb(Right(()))
        }
      }, 5000L, TimeUnit.MILLISECONDS)
      ()
    }
    _ <- log.info("schedule done")
    _ <- IO.fromFuture(IO(sleep))
    _ <- log.info("future done")
    } yield ()
}
