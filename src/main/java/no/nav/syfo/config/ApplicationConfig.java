package no.nav.syfo.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.*;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableCaching
public class ApplicationConfig {

    @Bean
    @Primary
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
