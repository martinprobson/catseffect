package net.martinprobson.catseffect

import cats.effect.{IO, IOApp}

import scala.concurrent.duration.DurationInt

object Main extends IOApp.Simple {

    def goShopping(name: String): IO[Unit] =  IO.println(s"$name is going to the shops") >>
            IO.sleep(1.second) >>
            goShopping(name)

    val goShoppingError: IO[Int] = IO(10/0).onError(_ => IO.println("BAD!!!!"))

    val goShoppingLater: IO[Unit] = goShopping("Martin").delayBy(10.seconds)

    val goShoppingInfinite: IO[Unit] = for {
        fred <- goShopping("Fred").start
        joe <- goShopping("Joe").start
        bob <- goShopping("Bob").start
        bill <- goShopping("Bill").start
        _ <- IO.sleep(10.seconds) >>
                fred.cancel >>
                bob.cancel >>
                joe.cancel >>
                bill.cancel
    } yield ()

    override def run: IO[Unit] = goShoppingInfinite
}
