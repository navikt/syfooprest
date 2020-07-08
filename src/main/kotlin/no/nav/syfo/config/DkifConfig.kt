package no.nav.syfo.config

import no.nav.syfo.services.ws.*
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.DigitalKontaktinformasjonV1
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.*
import javax.xml.ws.handler.Handler

@Configuration
class DkifConfig(
    @Value("\${servicegateway.url}") private val serviceUrl: String
) {
    @Bean
    @Primary
    @ConditionalOnProperty(value = [MOCK_KEY], havingValue = "false", matchIfMissing = true)
    fun digitalKontaktinformasjonV1(): DigitalKontaktinformasjonV1 {
        val port = factory()
        STSClientConfig.configureRequestSamlToken(port)
        return port
    }

    private fun factory(): DigitalKontaktinformasjonV1 {
        return WsClient<DigitalKontaktinformasjonV1>()
            .createPort(serviceUrl, DigitalKontaktinformasjonV1::class.java, listOf<Handler<*>>(LogErrorHandler()))
    }

    companion object {
        const val MOCK_KEY = "dkif.withmock"
    }
}