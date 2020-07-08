package no.nav.syfo.rest.ressurser;

import no.nav.security.oidc.api.ProtectedWithClaims;
import no.nav.syfo.metric.Metric;
import no.nav.syfo.rest.domain.RSVirksomhet;
import no.nav.syfo.service.OrganisasjonService;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;

import static no.nav.syfo.oidc.OIDCIssuer.EKSTERN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@ProtectedWithClaims(issuer = EKSTERN)
@RequestMapping(value = "/api/virksomhet/{virksomhetsnummer}")
public class VirksomhetsRessurs {

    private final Metric metric;
    private final OrganisasjonService organisasjonService;

    @Inject
    public VirksomhetsRessurs(
            Metric metric,
            OrganisasjonService organisasjonService
    ) {
        this.metric = metric;
        this.organisasjonService = organisasjonService;
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public RSVirksomhet hentVirksomhet(@PathVariable("virksomhetsnummer") String virksomhetsnummer) {
        metric.countEndpointRequest("hentVirksomhet");

        return new RSVirksomhet()
                .virksomhetsnummer(virksomhetsnummer)
                .navn(organisasjonService.hentNavn(virksomhetsnummer));
    }
}
