package no.nav.syfo.config;

import no.nav.apiapp.ApiApplication;
import no.nav.apiapp.config.ApiAppConfigurator;
import no.nav.metrics.aspects.CountAspect;
import no.nav.metrics.aspects.TimerAspect;
import no.nav.syfo.config.caching.CacheConfig;
import org.springframework.context.annotation.*;

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
public class ApplicationConfig implements ApiApplication{

    @Bean
    public TimerAspect timerAspect() {
        return new TimerAspect();
    }

    @Bean
    public CountAspect countAspect() {
        return new CountAspect();
    }

    @Override
    public void configure(ApiAppConfigurator apiAppConfigurator) {
        apiAppConfigurator
                .sts()
                .openAmLogin();
    }
}
