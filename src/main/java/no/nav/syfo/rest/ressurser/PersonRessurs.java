package no.nav.syfo.rest.ressurser;

import no.nav.security.oidc.context.OIDCRequestContextHolder;
import no.nav.security.spring.oidc.validation.api.ProtectedWithClaims;
import no.nav.syfo.metric.Metric;
import no.nav.syfo.rest.domain.RSPerson;
import no.nav.syfo.services.BrukerprofilService;
import no.nav.syfo.services.TilgangskontrollService;
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
@RequestMapping(value = "/api/person/{fnr}")
public class PersonRessurs {
    private static final Logger log = getLogger(PersonRessurs.class);

    private final Metric metric;
    private final OIDCRequestContextHolder contextHolder;
    private final BrukerprofilService brukerprofilService;
    private final TilgangskontrollService tilgangskontrollService;

    @Inject
    public PersonRessurs(
            Metric metric,
            OIDCRequestContextHolder contextHolder,
            BrukerprofilService brukerprofilService,
            TilgangskontrollService tilgangskontrollService
    ) {
        this.metric = metric;
        this.contextHolder = contextHolder;
        this.brukerprofilService = brukerprofilService;
        this.tilgangskontrollService = tilgangskontrollService;
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public RSPerson hentNavn(@PathVariable("fnr") String fnr) {
        metric.countEndpointRequest("hentPerson");

        String innloggetFnr = getSubjectEkstern(contextHolder);

        if (tilgangskontrollService.sporOmNoenAndreEnnSegSelvEllerEgneAnsatteEllerLedere(innloggetFnr, fnr)) {
            log.error("Fikk ikke hentet navn: Innlogget person spurte om fnr man ikke lov til fordi det er hverken seg selv eller en av sine ansatte.");
            throw new ForbiddenException();
        }
        return new RSPerson()
                .navn(brukerprofilService.hentNavnByFnr(fnr))
                .fnr(fnr);
    }

    @GetMapping(path = "/{virksomhetsnummer}",produces = APPLICATION_JSON_VALUE)
    public RSPerson hentNavnMedVirksomhet(
            @PathVariable("fnr") String fnr,
            @PathVariable("virksomhetsnummer") String virksomhetsnummer
    ) {
        metric.countEndpointRequest("hentNavnMedVirksomhet");

        String innloggetFnr = getSubjectEkstern(contextHolder);

        if (tilgangskontrollService.sporOmNoenAndreEnnSegSelvEllerEgneAnsatteEllerLedere(innloggetFnr, fnr, virksomhetsnummer)) {
            log.error("Fikk ikke hentet navn med virksomhet: Innlogget person har ikke tilgang fnr fordi det er hverken seg selv, en av sine ansatte eller sin naermeste leder");
            throw new ForbiddenException();
        }
        return new RSPerson()
                .navn(brukerprofilService.hentNavnByFnr(fnr))
                .fnr(fnr);
    }
}
