package no.nav.syfo.config;

import no.nav.apiapp.ApiApplication;
import no.nav.apiapp.config.ApiAppConfigurator;
import no.nav.metrics.aspects.CountAspect;
import no.nav.metrics.aspects.TimerAspect;
import no.nav.syfo.config.caching.CacheConfig;
import org.springframework.context.annotation.*;

import static no.nav.apiapp.ApiApplication.Sone.SBS;

@Configuration
@EnableAspectJAutoProxy
@Import({
        CacheConfig.class,
        ServiceConfig.class,
        AktoerConfig.class,
        AAregConfig.class,
        TpsConfig.class,
        DkifConfig.class,
        EregConfig.class,
        SykefravaersoppfoelgingV1Config.class,
})
@ComponentScan(basePackages = "no.nav.syfo.rest")
public class ApplicationConfig implements ApiApplication.NaisApiApplication {

    @Bean
    public TimerAspect timerAspect() {
        return new TimerAspect();
    }

    @Bean
    public CountAspect countAspect() {
        return new CountAspect();
    }

    @Override
    public String getApplicationName() {
        return "restoppfoelgingsdialog";
    }

    @Override
    public Sone getSone() {
        return SBS;
    }

    @Override
    public void configure(ApiAppConfigurator apiAppConfigurator) {
        apiAppConfigurator
                .sts()
                .openAmLogin();
    }
}
