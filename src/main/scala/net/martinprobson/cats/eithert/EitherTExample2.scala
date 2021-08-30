package net.martinprobson.cats.eithert

import cats.data.EitherT
import cats.effect.{ExitCode, IO, IOApp}
import cats.{Applicative, Monad}
import net.martinprobson.cats.eithert.AuthenticationError.{BannedUser, WrongPassword, WrongUserName}

object EitherTExample2 extends IOApp {
    case class User(name: String, password: String, subscriptionActive: Boolean)

    def findUserByName[F[_]: Applicative](username: String): F[Either[AuthenticationError, User]] = {
        val userMap = Map("User1" -> User("User1", "password", subscriptionActive = true),
            "User2" -> User("User2", "password", subscriptionActive = false))
        userMap.get(username) match {
            case Some(user) => Applicative[F].pure(Right(user))
            case None => Applicative[F].pure(Left(WrongUserName))
        }
    }

    def checkPassword[F[_] : Applicative](user: User, password: String): F[Either[AuthenticationError, Unit]] =
        if (user.password == password)
            Applicative[F].pure(Right(()))
        else
            Applicative[F].pure(Left(WrongPassword))

    def checkSubscription[F[_]: Applicative](user: User): F[Either[AuthenticationError, Unit]] =
        if (user.subscriptionActive) Applicative[F].pure(Right(())) else Applicative[F].pure(Left(BannedUser))

    def checkUserStatus[F[_]: Applicative](user: User): F[Either[AuthenticationError, Unit]] =
        Applicative[F].pure(Right(()))

    def authenticate[F[_] : Monad](userName: String, password: String): F[Either[AuthenticationError, User]] = {
        (for {
            user <- EitherT(findUserByName(userName))
            _ <- EitherT(checkPassword(user, password))
            _ <- EitherT(checkSubscription(user))
            _ <- EitherT(checkUserStatus(user))
        } yield user).value
    }

    override def run(args: List[String]): IO[ExitCode] = {
        authenticate[IO]("User1", "password").flatMap({
            case Left(err) => IO(println(err)) >> IO(ExitCode.Error)
            case Right(res) => IO(println(res)) >> IO(ExitCode.Success)
        })
    }
}
