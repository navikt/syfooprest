package no.nav.syfo.person.pdl

import no.nav.syfo.metric.Metric
import no.nav.syfo.consumer.sts.StsConsumer
import no.nav.syfo.util.*
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.*
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClientResponseException
import org.springframework.web.client.RestTemplate

@Service
class PdlConsumer(
    private val metric: Metric,
    @Value("\${pdl.url}") private val pdlUrl: String,
    @Value("\${syfooprest.api.pdlapi.apikey.password}") private val apiKeyPdlApi: String,
    private val stsConsumer: StsConsumer,
    private val restTemplate: RestTemplate
) {
    fun person(ident: String): PdlHentPerson? {
        val query = this::class.java.getResource("/pdl/hentPerson.graphql").readText().replace("[\n\r]", "")
        val entity = createRequestEntity(PdlRequest(query, Variables(ident)))

        try {
            val pdlPerson = restTemplate.exchange<PdlPersonResponse>(
                pdlUrl,
                HttpMethod.POST,
                entity,
                object : ParameterizedTypeReference<PdlPersonResponse>() {}
            )

            val pdlPersonReponse = pdlPerson.body!!
            return if (pdlPersonReponse.errors != null && pdlPersonReponse.errors.isNotEmpty()) {
                metric.countEvent(METRIC_CALL_PDL_FAIL)
                pdlPersonReponse.errors.forEach {
                    LOG.error("Error while requesting person from PersonDataLosningen: ${it.errorMessage()}")
                }
                null
            } else {
                metric.countEvent(METRIC_CALL_PDL_SUCCESS)
                pdlPersonReponse.data
            }
        } catch (exception: RestClientResponseException) {
            metric.countEvent(METRIC_CALL_PDL_FAIL)
            LOG.error("Error from PDL with request-url: $pdlUrl", exception)
            throw exception
        }
    }

    private fun createRequestEntity(request: PdlRequest): HttpEntity<PdlRequest> {
        val stsToken: String = stsConsumer.token()
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        headers.set(TEMA_HEADER, ALLE_TEMA_HEADERVERDI)
        headers.set(AUTHORIZATION, bearerHeader(stsToken))
        headers[NAV_CONSUMER_ID_HEADER] = APP_CONSUMER_ID
        headers[NAV_CALL_ID_HEADER] = createCallId()
        headers.set(NAV_CONSUMER_TOKEN_HEADER, bearerHeader(stsToken))
        headers.add(X_NAV_APIKEY, apiKeyPdlApi)
        return HttpEntity(request, headers)
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(PdlConsumer::class.java)

        const val METRIC_CALL_PDL_SUCCESS = "call_pdl_success"
        const val METRIC_CALL_PDL_FAIL = "call_pdl_fail"
    }
}
