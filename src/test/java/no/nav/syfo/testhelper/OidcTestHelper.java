package no.nav.syfo.testhelper;

import com.nimbusds.jwt.SignedJWT;
import no.nav.security.oidc.context.*;
import no.nav.security.oidc.test.support.JwtTokenGenerator;
import no.nav.syfo.api.auth.OIDCIssuer;

public class OidcTestHelper {

    public static OIDCValidationContext lagOIDCValidationContextEkstern(String subject) {
        return lagOIDCValidationContext(subject, OIDCIssuer.EKSTERN);
    }

    private static OIDCValidationContext lagOIDCValidationContext(String subject, String issuer) {
        //OIDC-hack - legg til token og oidcclaims for en test-person
        SignedJWT jwt = JwtTokenGenerator.createSignedJWT(subject);
        TokenContext tokenContext = new TokenContext(issuer, jwt.serialize());
        OIDCClaims oidcClaims = new OIDCClaims(jwt);
        OIDCValidationContext oidcValidationContext = new OIDCValidationContext();
        oidcValidationContext.addValidatedToken(issuer, tokenContext, oidcClaims);
        return oidcValidationContext;
    }


    public static void loggInnBruker(OIDCRequestContextHolder oidcRequestContextHolder, String subject) {
        //OIDC-hack - legg til token og oidcclaims for en test-person
        SignedJWT jwt = JwtTokenGenerator.createSignedJWT(subject);
        String issuer = OIDCIssuer.EKSTERN;
        TokenContext tokenContext = new TokenContext(issuer, jwt.serialize());
        OIDCClaims oidcClaims = new OIDCClaims(jwt);
        OIDCValidationContext oidcValidationContext = new OIDCValidationContext();
        oidcValidationContext.addValidatedToken(issuer, tokenContext, oidcClaims);
        oidcRequestContextHolder.setOIDCValidationContext(oidcValidationContext);
    }

    public static void loggUtAlle(OIDCRequestContextHolder oidcRequestContextHolder) {
        oidcRequestContextHolder.setOIDCValidationContext(null);
    }
}
