package no.nav.syfo.narmesteleder.controller

import no.nav.security.token.support.core.api.ProtectedWithClaims
import no.nav.security.token.support.core.context.TokenValidationContextHolder
import no.nav.syfo.metric.Metric
import no.nav.syfo.narmesteleder.consumer.Naermesteleder
import no.nav.syfo.consumer.aktorregister.AktorregisterConsumer
import no.nav.syfo.tilgang.TilgangskontrollService
import no.nav.syfo.api.auth.OIDCUtil
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import javax.inject.Inject
import javax.ws.rs.ForbiddenException
import javax.ws.rs.NotFoundException
import no.nav.syfo.api.auth.OIDCIssuer.EKSTERN
import no.nav.syfo.narmesteleder.consumer.NermesteLedereConsumer

@RestController
@ProtectedWithClaims(issuer = EKSTERN)
@RequestMapping(value = ["/api/nermesteledere/{fnr}"])
class NermesteledereController @Inject constructor(
    private val metric: Metric,
    private val contextHolder: TokenValidationContextHolder,
    private val tilgangskontrollService: TilgangskontrollService,
    private val aktorregisterConsumer: AktorregisterConsumer,
    private val nermesteLedereConsumer: NermesteLedereConsumer
) {
    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun hentNermesteLeder(
        @PathVariable("fnr") fnr: String
    ): List<RSNaermesteLeder> {
        metric.countEndpointRequest("hentNermesteLedere")
        val innloggetFnr = OIDCUtil.getSubjectEkstern(contextHolder)

        if (tilgangskontrollService.sporOmNoenAndreEnnSegSelvEllerEgneAnsatte(innloggetFnr, fnr)) {
            LOG.error("Fikk ikke hentet liste over narmeste ledere: Innlogget person spurte om fnr man ikke lov til fordi det er hverken seg selv eller en av sine ansatte.")
            throw ForbiddenException()
        }

        val nermesteLedere = nermesteLedereConsumer.nermesteLedere(fnr)

        when {
            nermesteLedere == null -> {
                throw NotFoundException()
            }
            else -> {
                val nermesteLedereMapped = mutableListOf<RSNaermesteLeder>()
                nermesteLedere.filter { it.naermesteLederStatus.erAktiv }.forEach {
                    nermesteLedereMapped.add(mapNermesteLeder(it))
                }
                return nermesteLedereMapped
            }
        }
    }

    private fun mapNermesteLeder(nermesteleder: Naermesteleder): RSNaermesteLeder {
        val lederFnr = aktorregisterConsumer.hentFnrForAktor(nermesteleder.naermesteLederAktoerId)
        return RSNaermesteLeder(
            virksomhetsnummer = nermesteleder.orgnummer,
            navn = nermesteleder.navn,
            epost = nermesteleder.epost,
            tlf = nermesteleder.mobil,
            erAktiv = nermesteleder.naermesteLederStatus.erAktiv,
            aktivFom = nermesteleder.naermesteLederStatus.aktivFom,
            aktivTom = nermesteleder.naermesteLederStatus.aktivTom,
            fnr = lederFnr,
            samtykke = null,
            sistInnlogget = null
        )
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(NermesteledereController::class.java)
    }
}
