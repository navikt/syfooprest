package no.nav.syfo.testhelper

import no.nav.security.token.support.core.context.TokenValidationContext
import no.nav.security.token.support.core.jwt.JwtToken
import no.nav.security.token.support.test.JwtTokenGenerator
import no.nav.syfo.api.auth.OIDCIssuer.EKSTERN

object OidcTestHelper {

    @JvmStatic
    fun getValidationContext(subject: String): TokenValidationContext {
        val jwt = JwtToken(JwtTokenGenerator.createSignedJWT(subject).serialize())
        val issuer = EKSTERN
        val issuerTokenMap = HashMap<String, JwtToken>()
        issuerTokenMap[issuer] = jwt
        return TokenValidationContext(issuerTokenMap)
    }
}
