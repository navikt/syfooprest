package no.nav.syfo.rest.ressurser;

import io.swagger.annotations.Api;
import no.nav.metrics.aspects.Count;
import no.nav.syfo.rest.domain.RSStilling;
import no.nav.syfo.services.AktoerService;
import no.nav.syfo.services.ArbeidsforholdService;
import no.nav.syfo.services.TilgangskontrollService;
import org.slf4j.Logger;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;
import javax.ws.rs.*;
import java.util.List;

import static java.lang.System.getProperty;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static no.nav.common.auth.SubjectHandler.getIdent;
import static no.nav.syfo.rest.ressurser.mocks.ArbeidsforholdMock.arbeidsforhold;
import static org.slf4j.LoggerFactory.getLogger;

@Controller
@Path("/arbeidsforhold")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Api(value = "arbeidsforhold", description = "Endepunkt for henting av arbeidsforhold for sykmeldte")
public class ArbeidsforholdRessurs {
    private static final Logger LOG = getLogger(ArbeidsforholdRessurs.class);

    @Inject
    private ArbeidsforholdService arbeidsforholdService;
    @Inject
    private TilgangskontrollService tilgangskontrollService;
    @Inject
    private AktoerService aktoerService;

    @GET
    @Count(name = "hentArbeidsforhold")
    public List<RSStilling> hentArbeidsforhold(@QueryParam("fnr") String oppslaattFnr, @QueryParam("virksomhetsnummer") String virksomhetsnummer, @QueryParam("fom") String fom) {
        if ("true".equals(getProperty("local.mock"))) {
            return arbeidsforhold();
        }

        if (tilgangskontrollService.sporOmNoenAndreEnnSegSelvEllerEgneAnsatte(aktoerService.hentAktoerIdForFnr(oppslaattFnr))) {
            LOG.error("{} spurte om fnr {}. Dette får man ikke lov til fordi det er hverken seg selv eller en av sine ansatte.", getIdent(), oppslaattFnr);
            throw new ForbiddenException();
        }

        return arbeidsforholdService.hentBrukersArbeidsforholdHosVirksomhet(oppslaattFnr, virksomhetsnummer, fom);
    }
}
