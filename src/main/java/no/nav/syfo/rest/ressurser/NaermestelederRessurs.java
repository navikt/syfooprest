package no.nav.syfo.rest.ressurser;

import no.nav.security.oidc.context.OIDCRequestContextHolder;
import no.nav.security.spring.oidc.validation.api.ProtectedWithClaims;
import no.nav.syfo.metric.Metric;
import no.nav.syfo.narmesteleder.Naermesteleder;
import no.nav.syfo.narmesteleder.NarmesteLederConsumer;
import no.nav.syfo.rest.domain.RSNaermesteLeder;
import no.nav.syfo.services.*;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotFoundException;

import static no.nav.syfo.narmesteleder.NarmestelederMappers.narmesteLeder2Rs;
import static no.nav.syfo.oidc.OIDCIssuer.EKSTERN;
import static no.nav.syfo.utils.MapUtil.map;
import static no.nav.syfo.utils.OIDCUtil.getSubjectEkstern;
import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.http.ResponseEntity.status;

@RestController
@ProtectedWithClaims(issuer = EKSTERN)
@RequestMapping(value = "/api/naermesteleder/{fnr}")
public class NaermestelederRessurs {

    private static final Logger log = getLogger(NaermestelederRessurs.class);

    private final Metric metric;
    private final OIDCRequestContextHolder contextHolder;
    private final TilgangskontrollService tilgangskontrollService;
    private final AktoerService aktoerService;
    private final NarmesteLederConsumer narmesteLederConsumer;
    private final NaermesteLederService naermesteLederService;

    @Inject
    public NaermestelederRessurs(
            Metric metric,
            OIDCRequestContextHolder contextHolder,
            TilgangskontrollService tilgangskontrollService,
            AktoerService aktoerService,
            NarmesteLederConsumer narmesteLederConsumer,
            NaermesteLederService naermesteLederService
    ) {
        this.metric = metric;
        this.contextHolder = contextHolder;
        this.tilgangskontrollService = tilgangskontrollService;
        this.aktoerService = aktoerService;
        this.narmesteLederConsumer = narmesteLederConsumer;
        this.naermesteLederService = naermesteLederService;
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public RSNaermesteLeder hentNaermesteLeder(
            @PathVariable("fnr") String fnr,
            @RequestParam("virksomhetsnummer") String virksomhetsnummer
    ) {
        metric.countEndpointRequest("hentNaermesteLeder");

        String innloggetFnr = getSubjectEkstern(contextHolder);

        if (tilgangskontrollService.sporOmNoenAndreEnnSegSelvEllerEgneAnsatte(innloggetFnr, fnr)) {
            log.error("Fikk ikke hentet narmeste leder: Innlogget person spurte om fnr man ikke lov til fordi det er hverken seg selv eller en av sine ansatte.");
            throw new ForbiddenException();
        }

        RSNaermesteLeder naermesteLeder = mapNaermesteLeder(narmesteLederConsumer.narmesteLeder(fnr, virksomhetsnummer));

        if (!naermesteLeder.erAktiv) {
            throw new NotFoundException();
        }

        return naermesteLeder;
    }

    private RSNaermesteLeder mapNaermesteLeder(Naermesteleder naermesteleder) {
        return map(naermesteleder, narmesteLeder2Rs)
                .fnr(aktoerService.hentFnrForAktoer(naermesteleder.naermesteLederAktoerId));
    }

    @GetMapping(path = "/forrige", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity hentForrigeNaermesteLeder(
            @PathVariable("fnr") String fnr,
            @RequestParam("virksomhetsnummer") String virksomhetsnummer
    ) {
        metric.countEndpointRequest("hentForrigeNaermesteLeder");

        String innloggetFnr = getSubjectEkstern(contextHolder);

        if (tilgangskontrollService.sporOmNoenAndreEnnSegSelvEllerEgneAnsatte(innloggetFnr, fnr)) {
            log.error("Fikk ikke hentet forrige narmeste leder: Innlogget person spurte om fnr man ikke lov til fordi det er hverken seg selv eller en av sine ansatte.");
            throw new ForbiddenException();
        }

        return naermesteLederService.hentForrigeNaermesteLeder(aktoerService.hentAktoerIdForFnr(fnr), virksomhetsnummer)
                .map(forrigeLeder -> ok()
                        .contentType(APPLICATION_JSON)
                        .body(forrigeLeder)
                )
                .orElse(status(HttpStatus.NOT_FOUND)
                        .contentType(APPLICATION_JSON)
                        .build()
                );
    }
}
