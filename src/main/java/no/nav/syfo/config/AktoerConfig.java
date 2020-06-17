package no.nav.syfo.config;

import no.nav.syfo.services.ws.LogErrorHandler;
import no.nav.syfo.services.ws.STSClientConfig;
import no.nav.tjeneste.virksomhet.aktoer.v2.AktoerV2;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.feature.LoggingFeature;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.PhaseInterceptor;
import org.apache.cxf.ws.addressing.WSAddressingFeature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.*;

import javax.xml.namespace.QName;
import java.util.Arrays;

import static java.util.Collections.singletonList;

@Configuration
public class AktoerConfig {

    private static final String AKTOER_V_2_WSDL = "classpath:wsdl/no/nav/tjeneste/virksomhet/aktoer/v2/Binding.wsdl";
    private static final String AKTOER_V_2_NAMESPACE = "http://nav.no/tjeneste/virksomhet/aktoer/v2/Binding/";
    private static final QName AKTOER_V_2_SERVICE = new QName(AKTOER_V_2_NAMESPACE, "Aktoer");
    private static final QName AKTOER_V_2_PORT = new QName(AKTOER_V_2_NAMESPACE, "Aktoer_v2Port");

    public static final String MOCK_KEY = "aktoer.withmock";
    @Value("${servicegateway.url}")
    private String serviceUrl;

    @Bean
    @ConditionalOnProperty(value = MOCK_KEY, havingValue = "false", matchIfMissing = true)
    @Primary
    public AktoerV2 aktoerV2() {
        AktoerV2 port = factory();
        STSClientConfig.configureRequestSamlToken(port);
        return port;
    }

    @SuppressWarnings("unchecked")
    private AktoerV2 factory() {
        return getPort(singletonList(new LogErrorHandler()));
    }

    AktoerV2 getPort(List<Handler> handlers, PhaseInterceptor<? extends Message>... interceptors) {
        JaxWsProxyFactoryBean factoryBean = new JaxWsProxyFactoryBean();
        factoryBean.setWsdlURL(AKTOER_V_2_WSDL);
        factoryBean.setServiceName(AKTOER_V_2_SERVICE);
        factoryBean.setEndpointName(AKTOER_V_2_PORT);
        factoryBean.setServiceClass(AktoerV2.class);
        factoryBean.setAddress(serviceUrl + "aktoerregister/ws/Aktoer/v2");
        factoryBean.getFeatures().add(new WSAddressingFeature());
        factoryBean.getFeatures().add(new LoggingFeature());
        AktoerV2 port = (AktoerV2) factoryBean.create();
        ((BindingProvider) port).getBinding().setHandlerChain(handlers);
        Client client = ClientProxy.getClient(port);
        Arrays.stream(interceptors).forEach(client.getOutInterceptors()::add);
        return factoryBean.create(AktoerV2.class);
    }
}
