package no.nav.syfo

import no.nav.security.token.support.test.spring.TokenGeneratorConfiguration
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.core.env.Environment
import java.util.*

@Configuration
@Import(TokenGeneratorConfiguration::class)
class LocalApplicationConfig(environment: Environment) {
    init {
        System.setProperty("SECURITYTOKENSERVICE_URL", "q12123")
        System.setProperty("SRV_USERNAME", Objects.requireNonNull(environment.getProperty("srv.username")))
        System.setProperty("SRV_PASSWORD", Objects.requireNonNull(environment.getProperty("srv.password")))
    }
}
