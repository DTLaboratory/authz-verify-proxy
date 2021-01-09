package dtlaboratory.authz

import java.security.interfaces.RSAPublicKey

import com.auth0.jwk.UrlJwkProvider
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.TokenExpiredException
import com.typesafe.scalalogging.LazyLogging
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import dtlaboratory.authz.verifyproxy.Conf._

class JwtSpec extends AnyFlatSpec with should.Matchers with LazyLogging {

  "A jwt" should "parse" in {

    val provider = new UrlJwkProvider(jwksHost)

    val token =
      "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6Im5SMnhud2c1RDBBVWN2SXUyNjdFdiJ9.eyJpc3MiOiJodHRwczovL2F1dGguc29taW5kLnRlY2gvIiwic3ViIjoiZ2l0aHVifDExMDk5OSIsImF1ZCI6WyJodHRwczovL3NvbWluZC50ZWNoL2R0bGFiL2FkbWluIiwiaHR0cHM6Ly9uYXZpY29yZS51cy5hdXRoMC5jb20vdXNlcmluZm8iXSwiaWF0IjoxNTk1MTI3OTc4LCJleHAiOjE1OTUyMTQzNzgsImF6cCI6IllaNzdVZnQ3aHZ3TzdkejdmMzZXaHlZMDBraG9VRzNhIiwic2NvcGUiOiJvcGVuaWQgcHJvZmlsZSBlbWFpbCIsInBlcm1pc3Npb25zIjpbImxpc3Q6dHlwZXMiLCJyZWFkOnR5cGVzIiwid3JpdGU6dHlwZXMiXX0.lABFGJBXRJsOFvUxxMGj0BSBAFzGD0DK6MMuSqXlnXG9wv1XwzVTaACBWtamE1EGB6xoIuXP7xYpw0qYZTVA2ITOYc0GaHeHnPaaffAiYVzgbEjNIYHrWWti0arijn3CuVtmYSnQT54RRSifnjqSnscZHCgHcsKQYpt2ByfKfjKaTWnJWWkLIq4ppn26e0VylmKGFjqvAL8FDY-o4ynPPhM5QJpsO919kbQ_79_jd5M2rJ5O5Tk2KrUj5bdHvgXkl0JL-wGlsNhrJh3tlxC6AWmL1x4GeE-eima8AM448wjgKmKXy9Oy_ZszUSLcx3Sstxue5OLB9Np2S-86Y5qAgg"
    val jwt = JWT.decode(token)
    jwt should not be None.orNull
    jwt.getAlgorithm should be("RS256")
    jwt.getKeyId should be("nR2xnwg5D0AUcvIu267Ev")

    val jwk = provider.get(jwt.getKeyId)
    jwk.getAlgorithm should be("RS256")
    val algorithm =
      Algorithm.RSA256(jwk.getPublicKey.asInstanceOf[RSAPublicKey], None.orNull)
    val verifier = JWT.require(algorithm).build()

    try {

    verifier.verify(token)
      assert(false, "no exception")
    } catch {
      case _: TokenExpiredException =>
        logger.debug("good, token is expired")
      case e: Exception =>
        assert(false, s"wrong exception: $e")
    }

  }

}
