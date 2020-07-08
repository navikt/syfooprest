package no.nav.syfo.rest.ressurser;

import no.nav.security.oidc.api.ProtectedWithClaims;
import no.nav.security.oidc.context.OIDCRequestContextHolder;
import no.nav.syfo.metric.Metric;
import no.nav.syfo.rest.domain.RSKontaktinfo;
import no.nav.syfo.service.AktoerService;
import no.nav.syfo.services.*;
import no.nav.syfo.tilgang.TilgangskontrollService;
import org.slf4j.Logger;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.ws.rs.ForbiddenException;

import static no.nav.syfo.oidc.OIDCIssuer.EKSTERN;
import static no.nav.syfo.oidc.OIDCUtil.getSubjectEkstern;
import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@ProtectedWithClaims(issuer = EKSTERN)
@RequestMapping(value = "/api/kontaktinfo/{fnr}")
public class KontaktinfoRessurs {

    private static final Logger log = getLogger(KontaktinfoRessurs.class);

    private final Metric metric;
    private final OIDCRequestContextHolder contextHolder;
    private final DkifService dkifService;
    private final AktoerService aktoerService;
    private final TilgangskontrollService tilgangskontrollService;

    @Inject
    public KontaktinfoRessurs(
            Metric metric,
            OIDCRequestContextHolder contextHolder,
            DkifService dkifService,
            AktoerService aktoerService,
            TilgangskontrollService tilgangskontrollService
    ) {
        this.metric = metric;
        this.contextHolder = contextHolder;
        this.dkifService = dkifService;
        this.aktoerService = aktoerService;
        this.tilgangskontrollService = tilgangskontrollService;
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public RSKontaktinfo hentKontaktinfo(@PathVariable("fnr") final String oppslaattFnr) {
        metric.countEndpointRequest("hentKontaktinfo");

        String innloggetFnr = getSubjectEkstern(contextHolder);

        if (tilgangskontrollService.sporOmNoenAndreEnnSegSelvEllerEgneAnsatte(innloggetFnr, oppslaattFnr)) {
            log.error("Fikk ikke hentet kontaktinfo: Innlogget person spurte om fnr man ikke lov til fordi det er hverken seg selv eller en av sine ansatte.");
            throw new ForbiddenException();
        }
        String oppslaattAktoerId = aktoerService.hentAktoerIdForFnr(oppslaattFnr);
        return dkifService.hentRSKontaktinfoAktoerId(oppslaattAktoerId);
    }
}
