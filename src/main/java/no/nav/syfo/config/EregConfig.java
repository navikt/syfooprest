package no.nav.syfo.config;

import no.nav.syfo.services.ws.*;
import no.nav.tjeneste.virksomhet.organisasjon.v4.OrganisasjonV4;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.*;

import static java.util.Collections.singletonList;

@Configuration
public class EregConfig {

    public static final String MOCK_KEY = "ereg.withmock";
    @Value("${servicegateway.url}")
    private String serviceUrl;

    @Bean
    @Primary
    @ConditionalOnProperty(value = MOCK_KEY, havingValue = "false", matchIfMissing = true)
    public OrganisasjonV4 organisasjonV4() {
        OrganisasjonV4 port = factory();
        STSClientConfig.configureRequestSamlToken(port);
        return port;
    }

    @SuppressWarnings("unchecked")
    private OrganisasjonV4 factory() {
        return new WsClient<OrganisasjonV4>()
                .createPort(serviceUrl, OrganisasjonV4.class, singletonList(new LogErrorHandler()));
    }
}
