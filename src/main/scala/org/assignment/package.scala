package org

import scala.concurrent.{ExecutionContext, Future}
import scala.io.Source._
import scala.language.postfixOps

package object assignment {

  sealed trait HasUrl {

    import RegularExpressions._

    lazy val domain = {
      domainRegex findFirstIn url match {
        case Some(value) => value
        case None => throw new IllegalArgumentException(s"no domain found for url = $url")
      }

    }

    def url: String

  }

  case class OK(url: String, nonStandardCode: Option[Int]) extends HasUrl {

    override def toString = {
      nonStandardCode map {
        code =>
          s"url = $url code = $code"
      } getOrElse s"url = $url"
    }
  }

  case class Fail(url: String, throwable: Throwable) extends HasUrl {

    override def toString = {
      s"url = $url code = $throwable"
    }

  }

  object HttpResponses {

    import dispatch.Res

    import scalaj.http.HttpResponse

    sealed abstract class HttpOps[T](response: T) {

      def redirectURL(): String

      def nonStandardCode(): Option[Int]

    }

    implicit class HttpResponseOps(val response: HttpResponse[Array[Byte]]) extends HttpOps[HttpResponse[Array[Byte]]](response) {

      import RegularExpressions._

      def redirectURL() = {
        (response location) orElse (urlsRegex findFirstIn (fromBytes(response body) toString())) match {
          case Some(url) => url
          case None => throw new IllegalArgumentException("randomise web service didn't provide valid URL")
        }
      }

      def nonStandardCode() = {
        Option(response code) filter (_ != 200)
      }


    }

    implicit class DispatchResponseOps(val response: Res) extends HttpOps[Res](response) {

      import RegularExpressions._

      def redirectURL() = {
        urlsRegex findFirstIn (response getResponseBody) match {
          case Some(url) => url
          case None => throw new IllegalArgumentException("randomise web service didn't provide valid URL")
        }
      }

      def nonStandardCode() = {
        Option(response getStatusCode) filter (_ != 200)
      }

    }

  }

  object RegularExpressions {

    lazy val domainRegex = """(?:https?://)?(?:www\.)?([A-Za-z0-9._%+-]+)/?.*""" r

    lazy val urlsRegex = """(http|ftp|https)://([\w+?\.\w+])+([a-zA-Z0-9\~\!\@\#\$\%\^\&\*\(\)_\-\=\+\\\/\?\.\:\;\'\,]*)?""" r

  }

  object Modes {

    import HttpResponses.HttpOps
    import dispatch.Res

    import scalaj.http.HttpResponse

    sealed trait Mode[T] {

      def query(timeout: Int)(urlString: String)(implicit executionContext: ExecutionContext): Future[T]

      def toHttpOps(response: T): HttpOps[T]
    }

    object DispatchMode extends Mode[Res] {

      import HttpResponses.DispatchResponseOps
      import dispatch._

      def query(timeout: Int)(urlString: String)(implicit executionContext: ExecutionContext) = {
        Http(url(urlString) setFollowRedirects false GET)
      }


      def toHttpOps(response: Res): DispatchResponseOps = {
        new DispatchResponseOps(response)
      }
    }

    object ScalaJMode extends Mode[HttpResponse[Array[Byte]]] {

      import HttpResponses.HttpResponseOps

      import scalaj.http.Http

      def query(timeout: Int)(url: String)(implicit executionContext: ExecutionContext) = {
        Future(Http(url) timeout(timeout, timeout) asBytes)
      }

      def toHttpOps(response: HttpResponse[Array[Byte]]): HttpResponseOps = {
        new HttpResponseOps(response)
      }

    }

  }

}
