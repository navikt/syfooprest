package no.nav.syfo.virksomhet.consumer

import no.nav.syfo.services.ws.*
import no.nav.syfo.ws.STSClientConfig
import no.nav.syfo.ws.WsClient
import no.nav.tjeneste.virksomhet.organisasjon.v4.OrganisasjonV4
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.*

@Configuration
class EregConfig(
    @Value("\${servicegateway.url}") private val serviceUrl: String
) {
    @Bean
    @Primary
    @ConditionalOnProperty(value = [MOCK_KEY], havingValue = "false", matchIfMissing = true)
    fun organisasjonV4(): OrganisasjonV4 {
        val port = factory()
        STSClientConfig.configureRequestSamlToken(port)
        return port
    }

    private fun factory(): OrganisasjonV4 {
        return WsClient<OrganisasjonV4>()
            .createPort(serviceUrl, OrganisasjonV4::class.java, listOf(LogErrorHandler()))
    }

    companion object {
        const val MOCK_KEY = "ereg.withmock"
    }
}
