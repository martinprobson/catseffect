package net.martinprobson.catseffect

import cats.effect.{IO, IOApp}
import cats.effect.IOApp.Simple
import cats.effect.std.Random
import cats.effect.unsafe.implicits.global

import scala.concurrent.duration.DurationInt

object Example2 extends App {
    val prog1: IO[Unit] = for {
        ctr <- IO.ref(0)

        wait = IO.sleep(1.second)
        poll = wait >> ctr.get

        _ <- poll.flatMap(IO.println(_)).foreverM.start
        _ <- (wait >> ctr.update(_+10)).foreverM.void
    } yield ()

    val prog2: IO[Unit] = {
        IO.ref(0).flatMap(ctr => {
            val wait = IO.sleep(1.second)
            val poll = wait >> ctr.get

            poll.flatMap(i => IO.println(i)).foreverM.start >>
                    (wait >> ctr.update(_ + 10)).foreverM.void
        })
    }
    prog1.unsafeRunSync()
}
