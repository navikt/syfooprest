package no.nav.syfo.rest.ressurser;

import no.nav.security.oidc.api.ProtectedWithClaims;
import no.nav.security.oidc.context.OIDCRequestContextHolder;
import no.nav.syfo.metric.Metric;
import no.nav.syfo.rest.domain.RSStilling;
import no.nav.syfo.services.ArbeidsforholdService;
import no.nav.syfo.services.TilgangskontrollService;
import org.slf4j.Logger;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.ws.rs.ForbiddenException;
import java.util.List;

import static no.nav.syfo.oidc.OIDCIssuer.EKSTERN;
import static no.nav.syfo.utils.OIDCUtil.getSubjectEkstern;
import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@ProtectedWithClaims(issuer = EKSTERN)
@RequestMapping(value = "/api/arbeidsforhold")
public class ArbeidsforholdRessurs {

    private static final Logger log = getLogger(ArbeidsforholdRessurs.class);

    private final Metric metric;
    private final OIDCRequestContextHolder contextHolder;
    private final ArbeidsforholdService arbeidsforholdService;
    private final TilgangskontrollService tilgangskontrollService;

    @Inject
    public ArbeidsforholdRessurs(
            Metric metric,
            OIDCRequestContextHolder contextHolder,
            ArbeidsforholdService arbeidsforholdService,
            TilgangskontrollService tilgangskontrollService
    ) {
        this.metric = metric;
        this.contextHolder = contextHolder;
        this.arbeidsforholdService = arbeidsforholdService;
        this.tilgangskontrollService = tilgangskontrollService;
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public List<RSStilling> hentArbeidsforhold(
            @RequestParam("fnr") String oppslaattFnr,
            @RequestParam("virksomhetsnummer") String virksomhetsnummer,
            @RequestParam("fom") String fom
    ) {
        metric.countEndpointRequest("hentArbeidsforhold");

        String innloggetFnr = getSubjectEkstern(contextHolder);

        if (tilgangskontrollService.sporOmNoenAndreEnnSegSelvEllerEgneAnsatte(innloggetFnr, oppslaattFnr)) {
            log.error("Fikk ikke hentet arbeidsforhold: Innlogget person spurte om fnr man ikke lov til fordi det er hverken seg selv eller en av sine ansatte.");
            throw new ForbiddenException();
        }

        return arbeidsforholdService.hentBrukersArbeidsforholdHosVirksomhet(oppslaattFnr, virksomhetsnummer, fom);
    }
}
