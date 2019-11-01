package no.nav.syfo.rest.ressurser;

import no.nav.security.oidc.context.OIDCRequestContextHolder;
import no.nav.security.spring.oidc.validation.api.ProtectedWithClaims;
import no.nav.syfo.metric.Metrikk;
import no.nav.syfo.rest.domain.RSKontaktinfo;
import no.nav.syfo.services.*;
import org.slf4j.Logger;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.ws.rs.ForbiddenException;

import static no.nav.syfo.oidc.OIDCIssuer.EKSTERN;
import static no.nav.syfo.utils.OIDCUtil.getSubjectEkstern;
import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@ProtectedWithClaims(issuer = EKSTERN)
@RequestMapping(value = "/api/kontaktinfo/{fnr}")
public class KontaktinfoRessurs {

    private static final Logger log = getLogger(KontaktinfoRessurs.class);

    private final Metrikk metrikk;
    private final OIDCRequestContextHolder contextHolder;
    private final DkifService dkifService;
    private final AktoerService aktoerService;
    private final TilgangskontrollService tilgangskontrollService;

    @Inject
    public KontaktinfoRessurs(
            Metrikk metrikk,
            OIDCRequestContextHolder contextHolder,
            DkifService dkifService,
            AktoerService aktoerService,
            TilgangskontrollService tilgangskontrollService
    ) {
        this.metrikk = metrikk;
        this.contextHolder = contextHolder;
        this.dkifService = dkifService;
        this.aktoerService = aktoerService;
        this.tilgangskontrollService = tilgangskontrollService;
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public RSKontaktinfo hentKontaktinfo(@PathVariable("fnr") final String oppslaattFnr) {
        metrikk.tellEndepunktKall("hentKontaktinfo");

        String innloggetFnr = getSubjectEkstern(contextHolder);

        String oppslaattAktoerId = aktoerService.hentAktoerIdForFnr(oppslaattFnr);
        if (tilgangskontrollService.sporOmNoenAndreEnnSegSelvEllerEgneAnsatte(innloggetFnr, oppslaattAktoerId)) {
            log.error("Innlogget person spurtee om fnr man ikke lov til fordi det er hverken seg selv eller en av sine ansatte.");
            throw new ForbiddenException();
        }
        return dkifService.hentRSKontaktinfoAktoerId(oppslaattAktoerId);
    }
}
