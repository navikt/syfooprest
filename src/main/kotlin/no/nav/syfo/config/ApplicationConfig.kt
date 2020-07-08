package no.nav.syfo.config

import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.*
import org.springframework.web.client.RestTemplate

@Configuration
@EnableCaching
class ApplicationConfig {
    @Bean
    @Primary
    fun restTemplate(): RestTemplate {
        return RestTemplate()
    }
}
