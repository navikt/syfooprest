package no.nav.syfo.api.auth

import no.nav.security.token.support.core.context.TokenValidationContext
import no.nav.security.token.support.core.context.TokenValidationContextHolder

object OIDCUtil {

    fun getSubjectEkstern(contextHolder: TokenValidationContextHolder): String {
        val issuer = OIDCIssuer.EKSTERN
        val context: TokenValidationContext = contextHolder.tokenValidationContext
        return context.getClaims(issuer)?.subject ?: throw RuntimeException("Fant ikke 'subject'-claim i token fra issuer: $issuer")
    }

    fun getIssuerToken(contextHolder: TokenValidationContextHolder, issuer: String): String {
        val context = contextHolder.tokenValidationContext
        return context.getJwtToken(issuer)?.tokenAsString ?: throw RuntimeException("Klarte ikke hente token fra issuer: $issuer")
    }
}
