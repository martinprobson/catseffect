package net.martinprobson.cats.applicative

import cats._
import cats.implicits._
import cats.syntax._

object Example2 extends App {

    val toStr = (i: Int) =>  Some(i.toString)
    val add = (i: Int) => Some(i + 10)

    val f = Applicative[Option].pure(add)
    val res1 = f.ap(Some(50))
    println(res1)
    val res2 = f.ap(None)
    println(res2)
}
