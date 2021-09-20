package net.martinprobson.catseffect.producerconsumer

import cats.effect._
import cats.effect.std.Console
import cats.syntax.all._
import collection.immutable.Queue

case class State[F[_], A](queue: Queue[A], takers: Queue[Deferred[F,A]])

object State {
    def empty[F[_],A]: State[F,A] = State(Queue.empty, Queue.empty)
}

object UnBoundedProducerConsumer extends IOApp {
    def consumer[F[_]: Async :Console](id: Int, stateR: Ref[F, State[F, Int]]): F[Unit] = {

        val take: F[Int] =
            Deferred[F, Int].flatMap { taker =>
                stateR.modify {
                    case State(queue, takers) if queue.nonEmpty =>
                        val (i, rest) = queue.dequeue
                        //State(rest, takers) -> Async[F].pure(i)
                        (State(rest, takers) ,Async[F].pure(i))
                    case State(queue, takers) =>
                        State(queue, takers.enqueue(taker)) -> taker.get
                }.flatten
            }

        for {
            i <- take
            _ <- if (i % 10000 == 0) Console[F].println(s"Consumer $id has reached $i items") else Async[F].unit
            _ <- if (i % 10000 == 0) Async[F].delay(println(s"Consumer $id has reached $i items")) else Async[F].unit
//            _ <- Console[F].println(s"Consumer $id has reached $i items")
            _ <- consumer(id, stateR)
        } yield ()
    }

    def producer[F[_]: Sync](id: Int, counterR: Ref[F, Int], stateR: Ref[F, State[F, Int]]): F[Unit] = {

        def offer(i: Int): F[Unit] =
            stateR.modify {
                case State(queue, takers) if takers.nonEmpty =>
                    val (taker, rest) = takers.dequeue
                    State(queue, rest) -> taker.complete(i).void
                case State(queue, takers) =>
                    State(queue.enqueue(i), takers) -> Sync[F].unit
            }.flatten

        for {
            i <- counterR.getAndUpdate(_+1)
            _ <- offer(i)
            //_ <- if (i % 10000 == 0) Console[F].println(s"Producer $id has reached $i items") else Sync[F].unit
            //_ <- if (i % 10000 == 0) Sync[F].delay(println(s"Producer $id has reached $i items")) else Sync[F].unit
            _ <- producer(id, counterR, stateR)
        } yield ()
    }

    override def run(args: List[String]): IO[ExitCode] =
        for {
            stateR <- Ref.of[IO, State[IO, Int]](State.empty[IO, Int])
            counterR <- Ref.of[IO, Int](1)
            producers = List.range(1,11).map(producer(_, counterR, stateR))
            consumers = List.range(1,11).map(consumer(_, stateR))
            res <- (consumers ++ producers)
                    .parSequence.as(ExitCode.Success)
                    .handleErrorWith { t =>
                        Console[IO].errorln(s"Error caught: ${t.getMessage}").as(ExitCode.Error)
                    }
        } yield res
}
