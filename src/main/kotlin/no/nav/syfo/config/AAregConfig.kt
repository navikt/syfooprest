package no.nav.syfo.config

import no.nav.syfo.services.ws.*
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.binding.ArbeidsforholdV3
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.*
import javax.xml.ws.handler.Handler

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
            .createPort(serviceUrl, ArbeidsforholdV3::class.java, listOf<Handler<*>>(LogErrorHandler()))
    }

    companion object {
        const val MOCK_KEY = "arbeidsforhold.aareg.withmock"
    }
}
