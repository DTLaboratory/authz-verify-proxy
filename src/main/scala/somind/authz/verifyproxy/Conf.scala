package somind.authz.verifyproxy

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.util.Timeout
import com.typesafe.config.{Config, ConfigFactory}
import com.typesafe.scalalogging.LazyLogging
import somind.authz.verifyproxy.observe.Observer

import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration.{Duration, FiniteDuration}

object Conf extends LazyLogging {

  implicit val system: ActorSystem = ActorSystem("auth-verify-system")
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  val conf: Config = ConfigFactory.load()

  val healthToleranceSeconds: Int = conf.getString("main.healthToleranceSeconds").toInt

  def requestDuration: Duration = {
    val t = "120 seconds"
    Duration(t)
  }

  implicit def requestTimeout: Timeout = {
    val d = requestDuration
    FiniteDuration(d.length, d.unit)
  }

  val observer: ActorRef = system.actorOf(Props[Observer], "observer")

  val jwksHost: String = conf.getString("main.jwksHost")
  val remoteHost: String = conf.getString("main.remoteHost")
  val remotePort: Int = conf.getInt("main.remotePort")

}
