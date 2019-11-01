package no.nav.syfo.rest.ressurser;

import no.nav.security.spring.oidc.validation.api.ProtectedWithClaims;
import no.nav.syfo.metric.Metrikk;
import no.nav.syfo.rest.domain.RSVirksomhet;
import no.nav.syfo.services.OrganisasjonService;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;

import static no.nav.syfo.oidc.OIDCIssuer.EKSTERN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@ProtectedWithClaims(issuer = EKSTERN)
@RequestMapping(value = "/api/virksomhet/{virksomhetsnummer}")
public class VirksomhetsRessurs {

    private final Metrikk metrikk;
    private final OrganisasjonService organisasjonService;

    @Inject
    public VirksomhetsRessurs(
            Metrikk metrikk,
            OrganisasjonService organisasjonService
    ) {
        this.metrikk = metrikk;
        this.organisasjonService = organisasjonService;
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public RSVirksomhet hentVirksomhet(@PathVariable("virksomhetsnummer") String virksomhetsnummer) {
        metrikk.tellEndepunktKall("hentVirksomhet");

        return new RSVirksomhet()
                .virksomhetsnummer(virksomhetsnummer)
                .navn(organisasjonService.hentNavn(virksomhetsnummer));
    }
}
