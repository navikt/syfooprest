package no.nav.syfo.testhelper

import no.nav.security.oidc.context.*
import no.nav.security.oidc.test.support.JwtTokenGenerator
import no.nav.syfo.api.auth.OIDCIssuer

object OidcTestHelper {
    @JvmStatic
    fun loggInnBruker(oidcRequestContextHolder: OIDCRequestContextHolder, subject: String?) {
        val jwt = JwtTokenGenerator.createSignedJWT(subject)
        val issuer = OIDCIssuer.EKSTERN
        val tokenContext = TokenContext(issuer, jwt.serialize())
        val oidcClaims = OIDCClaims(jwt)
        val oidcValidationContext = OIDCValidationContext()
        oidcValidationContext.addValidatedToken(issuer, tokenContext, oidcClaims)
        oidcRequestContextHolder.oidcValidationContext = oidcValidationContext
    }

    fun loggUtAlle(oidcRequestContextHolder: OIDCRequestContextHolder) {
        oidcRequestContextHolder.oidcValidationContext = null
    }
}
