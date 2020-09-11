package somind.authz.verifyproxy

import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import ch.megard.akka.http.cors.scaladsl.CorsDirectives._
import com.typesafe.scalalogging.LazyLogging
import Conf._
import somind.authz.verifyproxy.models.JsonSupport
import somind.authz.verifyproxy.observe.ObserverRoute
import somind.authz.verifyproxy.routes.VerifyRoute

object Main extends LazyLogging with JsonSupport with HttpSupport {
  def main(args: Array[String]) {
    val route =
      ObserverRoute.apply ~
        handleErrors {
          cors(corsSettings) {
            handleErrors {
              logRequest(urlpath) {
                pathPrefix(urlpath) {
                  VerifyRoute.apply
                }
              }
            }
          }
        }
    Http().newServerAt("0.0.0.0", port).bindFlow(route)
  }
}
