package no.nav.syfo.person.controller

import no.nav.security.oidc.api.ProtectedWithClaims
import no.nav.security.oidc.context.OIDCRequestContextHolder
import no.nav.syfo.metric.Metric
import no.nav.syfo.api.auth.OIDCIssuer
import no.nav.syfo.person.pdl.PdlConsumer
import no.nav.syfo.person.pdl.fullName
import no.nav.syfo.tilgang.TilgangskontrollService
import no.nav.syfo.api.auth.OIDCUtil
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import javax.inject.Inject
import javax.ws.rs.ForbiddenException

@RestController
@ProtectedWithClaims(issuer = OIDCIssuer.EKSTERN)
@RequestMapping(value = ["/api/person/{fnr}"])
class PersonController @Inject constructor(
        private val metric: Metric,
        private val contextHolder: OIDCRequestContextHolder,
        private val pdlConsumer: PdlConsumer,
        private val tilgangskontrollService: TilgangskontrollService
) {
    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun hentNavn(@PathVariable("fnr") fnr: String): Person {
        metric.countEndpointRequest("hentPerson")
        val innloggetFnr = OIDCUtil.getSubjectEkstern(contextHolder)
        if (tilgangskontrollService.sporOmNoenAndreEnnSegSelvEllerEgneAnsatte(innloggetFnr, fnr)) {
            LOG.error("Fikk ikke hentet navn: Innlogget person spurte om fnr man ikke lov til fordi det er hverken seg selv eller en av sine ansatte.")
            throw ForbiddenException()
        }
        return Person(
            fnr = fnr,
            navn = pdlConsumer.person(fnr)?.fullName()
        )
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(PersonController::class.java)
    }
}
