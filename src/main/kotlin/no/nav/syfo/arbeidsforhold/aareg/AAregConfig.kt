package no.nav.syfo.arbeidsforhold.aareg

import no.nav.syfo.ws.*
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.binding.ArbeidsforholdV3
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.*

@Configuration
class AAregConfig(
    @Value("\${servicegateway.url}") private val serviceUrl: String
) {
    @Bean
    @ConditionalOnProperty(value = [MOCK_KEY], havingValue = "false", matchIfMissing = true)
    @Primary
    fun arbeidsforholdV3(): ArbeidsforholdV3 {
        val port = factory()
        STSClientConfig.configureRequestSamlToken(port)
        return port
    }

    private fun factory(): ArbeidsforholdV3 {
        return WsClient<ArbeidsforholdV3>()
            .createPort(serviceUrl, ArbeidsforholdV3::class.java, listOf(LogErrorHandler()))
    }

    companion object {
        const val MOCK_KEY = "arbeidsforhold.aareg.withmock"
    }
}
