package no.nav.syfo.narmesteleder.controller

import no.nav.security.oidc.api.ProtectedWithClaims
import no.nav.security.oidc.context.OIDCRequestContextHolder
import no.nav.syfo.metric.Metric
import no.nav.syfo.narmesteleder.consumer.Naermesteleder
import no.nav.syfo.narmesteleder.consumer.NarmesteLederConsumer
import no.nav.syfo.api.auth.OIDCIssuer
import no.nav.syfo.consumer.aktorregister.AktorregisterConsumer
import no.nav.syfo.tilgang.TilgangskontrollService
import no.nav.syfo.api.auth.OIDCUtil
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import javax.inject.Inject
import javax.ws.rs.ForbiddenException
import javax.ws.rs.NotFoundException

@RestController
@ProtectedWithClaims(issuer = OIDCIssuer.EKSTERN)
@RequestMapping(value = ["/api/naermesteleder/{fnr}"])
class NaermestelederController @Inject constructor(
    private val metric: Metric,
    private val contextHolder: OIDCRequestContextHolder,
    private val tilgangskontrollService: TilgangskontrollService,
    private val aktorregisterConsumer: AktorregisterConsumer,
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

        val naermesteleder = narmesteLederConsumer.narmesteLeder(fnr, virksomhetsnummer)
        when {
            naermesteleder == null -> {
                throw NotFoundException()
            }
            !naermesteleder.naermesteLederStatus.erAktiv -> {
                throw NotFoundException()
            }
            else -> {
                return mapNaermesteLeder(naermesteleder)
            }
        }
    }

    private fun mapNaermesteLeder(naermesteleder: Naermesteleder): RSNaermesteLeder {
        val lederFnr = aktorregisterConsumer.hentFnrForAktor(naermesteleder.naermesteLederAktoerId)
        return RSNaermesteLeder(
                virksomhetsnummer = naermesteleder.orgnummer,
                navn = naermesteleder.navn,
                epost = naermesteleder.epost,
                tlf = naermesteleder.mobil,
                erAktiv = naermesteleder.naermesteLederStatus.erAktiv,
                aktivFom = naermesteleder.naermesteLederStatus.aktivFom,
                aktivTom = naermesteleder.naermesteLederStatus.aktivTom,
                fnr = lederFnr,
                samtykke = null,
                sistInnlogget = null
        )
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(NaermestelederController::class.java)
    }
}
