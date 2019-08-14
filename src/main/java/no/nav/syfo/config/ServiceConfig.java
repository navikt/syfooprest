package no.nav.syfo.config;

import no.nav.syfo.services.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceConfig {

    @Bean
    public AktoerService aktoerService() {
        return new AktoerService();
    }

    @Bean
    public ArbeidsforholdService arbeidsforholdService() {
        return new ArbeidsforholdService();
    }

    @Bean
    public BrukerprofilService brukerprofilService() {
        return new BrukerprofilService();
    }

    @Bean
    public DkifService dkifService() {
        return new DkifService();
    }

    @Bean
    public TilgangskontrollService tilgangskontrollService() {
        return new TilgangskontrollService();
    }

    @Bean
    public OrganisasjonService organisasjonService() {
        return new OrganisasjonService();
    }

    @Bean
    public NaermesteLederService naermesteLederService() {
        return new NaermesteLederService();
    }

}

