package net.martinprobson.catseffect

import cats.effect.IO
import cats.effect.std.Random
import cats.effect.unsafe.implicits.global

import scala.concurrent.duration.DurationInt

//object Main extends IOApp.Simple {
object Example1 extends App {
    //val run = IO.println("Hello world!")

    def sleepPrint1(word: String, name: String, rand: Random[IO]): IO[Unit] =
        for {
            delay <- rand.betweenInt(200, 700)
            _ <- IO.sleep(delay.millis)
            _ <- IO.println(s"$word, $name")
        } yield ()

    def sleepPrint2(word: String, name: String, rand: Random[IO]): IO[Unit] = {
        rand.betweenInt(200, 700).flatMap(delay => IO.sleep(delay.millis).flatMap(_ => IO.println(s"$word, $name")))
    }

    def sleepPrint3(word: String, name: String, rand: Random[IO]): IO[Unit] = {
        rand.betweenInt(200, 700).flatMap(delay => IO.sleep(delay.millis) >> IO.println(s"$word, $name"))
    }

    def sleepPrint4(word: String, name: String, rand: Random[IO]): IO[Unit] =
        for {
            delay <- rand.betweenInt(200, 700)
            _ <- IO.sleep(delay.millis) >> IO.println(s"$word, $name")
        } yield ()

    def sleepPrint(word: String, name: String, rand: Random[IO]): IO[Unit] = sleepPrint1(word,name,rand)



    val run: IO[Unit] = for {
        fred <- IO.ref(0)
        rand <- Random.scalaUtilRandom[IO]
        _ <- IO.println("What is your name? ")
        //name <- IO.readLine
        name <- IO.pure("Martin")
        name2 <- IO.pure(1).memoize

        english <- sleepPrint("Hello", name, rand).foreverM.start
        french <- sleepPrint("Bonjour", name, rand).foreverM.start
        spanish <- sleepPrint("Hola", name, rand).foreverM.start
        oldEnglish <- sleepPrint("ahhh", name, rand).foreverM.start

        //_ <- IO.sleep(5.seconds)
        //_ <- english.cancel >> french.cancel >> spanish.cancel >> oldEnglish.cancel
        _ <- IO.sleep(5.seconds) >> english.cancel >> french.cancel >> spanish.cancel >> oldEnglish.cancel
    } yield ()

    run.unsafeRunSync()
    run.unsafeRunAsync(e => e match {
        case Left(err) => println(s"error $err")
        case Right(r) => println(s"Done $r")
    })
}
