package no.nav.syfo;

import no.nav.security.spring.oidc.api.EnableOIDCTokenValidation;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableOIDCTokenValidation
public class LocalApplication {
    public static void main(String[] args) {
        SpringApplication.run(LocalApplication.class, args);
    }
}
