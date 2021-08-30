package net.martinprobson.cats.eithert

import cats.data.EitherT
import cats.effect.{ExitCode, IO, IOApp}
import net.martinprobson.cats.eithert.AuthenticationError.{BannedUser, WrongPassword, WrongUserName}

object EitherTExample extends IOApp {
    case class User(name: String, password: String, subscriptionActive: Boolean)

    def findUserByName(username: String): IO[Either[AuthenticationError, User]] = {
        val userMap = Map("User1" -> User("User1", "password", subscriptionActive = true),
            "User2" -> User("User2", "password", subscriptionActive = false))
        userMap.get(username) match {
            case Some(user) => IO(Right(user))
            case None => IO(Left(WrongUserName))
        }
    }

    def checkPassword(user: User, password: String): IO[Either[AuthenticationError, Unit]] =
        if (user.password == password)
            IO(Right(()))
        else
            IO(Left(WrongPassword))

    def checkSubscription(user: User): IO[Either[AuthenticationError, Unit]] =
        if (user.subscriptionActive) IO(Right(())) else IO(Left(BannedUser))

    def checkUserStatus(user: User): IO[Either[AuthenticationError, Unit]] =
        IO(Right(()))

    def authenticate(userName: String, password: String): IO[Either[AuthenticationError, User]] = {
        (for {
            user <- EitherT(findUserByName(userName))
            _ <- EitherT(checkPassword(user, password))
            _ <- EitherT(checkSubscription(user))
            _ <- EitherT(checkUserStatus(user))
        } yield user).value
    }

    override def run(args: List[String]): IO[ExitCode] =
        authenticate("User1", "password").flatMap({
            case Left(err) => IO(println(err)) >> IO(ExitCode.Error)
            case Right(res) => IO(println(res)) >> IO(ExitCode.Success)
        })
}
