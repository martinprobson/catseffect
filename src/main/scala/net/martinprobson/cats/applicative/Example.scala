package net.martinprobson.cats.applicative

import cats._
import cats.implicits._
import cats.syntax._

case class Connection(username: String, password: String, url: String)

object Example extends App {

    val userName: Either[String, String] = Right("Martin")
    //val password: Either[String, String] = Right("password")
    val password: Either[String, String] = Left(" Invalid password ")
    //val url: Either[String, String] = Right("a.url.goes.here")
    val url: Either[String, String] = Left(" Invalid URL ")

    def attemptConnect(username: String, password: String, url: String): Either[String, Connection] =
        Right(Connection(username, password, url))

    def execute(connection: Connection): Unit = {
        println(s"Using conection $connection")
    }
    (userName, password, url).parMapN(Connection.apply) match {
        case Left(err) => println(err)
        case Right(c) => execute(c)
    }
}
