package net.martinprobson.catseffect

import cats.effect.IO
import cats.effect.std.Random
import cats.effect.unsafe.implicits.global

import scala.concurrent.duration.DurationInt

object Example3 extends App {
    lazy val loop: IO[Unit] = IO.println("hello") >> loop
    val fallback: IO[Unit] = IO.println("Fallback")

    loop.timeoutTo(5.seconds,fallback).unsafeRunSync()


}
