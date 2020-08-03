package no.nav.syfo.tilgang.consumer

import no.nav.security.oidc.context.OIDCRequestContextHolder
import no.nav.syfo.metric.Metric
import no.nav.syfo.api.auth.OIDCIssuer
import no.nav.syfo.util.NAV_PERSONIDENT
import no.nav.syfo.util.bearerHeader
import no.nav.syfo.api.auth.OIDCUtil
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClientResponseException
import org.springframework.web.client.RestTemplate

@Service
class BrukerTilgangConsumer(
    private val oidcContextHolder: OIDCRequestContextHolder,
    private val metric: Metric,
    private val restTemplate: RestTemplate,
    @param:Value("\${syfoapi.url}") private val baseUrl: String
) {
    fun hasAccessToAnsatt(ansattFnr: String): Boolean {
        return try {
            val response = restTemplate.exchange<BrukerTilgang>(
                accessAnsattUrl,
                HttpMethod.GET,
                entity(ansattFnr),
                BrukerTilgang::class.java
            )
            val responseBody = response.body!!
            metric.countOutgoingReponses(METRIC_CALL_BRUKERTILGANG, response.statusCodeValue)
            responseBody.tilgang
        } catch (e: RestClientResponseException) {
            metric.countOutgoingReponses(METRIC_CALL_BRUKERTILGANG, e.rawStatusCode)
            if (e.rawStatusCode == 401) {
                throw RequestUnauthorizedException("Unauthorized request to get access to Ansatt from Syfobrukertilgang via Syfoapi")
            } else {
                LOG.error("Error requesting Ansatt access from Syfoppfolgingsplanservice via Syfoapi: ", e)
                throw e
            }
        }
    }

    private fun entity(personIdent: String): HttpEntity<*> {
        val headers = HttpHeaders()
        headers.add(HttpHeaders.AUTHORIZATION, bearerHeader(OIDCUtil.getIssuerToken(oidcContextHolder, OIDCIssuer.EKSTERN)))
        headers.add(NAV_PERSONIDENT, personIdent)
        return HttpEntity<Any>(headers)
    }

    private val accessAnsattUrl: String
        get() = "$baseUrl/syfooppfolgingsplanservice/api/tilgang/ansatt"

    companion object {
        private val LOG = LoggerFactory.getLogger(BrukerTilgangConsumer::class.java)

        private const val METRIC_CALL_BRUKERTILGANG = "call_syfoapi_ansatttilgang"
    }
}
