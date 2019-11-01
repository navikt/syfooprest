package no.nav.syfo.rest.ressurser;

import no.nav.security.oidc.context.OIDCRequestContextHolder;
import no.nav.security.spring.oidc.validation.api.ProtectedWithClaims;
import no.nav.syfo.metric.Metrikk;
import no.nav.syfo.rest.domain.RSNaermesteLeder;
import no.nav.syfo.services.*;
import org.slf4j.Logger;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.Response.ok;
import static javax.ws.rs.core.Response.status;
import static no.nav.syfo.oidc.OIDCIssuer.EKSTERN;
import static no.nav.syfo.utils.OIDCUtil.getSubjectEkstern;
import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@ProtectedWithClaims(issuer = EKSTERN)
@RequestMapping(value = "/api/naermesteleder/{fnr}")
public class NaermestelederRessurs {

    private static final Logger log = getLogger(NaermestelederRessurs.class);

    private final Metrikk metrikk;
    private final OIDCRequestContextHolder contextHolder;
    private final TilgangskontrollService tilgangskontrollService;
    private final AktoerService aktoerService;
    private final NaermesteLederService naermesteLederService;

    @Inject
    public NaermestelederRessurs(
            Metrikk metrikk,
            OIDCRequestContextHolder contextHolder,
            TilgangskontrollService tilgangskontrollService,
            AktoerService aktoerService,
            NaermesteLederService naermesteLederService
    ) {
        this.metrikk = metrikk;
        this.contextHolder = contextHolder;
        this.tilgangskontrollService = tilgangskontrollService;
        this.aktoerService = aktoerService;
        this.naermesteLederService = naermesteLederService;
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public RSNaermesteLeder hentNaermesteLeder(
            @PathVariable("fnr") String fnr,
            @RequestParam("virksomhetsnummer") String virksomhetsnummer
    ) {
        metrikk.tellEndepunktKall("hentNaermesteLeder");

        String innloggetFnr = getSubjectEkstern(contextHolder);

        if (tilgangskontrollService.sporOmNoenAndreEnnSegSelvEllerEgneAnsatte(innloggetFnr, aktoerService.hentAktoerIdForFnr(fnr))) {
            log.error("Innlogget person spurtee om fnr man ikke lov til fordi det er hverken seg selv eller en av sine ansatte.");
            throw new ForbiddenException();
        }

        RSNaermesteLeder naermesteLeder = naermesteLederService.hentNaermesteLeder(aktoerService.hentAktoerIdForFnr(fnr), virksomhetsnummer);

        if (!naermesteLeder.erAktiv) {
            throw new NotFoundException();
        }

        return naermesteLeder;
    }

    @GetMapping(path = "/forrige", produces = APPLICATION_JSON_VALUE)
    public Response hentForrigeNaermesteLeder(
            @PathVariable("fnr") String fnr,
            @RequestParam("virksomhetsnummer") String virksomhetsnummer
    ) {
        metrikk.tellEndepunktKall("hentForrigeNaermesteLeder");

        String innloggetFnr = getSubjectEkstern(contextHolder);

        if (tilgangskontrollService.sporOmNoenAndreEnnSegSelvEllerEgneAnsatte(innloggetFnr, aktoerService.hentAktoerIdForFnr(fnr))) {
            log.error("Innlogget person spurtee om fnr man ikke lov til fordi det er hverken seg selv eller en av sine ansatte.");
            throw new ForbiddenException();
        }

        return naermesteLederService.hentForrigeNaermesteLeder(aktoerService.hentAktoerIdForFnr(fnr), virksomhetsnummer)
                .map(forrigeLeder -> ok(forrigeLeder).build())
                .orElse(status(404).build());

    }
}
