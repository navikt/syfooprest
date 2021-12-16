package no.nav.syfo.narmesteleder.controller

import no.nav.security.token.support.core.api.ProtectedWithClaims
import no.nav.security.token.support.core.context.TokenValidationContextHolder
import no.nav.syfo.api.auth.OIDCIssuer.EKSTERN
import no.nav.syfo.api.auth.OIDCUtil
import no.nav.syfo.metric.Metric
import no.nav.syfo.narmesteleder.consumer.NarmesteLederConsumer
import no.nav.syfo.tilgang.TilgangskontrollService
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import javax.inject.Inject
import javax.ws.rs.ForbiddenException
import javax.ws.rs.NotFoundException

@RestController
@ProtectedWithClaims(issuer = EKSTERN)
@RequestMapping(value = ["/api/naermesteleder/{fnr}"])
class NaermesteLederController @Inject constructor(
    private val metric: Metric,
    private val contextHolder: TokenValidationContextHolder,
    private val tilgangskontrollService: TilgangskontrollService,
    private val narmesteLederConsumer: NarmesteLederConsumer
) {
    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun hentNaermesteLeder(
        @PathVariable("fnr") fnr: String,
        @RequestParam("virksomhetsnummer") virksomhetsnummer: String
    ): RSNaermesteLeder {
        metric.countEndpointRequest("hentNaermesteLeder")
        val innloggetFnr = OIDCUtil.getSubjectEkstern(contextHolder)
        if (tilgangskontrollService.sporOmNoenAndreEnnSegSelvEllerEgneAnsatte(innloggetFnr, fnr)) {
            LOG.error("Fikk ikke hentet narmeste leder: Innlogget person spurte om fnr man ikke lov til fordi det er hverken seg selv eller en av sine ansatte.")
            throw ForbiddenException()
        }

        val narmesteleder = narmesteLederConsumer.narmesteLeder(fnr, virksomhetsnummer)

        when {
            narmesteleder == null -> {
                throw NotFoundException()
            }
            !narmesteleder.naermesteLederStatus.erAktiv -> {
                throw NotFoundException()
            }
            else -> {
                return mapNarmesteLeder(narmesteleder)
            }
        }
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(NaermesteLederController::class.java)
    }
}
