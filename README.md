![Scala CI](https://github.com/SoMind/authz-verify-proxy/workflows/Scala%20CI/badge.svg)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/28b0aa92bec148f49bd77a9a5a8e38e2)](https://app.codacy.com/gh/SoMind/authz-verify-proxy?utm_source=github.com&utm_medium=referral&utm_content=SoMind/authz-verify-proxy&utm_campaign=Badge_Grade_Dashboard)

Authz Verify Proxy
===========

A sidecar container to enforce security policy.

The current implementation is working with Auth0 <https://auth0.com/docs/authorization> tokens in the incoming
request http headers.

The proxy should work with any compliant JWT based token but it is possible there are assumptions made
specific to Auth0.

The proxy is specifically securing the Somind DtLab when it evaluates permission claims.

It retrieves the JWK store of public keys from a configured URL.  In its default deployment, that URL is

```bash
  https://navicore.us.auth0.com/
```

Override this by setting ENV var `JWK_HOST` to your own JWK store host and root path.

When a request destined for a service is intercepted by the proxy, it does the following:

1.  Extract the token from the `Authorization` header, strip `Bearer`.
2.  Parse the token.
3.  Verify the token was signed by us and is not expired.
4.  Extract the permissions from the claims.
5.  Calculate required permissions by looking at the http method and the root DtLab object being operated on.  ie: `read:type`, `write:actor`.  Future implementations will support `read:actor.my_fav_actor_types` where root type works like a multi-tenant namespace.
6.  Verify that the required permission is in the list of permissions extracted from the signed token.

If the above have no issues, then forward the request to the configured `REMOTE_HOST` and `REMOTE_PORT`.
