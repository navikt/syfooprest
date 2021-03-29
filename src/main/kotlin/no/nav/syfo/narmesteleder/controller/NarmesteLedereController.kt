package no.nav.syfo.narmesteleder.controller

import no.nav.security.token.support.core.api.ProtectedWithClaims
import no.nav.security.token.support.core.context.TokenValidationContextHolder
import no.nav.syfo.api.auth.OIDCIssuer.EKSTERN
import no.nav.syfo.api.auth.OIDCUtil
import no.nav.syfo.metric.Metric
import no.nav.syfo.narmesteleder.consumer.NarmesteLedereConsumer
import no.nav.syfo.tilgang.TilgangskontrollService
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import javax.inject.Inject
import javax.ws.rs.ForbiddenException
import javax.ws.rs.NotFoundException

@RestController
@ProtectedWithClaims(issuer = EKSTERN)
@RequestMapping(value = ["/api/narmesteledere/{fnr}"])
class NarmesteLedereController @Inject constructor(
    private val metric: Metric,
    private val contextHolder: TokenValidationContextHolder,
    private val tilgangskontrollService: TilgangskontrollService,
    private val narmesteLederMapper: NarmesteLederMapper,
    private val narmesteLedereConsumer: NarmesteLedereConsumer
) {
    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun hentNermesteLedere(
        @PathVariable("fnr") fnr: String
    ): List<RSNaermesteLeder> {
        metric.countEndpointRequest("hentNermesteLedere")
        val innloggetFnr = OIDCUtil.getSubjectEkstern(contextHolder)

        if (tilgangskontrollService.sporOmNoenAndreEnnSegSelvEllerEgneAnsatte(innloggetFnr, fnr)) {
            LOG.error("Fikk ikke hentet liste over narmeste ledere: Innlogget person spurte om fnr man ikke lov til fordi det er hverken seg selv eller en av sine ansatte.")
            throw ForbiddenException()
        }

        val narmesteLedere = narmesteLedereConsumer.narmesteLedere(fnr)

        when {
            narmesteLedere == null -> {
                throw NotFoundException()
            }
            else -> {
                val narmesteLedereMapped = mutableListOf<RSNaermesteLeder>()

                narmesteLedere.filter { it.naermesteLederStatus.erAktiv }.forEach {
                    narmesteLedereMapped.add(narmesteLederMapper.map(it))
                }

                if (narmesteLedereMapped.isEmpty()) {
                    throw NotFoundException()
                }

                return narmesteLedereMapped
            }
        }
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(NarmesteLedereController::class.java)
    }
}
