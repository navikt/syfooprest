package no.nav.syfo.rest.ressurser;

import io.swagger.annotations.Api;
import no.nav.metrics.aspects.Count;
import no.nav.syfo.rest.domain.RSNaermesteLeder;
import no.nav.syfo.services.*;
import org.slf4j.Logger;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import static java.lang.System.getProperty;
import static java.time.LocalDate.now;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.ok;
import static javax.ws.rs.core.Response.status;
import static no.nav.common.auth.SubjectHandler.getIdent;
import static org.slf4j.LoggerFactory.getLogger;

@Controller
@Path("/naermesteleder/{fnr}")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Api(value = "naermesteleder", description = "Endepunkt for å hente naermesteleder for en gitt aktoerId")
public class NaermestelederRessurs {
    private static final Logger LOG = getLogger(NaermestelederRessurs.class);

    @Inject
    private TilgangskontrollService tilgangskontrollService;
    @Inject
    private AktoerService aktoerService;
    @Inject
    private NaermesteLederService naermesteLederService;

    @GET
    @Count(name = "hentNaermesteLeder")
    public RSNaermesteLeder hentNaermesteLeder(@PathParam("fnr") String fnr, @QueryParam("virksomhetsnummer") String virksomhetsnummer) {
        if ("true".equals(getProperty("local.mock"))) {
            return new RSNaermesteLeder().fnr("99009900999").aktivFom(now().minusMonths(6)).navn("Are Arbeidsgiver").tlf("22225555").epost("are@arbeidsgiver.no").erAktiv(true).virksomhetsnummer(virksomhetsnummer);
        }
        if (tilgangskontrollService.sporOmNoenAndreEnnSegSelvEllerEgneAnsatte(aktoerService.hentAktoerIdForFnr(fnr))) {
            LOG.error("{} spurte om fnr {}. Dette får man ikke lov til fordi det er hverken seg selv eller en av sine ansatte.", getIdent(), fnr);
            throw new ForbiddenException();
        }

        RSNaermesteLeder naermesteLeder = naermesteLederService.hentNaermesteLeder(aktoerService.hentAktoerIdForFnr(fnr), virksomhetsnummer);

        if (!naermesteLeder.erAktiv) {
            throw new NotFoundException();
        }

        return naermesteLeder;
    }

    @GET
    @Path("/forrige")
    @Count(name = "hentForrigeNaermesteLeder")
    public Response hentForrigeNaermesteLeder(@PathParam("fnr") String fnr, @QueryParam("virksomhetsnummer") String virksomhetsnummer) {
        if ("true".equals(getProperty("local.mock"))) {
            return status(404).build();
        }
        if (tilgangskontrollService.sporOmNoenAndreEnnSegSelvEllerEgneAnsatte(aktoerService.hentAktoerIdForFnr(fnr))) {
            LOG.error("{} spurte om fnr {}. Dette får man ikke lov til fordi det er hverken seg selv eller en av sine ansatte.", getIdent(), fnr);
            throw new ForbiddenException();
        }

        return naermesteLederService.hentForrigeNaermesteLeder(aktoerService.hentAktoerIdForFnr(fnr), virksomhetsnummer)
                .map(forrigeLeder -> ok(forrigeLeder).build())
                .orElse(status(404).build());

    }


}
