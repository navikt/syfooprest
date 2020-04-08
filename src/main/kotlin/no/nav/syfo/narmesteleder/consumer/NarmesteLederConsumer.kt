package no.nav.syfo.narmesteleder.consumer

import no.nav.security.oidc.context.OIDCRequestContextHolder
import no.nav.syfo.metric.Metric
import no.nav.syfo.oidc.OIDCIssuer
import no.nav.syfo.util.NAV_PERSONIDENT
import no.nav.syfo.util.bearerHeader
import no.nav.syfo.utils.OIDCUtil
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
class NarmesteLederConsumer(
        private val oidcContextHolder: OIDCRequestContextHolder,
        private val metric: Metric,
        private val restTemplate: RestTemplate,
        @param:Value("\${syfoapi.url}") private val baseUrl: String
) {
    fun narmesteLeder(ansattFnr: String, virksomhetsnummer: String): Naermesteleder? {
        return try {
            val response = restTemplate.exchange<Naermesteleder>(
                    getNarmesteLederUrl(virksomhetsnummer),
                    HttpMethod.GET,
                    entity(ansattFnr),
                    object : ParameterizedTypeReference<Naermesteleder>() {}
            )
            if (response.statusCode == HttpStatus.NO_CONTENT) {
                metric.countEvent("call_syfoapi_narmesteleder_nocontent")
            } else if (response.statusCode != HttpStatus.OK) {
                metric.countEvent("call_syfoapi_narmesteleder_fail")
                val message = ERROR_MESSAGE_BASE + response.statusCode
                LOG.error(message)
                throw RuntimeException(message)
            }
            metric.countEvent("call_syfoapi_narmesteleder_success")
            response.body
        } catch (e: RestClientException) {
            LOG.error(ERROR_MESSAGE_BASE, e)
            metric.countEvent("call_syfoapi_narmesteleder_fail")
            throw e
        }
    }

    private fun entity(personIdent: String): HttpEntity<*> {
        val headers = HttpHeaders()
        headers.add(HttpHeaders.AUTHORIZATION, bearerHeader(OIDCUtil.getIssuerToken(oidcContextHolder, OIDCIssuer.EKSTERN)))
        headers.add(NAV_PERSONIDENT, personIdent)
        return HttpEntity<Any>(headers)
    }

    private fun getNarmesteLederUrl(virksomhetsnummer: String): String {
        return "$baseUrl/syfooppfolgingsplanservice/api/narmesteleder/$virksomhetsnummer"
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(NarmesteLederConsumer::class.java)
        const val ERROR_MESSAGE_BASE = "Error requesting Naermeste Leder from syfoppfolgingsplanservice via syfoapi"
    }

}
