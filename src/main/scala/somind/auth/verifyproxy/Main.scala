package somind.auth.verifyproxy

import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import ch.megard.akka.http.cors.scaladsl.CorsDirectives._
import com.typesafe.scalalogging.LazyLogging
import somind.auth.verifyproxy.Conf._
import somind.auth.verifyproxy.models.JsonSupport
import somind.auth.verifyproxy.observe.ObserverRoute
import somind.auth.verifyproxy.routes.VerifyRoute

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
    Http().bindAndHandle(route, "0.0.0.0", port)
  }
}
