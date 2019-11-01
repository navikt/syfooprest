package no.nav.syfo.config;

import no.nav.syfo.services.ws.*;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.BrukerprofilV3;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.*;

import static java.util.Collections.singletonList;

@Configuration
public class TpsConfig {
    public static final String MOCK_KEY = "brukerprofil.withmock";
    @Value("${servicegateway.url}")
    private String serviceUrl;

    @Bean
    @Primary
    @ConditionalOnProperty(value = MOCK_KEY, havingValue = "false", matchIfMissing = true)
    public BrukerprofilV3 brukerprofilV3() {
        BrukerprofilV3 port = factory();
        STSClientConfig.configureRequestSamlToken(port);
        return port;
    }

    @SuppressWarnings("unchecked")
    private BrukerprofilV3 factory() {
        return new WsClient<BrukerprofilV3>()
                .createPort(serviceUrl, BrukerprofilV3.class, singletonList(new LogErrorHandler()));
    }
}
