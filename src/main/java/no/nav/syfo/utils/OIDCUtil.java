package no.nav.syfo.utils;

import com.nimbusds.jwt.JWTClaimsSet;
import no.nav.security.oidc.context.*;
import no.nav.syfo.oidc.OIDCIssuer;
import no.nav.syfo.services.ws.OnBehalfOfOutInterceptor;
import org.apache.cxf.endpoint.Client;

import java.util.Optional;

import static no.nav.security.oidc.OIDCConstants.OIDC_VALIDATION_CONTEXT;

public class OIDCUtil {

    public static void leggTilOnBehalfOfOutInterceptorForOIDC(Client client, String OIDCToken) {
        client.getRequestContext().put(OnBehalfOfOutInterceptor.REQUEST_CONTEXT_ONBEHALFOF_TOKEN_TYPE, OnBehalfOfOutInterceptor.TokenType.OIDC);
        client.getRequestContext().put(OnBehalfOfOutInterceptor.REQUEST_CONTEXT_ONBEHALFOF_TOKEN, OIDCToken);
    }

    private static OIDCValidationContext context(OIDCRequestContextHolder contextHolder) {
        return Optional.ofNullable(contextHolder.getOIDCValidationContext())
                .orElse(null);
    }

    private static OIDCClaims claims(OIDCRequestContextHolder contextHolder, String issuer) {
        return Optional.ofNullable(context(contextHolder))
                .map(s -> s.getClaims(issuer))
                .orElse(null);
    }

    private static JWTClaimsSet claimSet(OIDCRequestContextHolder contextHolder, String issuer) {
        return Optional.ofNullable(claims(contextHolder, issuer))
                .map(OIDCClaims::getClaimSet)
                .orElse(null);
    }

    public static String getSubjectEkstern(OIDCRequestContextHolder contextHolder) {
        return Optional.ofNullable(claimSet(contextHolder, OIDCIssuer.EKSTERN))
                .map(JWTClaimsSet::getSubject)
                .orElse(null);
    }

    public static String getIssuerToken(OIDCRequestContextHolder contextHolder, String issuer) {
        OIDCValidationContext context = (OIDCValidationContext) contextHolder
                .getRequestAttribute(OIDC_VALIDATION_CONTEXT);
        TokenContext tokenContext = context.getToken(issuer);
        return tokenContext.getIdToken();
    }
}
