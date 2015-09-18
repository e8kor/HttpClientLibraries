package org
package system

import akka.actor.{Actor, ActorLogging, ActorRef, Terminated}
import com.typesafe.config.Config

import scala.language.postfixOps

class Terminator(app: ActorRef)(implicit config: Config) extends Actor with ActorLogging {

  context watch app

  def receive = {
    case Terminated(actorRef) ⇒
      log info "root executor terminated, suite completed"
  }

}