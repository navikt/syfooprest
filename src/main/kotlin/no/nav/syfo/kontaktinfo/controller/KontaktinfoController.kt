package no.nav.syfo.kontaktinfo.controller

import no.nav.security.token.support.core.api.ProtectedWithClaims
import no.nav.security.token.support.core.context.TokenValidationContextHolder
import no.nav.syfo.api.auth.OIDCIssuer.EKSTERN
import no.nav.syfo.api.auth.OIDCUtil.getSubjectEkstern
import no.nav.syfo.consumer.aktorregister.AktorregisterConsumer
import no.nav.syfo.kontaktinfo.consumer.DkifConsumer
import no.nav.syfo.metric.Metric
import no.nav.syfo.tilgang.TilgangskontrollService
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import javax.inject.Inject
import javax.ws.rs.ForbiddenException

@RestController
@ProtectedWithClaims(issuer = EKSTERN)
@RequestMapping(value = ["/api/kontaktinfo/{fnr}"])
class KontaktinfoController @Inject constructor(
    private val metric: Metric,
    private val contextHolder: TokenValidationContextHolder,
    private val dkifConsumer: DkifConsumer,
    private val aktorregisterConsumer: AktorregisterConsumer,
    private val tilgangskontrollService: TilgangskontrollService
) {
    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun hentKontaktinfo(@PathVariable("fnr") oppslaattFnr: String): RSKontaktinfo {
        metric.countEndpointRequest("hentKontaktinfo")
        val innloggetFnr = getSubjectEkstern(contextHolder)
        if (tilgangskontrollService.sporOmNoenAndreEnnSegSelvEllerEgneAnsatte(innloggetFnr, oppslaattFnr)) {
            log.error("Fikk ikke hentet kontaktinfo: Innlogget person spurte om fnr man ikke lov til fordi det er hverken seg selv eller en av sine ansatte.")
            throw ForbiddenException()
        }
        val oppslaattAktoerId = aktorregisterConsumer.hentAktorIdForFnr(oppslaattFnr)
        return dkifConsumer.hentRSKontaktinfoAktoerId(oppslaattAktoerId)
    }

    companion object {
        private val log = LoggerFactory.getLogger(KontaktinfoController::class.java)
    }
}
