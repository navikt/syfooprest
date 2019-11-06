package no.nav.syfo.localconfig;

import no.nav.security.spring.oidc.test.TokenGeneratorConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;

import static java.util.Objects.requireNonNull;

@Configuration
@Import(TokenGeneratorConfiguration.class)
public class LocalApplicationConfig {

    public LocalApplicationConfig(Environment environment) {
        System.setProperty("SECURITYTOKENSERVICE_URL", "q12123");
        System.setProperty("SRV_USERNAME", requireNonNull(environment.getProperty("srvrestoppfoelgingsdialog.username")));
        System.setProperty("SRV_PASSWORD", requireNonNull(environment.getProperty("srvrestoppfoelgingsdialog.password")));
    }
}
