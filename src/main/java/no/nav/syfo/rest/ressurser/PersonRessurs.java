package no.nav.syfo.rest.ressurser;

import io.swagger.annotations.Api;
import no.nav.metrics.aspects.Count;
import no.nav.syfo.rest.domain.RSPerson;
import no.nav.syfo.services.AktoerService;
import no.nav.syfo.services.BrukerprofilService;
import no.nav.syfo.services.TilgangskontrollService;
import org.slf4j.Logger;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;
import javax.ws.rs.*;

import static java.lang.System.getProperty;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static no.nav.common.auth.SubjectHandler.getIdent;
import static org.slf4j.LoggerFactory.getLogger;

@Controller
@Path("/person/{fnr}")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Api(value = "person", description = "Endepunkt for å hente navn for en gitt aktoerId")
public class PersonRessurs {
    private static final Logger LOG = getLogger(PersonRessurs.class);

    @Inject
    private BrukerprofilService brukerprofilService;
    @Inject
    private TilgangskontrollService tilgangskontrollService;
    @Inject
    private AktoerService aktoerService;

    @GET
    @Count(name = "hentPerson")
    public RSPerson hentNavn(@PathParam("fnr") String fnr) {
        if ("true".equals(getProperty("local.mock"))) {
            return new RSPerson().fnr(fnr).navn("Sygve Sykmeldt");
        }
        if (tilgangskontrollService.sporOmNoenAndreEnnSegSelvEllerEgneAnsatteEllerLedere(aktoerService.hentAktoerIdForFnr(fnr))) {
            LOG.warn("{} spurte om fnr {}. Dette får man ikke lov til fordi det er hverken seg selv, en av sine ansatte eller nærmeste ledere", getIdent(), fnr);
            throw new ForbiddenException();
        }
        return new RSPerson()
                .navn(brukerprofilService.hentNavnByFnr(fnr))
                .fnr(fnr);
    }

}
