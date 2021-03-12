package no.nav.syfo.narmesteleder.consumer

import no.nav.security.token.support.core.context.TokenValidationContextHolder
import no.nav.syfo.metric.Metric
import no.nav.syfo.api.auth.OIDCIssuer
import no.nav.syfo.util.NAV_PERSONIDENT
import no.nav.syfo.util.bearerHeader
import no.nav.syfo.api.auth.OIDCUtil
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate

@Service
class NermesteLedereConsumer(
    private val contextHolder: TokenValidationContextHolder,
    private val metric: Metric,
    private val restTemplate: RestTemplate,
    @param:Value("\${syfoapi.url}") private val baseUrl: String
) {
    fun nermesteLedere(ansattFnr: String): List<Naermesteleder>? {
        return try {
            val response = restTemplate.exchange(
                getNermesteLedereUrl(),
                HttpMethod.GET,
                entity(ansattFnr),
                object : ParameterizedTypeReference<List<Naermesteleder>>() {}
            )
            if (response.statusCode == HttpStatus.NO_CONTENT) {
                metric.countEvent("call_syfoapi_nermesteledere_nocontent")
            } else if (response.statusCode != HttpStatus.OK) {
                metric.countEvent("call_syfoapi_nermesteledere_fail")
                val message = ERROR_MESSAGE_BASE + response.statusCode
                LOG.error(message)
                throw RuntimeException(message)
            }
            metric.countEvent("call_syfoapi_nermesteledere_success")
            response.body
        } catch (e: RestClientException) {
            LOG.error(ERROR_MESSAGE_BASE, e)
            metric.countEvent("call_syfoapi_nermesteledere_fail")
            throw e
        }
    }

    private fun entity(personIdent: String): HttpEntity<*> {
        val headers = HttpHeaders()
        headers.add(HttpHeaders.AUTHORIZATION, bearerHeader(OIDCUtil.getIssuerToken(contextHolder, OIDCIssuer.EKSTERN)))
        headers.add(NAV_PERSONIDENT, personIdent)
        return HttpEntity<Any>(headers)
    }

    private fun getNermesteLedereUrl(): String {
        return "$baseUrl/syfooppfolgingsplanservice/api/nermesteledere"
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(NermesteLedereConsumer::class.java)
        const val ERROR_MESSAGE_BASE = "Error requesting Naermeste Leder from syfoppfolgingsplanservice via syfoapi"
    }
}
