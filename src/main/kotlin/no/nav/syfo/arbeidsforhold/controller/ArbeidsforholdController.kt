package no.nav.syfo.arbeidsforhold.controller

import no.nav.security.token.support.core.api.ProtectedWithClaims
import no.nav.security.token.support.core.context.TokenValidationContextHolder
import no.nav.syfo.api.auth.OIDCIssuer.EKSTERN
import no.nav.syfo.api.auth.OIDCUtil.getSubjectEkstern
import no.nav.syfo.arbeidsforhold.aareg.ArbeidsforholdConsumer
import no.nav.syfo.metric.Metric
import no.nav.syfo.tilgang.TilgangskontrollService
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import javax.inject.Inject
import javax.ws.rs.ForbiddenException

@RestController
@ProtectedWithClaims(issuer = EKSTERN)
@RequestMapping(value = ["/api/arbeidsforhold"])
class ArbeidsforholdController @Inject constructor(
    private val metric: Metric,
    private val contextHolder: TokenValidationContextHolder,
    private val arbeidsforholdConsumer: ArbeidsforholdConsumer,
    private val tilgangskontrollService: TilgangskontrollService
) {
    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun hentArbeidsforhold(
        @RequestParam("fnr") oppslaattFnr: String,
        @RequestParam("virksomhetsnummer") virksomhetsnummer: String,
        @RequestParam("fom") fom: String
    ): List<RSStilling> {
        metric.countEndpointRequest("hentArbeidsforhold")
        val innloggetFnr = getSubjectEkstern(contextHolder)
        if (tilgangskontrollService.sporOmNoenAndreEnnSegSelvEllerEgneAnsatte(innloggetFnr, oppslaattFnr)) {
            log.error("Fikk ikke hentet arbeidsforhold: Innlogget person spurte om fnr man ikke lov til fordi det er hverken seg selv eller en av sine ansatte.")
            throw ForbiddenException()
        }
        return arbeidsforholdConsumer.hentBrukersArbeidsforholdHosVirksomhet(oppslaattFnr, virksomhetsnummer, fom)
    }

    companion object {
        private val log = LoggerFactory.getLogger(ArbeidsforholdController::class.java)
    }
}
