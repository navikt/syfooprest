package no.nav.syfo.rest.ressurser;

import io.swagger.annotations.Api;
import no.nav.metrics.aspects.Count;
import no.nav.syfo.rest.domain.RSVirksomhet;
import no.nav.syfo.services.OrganisasjonService;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;
import javax.ws.rs.*;

import static java.lang.System.getProperty;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Controller
@Path("/virksomhet/{virksomhetsnummer}")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Api(value = "virksomhet", description = "Endepunkt for å sjekke om en gitt aktoer har tilgang til Oppfølgingsdialog-tjenesten")
public class VirksomhetsRessurs {

    @Inject
    private OrganisasjonService organisasjonService;

    @GET
    @Count(name = "hentVirksomhet")
    public RSVirksomhet hentVirksomhet(@PathParam("virksomhetsnummer") String virksomhetsnummer) {
        if ("true".equals(getProperty("local.mock"))) {
            return new RSVirksomhet().virksomhetsnummer(virksomhetsnummer).navn("NAV AS");
        }
        return new RSVirksomhet()
                .virksomhetsnummer(virksomhetsnummer)
                .navn(organisasjonService.hentNavn(virksomhetsnummer));
    }

}
