package org
package assignment

import scala.concurrent.{ExecutionContext, Future}

case class Assignment(randomiseURL: String, count: Int) {

  def execute[T](timeout: Int, mode: Modes.Mode[T])(implicit executionContext: ExecutionContext): Future[Seq[HasUrl]] = {

    import mode.{query, toHttpOps}

    (Future traverse (Seq fill count)(randomiseURL) ) {
      url =>
        query(timeout)(url) map (value => toHttpOps(value) redirectURL()) flatMap {
          target =>
            query(timeout)(target) map(value => toHttpOps(value) nonStandardCode()) map {
              value =>
                OK(target, value)
            } recover {
              case thr:Throwable =>
                Fail(target, thr)
            }
        } recover {
          case thr:Throwable =>
            Fail(url, thr)
        }
    }

  }

}
