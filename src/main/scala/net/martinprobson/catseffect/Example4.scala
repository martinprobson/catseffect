package net.martinprobson.catseffect

import cats.effect.IO
import cats.effect.unsafe.implicits.global

import scala.concurrent.duration.DurationInt

object Example4 extends App {
    val prog1: IO[Unit] = for {
        _ <- IO.println("Hello")
    } yield ()

    val prog2: IO[Long] = IO(println("Hello"))
            .flatMap(_ => IO(println("Martin")).flatMap(_ => IO.pure(List(1,2,3)).flatMap(l => IO(l.sum))))

    val a = prog2.unsafeRunSync()
    println(a)
}
