package somind.auth.verifyproxy.routes

import akka.http.scaladsl.model._
import akka.http.scaladsl.server._
import com.typesafe.scalalogging.LazyLogging
import somind.auth.verifyproxy.HttpSupport
import somind.auth.verifyproxy.models._

object VerifyRoute
    extends JsonSupport
    with LazyLogging
    with Directives
    with HttpSupport {

  def apply: Route = {
    extractRequest { request =>
      logger.debug(s"headers: ${request.headers}")
      logger.debug(s"method: ${request.method}")
      path(Segments) { segs =>
        logger.debug(s"segments: $segs")
        complete(StatusCodes.NotImplemented)
      }
    }
  }

}
