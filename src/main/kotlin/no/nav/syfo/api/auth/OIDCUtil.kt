package no.nav.syfo.api.auth

import no.nav.security.token.support.core.context.TokenValidationContext
import no.nav.security.token.support.core.context.TokenValidationContextHolder

object OIDCUtil {

    @JvmStatic
    fun getSubjectEkstern(contextHolder: TokenValidationContextHolder): String {
        val context: TokenValidationContext = contextHolder.tokenValidationContext
        return context.getClaims(OIDCIssuer.EKSTERN)?.subject ?: throw RuntimeException("Fant ikke subject for OIDCIssuer Ekstern")
    }

    fun getIssuerToken(contextHolder: TokenValidationContextHolder, issuer: String): String {
        val context = contextHolder.tokenValidationContext
        return context.getJwtToken(issuer)?.tokenAsString ?: throw RuntimeException("Klarte ikke hente token for issuer: $issuer")
    }
}
