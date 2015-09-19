package org.assignment

import java.util.concurrent.TimeUnit

import com.typesafe.config.ConfigFactory

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext}
import scala.language.postfixOps

object Main extends App {

  implicit val executionContext = ExecutionContext.Implicits.global

  val config = ConfigFactory load()

  val httpTimeout = config getInt "assignment.httpTimeout"

  val requestCount = config getInt "assignment.requestsCount"

  val printFailed = config getBoolean "assignment.printFailed"

  val executionTimeout = config getInt "assignment.executionTimeout"

  val url = config getString "assignment.url"

  val executionDuration = Duration(executionTimeout, TimeUnit MILLISECONDS)

  val mode = config getString "assignment.mode" match {
    case "dispatch" => Modes DispatchMode
    case "scalaj" => Modes ScalaJMode
    case other => throw new IllegalArgumentException(s"unknown mode $other")
  }

  val list = Await result(Assignment(url, requestCount) execute(httpTimeout, mode), executionDuration) sortBy (value => (value domain)(0))

  println({
    if (printFailed)
      list
    else
      list filter (value => (value isInstanceOf)[OK])
  } mkString "\n")

  sys exit 0
}