package no.nav.syfo.consumer.aktorregister

import no.nav.syfo.services.ws.LogErrorHandler
import no.nav.syfo.services.ws.STSClientConfig
import no.nav.tjeneste.virksomhet.aktoer.v2.AktoerV2
import org.apache.cxf.feature.LoggingFeature
import org.apache.cxf.frontend.ClientProxy
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean
import org.apache.cxf.message.Message
import org.apache.cxf.phase.PhaseInterceptor
import org.apache.cxf.ws.addressing.WSAddressingFeature
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.*
import java.util.*
import javax.xml.namespace.QName
import javax.xml.ws.BindingProvider
import javax.xml.ws.handler.Handler

@Configuration
class AktorConfig(
    @Value("\${servicegateway.url}") private val serviceUrl: String
) {
    @Bean
    @ConditionalOnProperty(value = [MOCK_KEY], havingValue = "false", matchIfMissing = true)
    @Primary
    fun aktoerV2(): AktoerV2 {
        val port = factory()
        STSClientConfig.configureRequestSamlToken(port)
        return port
    }

    private fun factory(): AktoerV2 {
        return getPort(listOf<Handler<*>>(LogErrorHandler()))
    }

    fun getPort(handlers: List<Handler<*>?>?, vararg interceptors: PhaseInterceptor<out Message>): AktoerV2 {
        val factoryBean = JaxWsProxyFactoryBean()
        factoryBean.wsdlURL = AKTOER_V_2_WSDL
        factoryBean.serviceName = AKTOER_V_2_SERVICE
        factoryBean.endpointName = AKTOER_V_2_PORT
        factoryBean.serviceClass = AktoerV2::class.java
        factoryBean.address = serviceUrl + "aktoerregister/ws/Aktoer/v2"
        factoryBean.features.add(WSAddressingFeature())
        factoryBean.features.add(LoggingFeature())
        val port = factoryBean.create() as AktoerV2
        (port as BindingProvider).binding.handlerChain = handlers
        val client = ClientProxy.getClient(port)
        Arrays.stream(interceptors).forEach { e: PhaseInterceptor<out Message> -> client.outInterceptors.add(e) }
        return factoryBean.create(AktoerV2::class.java)
    }

    companion object {
        private const val AKTOER_V_2_WSDL = "classpath:wsdl/no/nav/tjeneste/virksomhet/aktoer/v2/Binding.wsdl"
        private const val AKTOER_V_2_NAMESPACE = "http://nav.no/tjeneste/virksomhet/aktoer/v2/Binding/"
        private val AKTOER_V_2_SERVICE = QName(AKTOER_V_2_NAMESPACE, "Aktoer")
        private val AKTOER_V_2_PORT = QName(AKTOER_V_2_NAMESPACE, "Aktoer_v2Port")
        const val MOCK_KEY = "aktoer.withmock"
    }
}
