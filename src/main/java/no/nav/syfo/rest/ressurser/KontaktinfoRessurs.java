package no.nav.syfo.rest.ressurser;

import io.swagger.annotations.Api;
import no.nav.metrics.aspects.Count;
import no.nav.syfo.rest.domain.RSKontaktinfo;
import no.nav.syfo.services.AktoerService;
import no.nav.syfo.services.DkifService;
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
@Path("/kontaktinfo/{fnr}")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Api(value = "kontaktinfo", description = "Endepunkt for å sjekke om en gitt aktoer er reservert i KRR")
public class KontaktinfoRessurs {
    private static final Logger LOG = getLogger(KontaktinfoRessurs.class);

    @Inject
    private DkifService dkifService;
    @Inject
    private AktoerService aktoerService;
    @Inject
    private TilgangskontrollService tilgangskontrollService;

    @GET
    @Count(name = "hentKontaktinfo")
    public RSKontaktinfo hentKontaktinfo(@PathParam("fnr") String oppslaattFnr) {
        if ("true".equals(getProperty("local.mock"))) {
            return new RSKontaktinfo().epost("test@epost.no").tlf("22225555").skalHaVarsel(true);
        }
        String oppslaattAktoerId = aktoerService.hentAktoerIdForFnr(oppslaattFnr);
        if (tilgangskontrollService.sporOmNoenAndreEnnSegSelvEllerEgneAnsatte(oppslaattAktoerId)) {
            LOG.error("{} spurte om fnr {}. Dette får man ikke lov til fordi det er hverken seg selv eller en av sine ansatte.", getIdent(), oppslaattAktoerId);
            throw new ForbiddenException();
        }
        return dkifService.hentRSKontaktinfoAktoerId(oppslaattAktoerId);
    }

}
