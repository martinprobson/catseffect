package net.martinprobson.cats.applicative

import cats._
import cats.implicits._
import cats.syntax._

object Example2 extends App {

  val toStr = (i: Int) => Some(i.toString)
//  val add = (i: Int) => Some(i + 10)
  val add: Int => Int => Int = a => b => a + b

//  val f = Applicative[Option].pure(add)
  val f = Applicative[Option].pure(add)
  val res1 = f.ap(Some(50)).ap(Some(10))
  println(res1)
  val res2 = f.ap(None).ap(Some(10))
  println(res2)

  val concat: String => String => String = a => b => a ++ b
  val f2 = Applicative[List].pure(concat)
  val res3 = f2.ap(List("AA")).ap(List("BB"))
  println(res3)
  val res4 = f2.ap(List("AA")).ap(List())
  println(res4)
  val res5 = f2.ap(Range(1, 100).toList.map(_.toString)).ap(List("CC", "DD", "EE", "FF", "GG"))
  println(res5)
}
