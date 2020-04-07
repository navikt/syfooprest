package no.nav.syfo.rest.ressurser;

import no.nav.security.oidc.api.ProtectedWithClaims;
import no.nav.security.oidc.context.OIDCRequestContextHolder;
import no.nav.syfo.metric.Metric;
import no.nav.syfo.narmesteleder.Naermesteleder;
import no.nav.syfo.narmesteleder.NarmesteLederConsumer;
import no.nav.syfo.rest.domain.RSNaermesteLeder;
import no.nav.syfo.services.*;
import no.nav.syfo.tilgang.TilgangskontrollService;
import org.slf4j.Logger;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotFoundException;
import java.util.Optional;

import static no.nav.syfo.narmesteleder.NarmestelederMappers.narmesteLeder2Rs;
import static no.nav.syfo.oidc.OIDCIssuer.EKSTERN;
import static no.nav.syfo.utils.MapUtil.map;
import static no.nav.syfo.utils.OIDCUtil.getSubjectEkstern;
import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

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

    @Inject
    public NaermestelederRessurs(
            Metric metric,
            OIDCRequestContextHolder contextHolder,
            TilgangskontrollService tilgangskontrollService,
            AktoerService aktoerService,
            NarmesteLederConsumer narmesteLederConsumer
    ) {
        this.metric = metric;
        this.contextHolder = contextHolder;
        this.tilgangskontrollService = tilgangskontrollService;
        this.aktoerService = aktoerService;
        this.narmesteLederConsumer = narmesteLederConsumer;
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

        Optional<Naermesteleder> naermesteleder = narmesteLederConsumer.narmesteLeder(fnr, virksomhetsnummer);
        if (!naermesteleder.isPresent() || !naermesteleder.get().naermesteLederStatus.erAktiv) {
            throw new NotFoundException();
        }

        return mapNaermesteLeder(naermesteleder.get());
    }

    private RSNaermesteLeder mapNaermesteLeder(Naermesteleder naermesteleder) {
        RSNaermesteLeder rsNaermesteLeder = map(naermesteleder, narmesteLeder2Rs);
        return rsNaermesteLeder
                .fnr(aktoerService.hentFnrForAktoer(naermesteleder.naermesteLederAktoerId));
    }
}
