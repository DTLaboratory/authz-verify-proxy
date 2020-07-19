package somind.auth.verifyproxy.routes

import java.security.interfaces.RSAPublicKey

import akka.http.scaladsl.model._
import akka.http.scaladsl.server._
import com.auth0.jwk.UrlJwkProvider
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces._
import com.typesafe.scalalogging.LazyLogging
import somind.auth.verifyproxy.Conf._
import somind.auth.verifyproxy.models._
import somind.auth.verifyproxy.{HttpSupport, observe}

object VerifyRoute
    extends JsonSupport
    with LazyLogging
    with Directives
    with HttpSupport {

  val provider = new UrlJwkProvider(jwksHost)

  // todo: this is expensive - need to reuse instances
  def decode(tokenString: String): Option[DecodedJWT] = {
    val token = tokenString.split(' ')(1)
    val jwt = JWT.decode(token)

    val jwk = provider.get(jwt.getKeyId)
    jwk.getAlgorithm match {
      case "RS256" =>
        val algorithm =
          Algorithm.RSA256(jwk.getPublicKey.asInstanceOf[RSAPublicKey], null)
        val verifier = JWT.require(algorithm).build()
        verifier.verify(token) match {
          case null =>
            observe.Observer("jwt_verify_failed")
            logger.warn(
              s"jwt not valid. audience: ${jwt.getAudience} subject: ${jwt.getSubject}")
            None
          case dj =>
            observe.Observer("jwt_verify_succeeded")
            Some(dj)
        }
      case a =>
        observe.Observer("jwt_verify_alg_not_supported")
        logger.warn(s"algorythm in jwt not supported: $a")
        None
    }

  }

  def enforcePermissions(decodedJWT: DecodedJWT,
                         segs: List[String],
                         method: HttpMethod): Option[List[String]] = {
    val claim = decodedJWT.getClaim("permissions")
    val permissions = claim.asArray(classOf[String]).toList
    method match {
      case HttpMethods.GET =>
        if (permissions.contains(s"read:${segs.head}"))
          Some(permissions)
        else {
          logger.warn(s"denied read access: $method $segs $permissions")
          None
        }
      case _ =>
        if (permissions.contains(s"write:${segs.head}"))
          Some(permissions)
        else {
          logger.warn(s"denied write access: $method $segs $permissions")
          None
        }
    }
  }

  def verifyJwt(segs: List[String],
                token: String,
                method: HttpMethod): Directive0 = {

    decode(token) match {
      case Some(dj) =>
        enforcePermissions(dj, segs, method) match {
          case Some(_) =>
            Directive.Empty
          case _ =>
            complete(StatusCodes.Unauthorized)
        }
      case _ =>
        complete(StatusCodes.Unauthorized)
    }

  }

  def apply: Route = {
    headerValueByName("Authorization") { token =>
      ignoreTrailingSlash {
        path(Segments) { segs =>
          extractRequest { r =>
            verifyJwt(segs, token, r.method) {
              // todo: forward to destination service
              // todo: forward to destination service
              // todo: forward to destination service
              complete(
                StatusCodes.OK,
                "{\n  \"msg\": \"Your access token was successfully validated!\"\n}")
            }
          }
        }
      }
    }
  }

}
