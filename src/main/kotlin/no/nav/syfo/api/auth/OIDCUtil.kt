package no.nav.syfo.api.auth

import com.nimbusds.jwt.JWTClaimsSet
import no.nav.security.oidc.OIDCConstants
import no.nav.security.oidc.context.*
import java.util.*

object OIDCUtil {
    private fun context(contextHolder: OIDCRequestContextHolder): OIDCValidationContext {
        return Optional.ofNullable(contextHolder.oidcValidationContext)
            .orElse(null)
    }

    private fun claims(contextHolder: OIDCRequestContextHolder, issuer: String): OIDCClaims {
        return Optional.ofNullable(context(contextHolder))
            .map { s: OIDCValidationContext -> s.getClaims(issuer) }
            .orElse(null)
    }

    private fun claimSet(contextHolder: OIDCRequestContextHolder, issuer: String): JWTClaimsSet {
        return Optional.ofNullable(claims(contextHolder, issuer))
            .map { obj: OIDCClaims -> obj.claimSet }
            .orElse(null)
    }

    @JvmStatic
    fun getSubjectEkstern(contextHolder: OIDCRequestContextHolder): String {
        return Optional.ofNullable(claimSet(contextHolder, OIDCIssuer.EKSTERN))
            .map { obj: JWTClaimsSet -> obj.subject }
            .orElse(null)
    }

    fun getIssuerToken(contextHolder: OIDCRequestContextHolder, issuer: String?): String {
        val context = contextHolder
            .getRequestAttribute(OIDCConstants.OIDC_VALIDATION_CONTEXT) as OIDCValidationContext
        val tokenContext = context.getToken(issuer)
        return tokenContext.idToken
    }
}
