package no.nav.syfo.consumer.sts

import no.nav.syfo.metric.Metric
import no.nav.syfo.util.X_NAV_APIKEY
import no.nav.syfo.util.basicHeader
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.*
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClientResponseException
import org.springframework.web.client.RestTemplate
import java.time.LocalDateTime

@Service
class StsConsumer(
        private val metric: Metric,
        @Value("\${security.token.service.rest.url}") private val baseUrl: String,
        @Value("\${srv.fss.username}") private val username: String,
        @Value("\${srv.fss.password}") private val password: String,
        @Value("\${syfooprest.api.ststoken.apikey.password}") private val apiKeySTS: String,
        private val restTemplateDefault: RestTemplate
) {
    private var cachedOidcToken: STSToken? = null

    fun token(): String {
        if (STSToken.shouldRenew(cachedOidcToken)) {
            val request = HttpEntity<Any>(authorizationHeader())

            try {
                val response = restTemplateDefault.exchange<STSToken>(
                        getStsTokenUrl(),
                        HttpMethod.GET,
                        request,
                        object : ParameterizedTypeReference<STSToken>() {}
                )
                cachedOidcToken = response.body
                metric.countEvent(METRIC_CALL_STS_SUCCESS)
            } catch (e: RestClientResponseException) {
                LOG.error("Request to get STS failed with status: ${e.rawStatusCode} and message: ${e.responseBodyAsString}")
                metric.countEvent(METRIC_CALL_STS_FAIL)
                throw e
            }
        }

        return cachedOidcToken!!.access_token
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(StsConsumer::class.java)

        const val METRIC_CALL_STS_SUCCESS = "call_sts_success"
        const val METRIC_CALL_STS_FAIL = "call_sts_fail"
    }

    private fun getStsTokenUrl(): String {
        return "$baseUrl/rest/v1/sts/token?grant_type=client_credentials&scope=openid"
    }

    private fun authorizationHeader(): HttpHeaders {
        val credentials = basicHeader(username, password)
        val headers = HttpHeaders()
        headers.add(HttpHeaders.AUTHORIZATION, credentials)
        headers.add(X_NAV_APIKEY, apiKeySTS)
        return headers
    }
}

data class STSToken(
        val access_token: String,
        val token_type: String,
        val expires_in: Int
) {
    val expirationTime: LocalDateTime = LocalDateTime.now().plusSeconds(expires_in - 10L)

    companion object {
        fun shouldRenew(token: STSToken?): Boolean {
            if (token == null) {
                return true
            }

            return isExpired(token)
        }

        private fun isExpired(token: STSToken): Boolean {
            return token.expirationTime.isBefore(LocalDateTime.now())
        }
    }
}
