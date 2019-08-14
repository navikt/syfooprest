package no.nav.syfo.config;

import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.modig.jaxws.handlers.MDCOutHandler;
import no.nav.sbl.dialogarena.types.Pingable;
import no.nav.syfo.mock.OppfolgingMock;
import no.nav.tjeneste.virksomhet.sykefravaersoppfoelging.v1.SykefravaersoppfoelgingV1;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static java.lang.System.getProperty;
import static no.nav.sbl.dialogarena.common.cxf.InstanceSwitcher.createMetricsProxyWithInstanceSwitcher;
import static no.nav.sbl.dialogarena.types.Pingable.Ping.feilet;
import static no.nav.sbl.dialogarena.types.Pingable.Ping.lyktes;

@Configuration
public class SykefravaersoppfoelgingV1Config {

    public static final String MOCK_KEY = "sykefravaersoppfoelgingv1.withmock";
    private static final String ENDEPUNKT_NAVN = "SYKEFRAVERSOPPFOELGING_V1";
    public static final String ENDEPUNKT_URL_KEY = "SYKEFRAVAERSOPPFOELGING_V1_ENDPOINTURL";
    private static final String ENDEPUNKT_URL = getProperty(ENDEPUNKT_URL_KEY);
    private static final boolean KRITISK = true;

    @Bean
    public SykefravaersoppfoelgingV1 sykefravaersoppfoelgingV1ws() {
        SykefravaersoppfoelgingV1 prod = factory()
                .configureStsForExternalSSO()
                .withHandler(new MDCOutHandler())
                .build();
        SykefravaersoppfoelgingV1 mock = new OppfolgingMock();
        return createMetricsProxyWithInstanceSwitcher(ENDEPUNKT_NAVN, prod, mock, MOCK_KEY, SykefravaersoppfoelgingV1.class);
    }

    @Bean
    public Pingable sykefravaersoppfoelging() {
        Pingable.Ping.PingMetadata pingMetadata = new Pingable.Ping.PingMetadata(ENDEPUNKT_URL, ENDEPUNKT_NAVN, KRITISK);
        final SykefravaersoppfoelgingV1 sykefravaersoppfoelgingV1Ping = factory()
                .configureStsForSystemUser()
                .build();
        return () -> {
            try {
                sykefravaersoppfoelgingV1Ping.ping();
                return lyktes(pingMetadata);
            } catch (Exception e) {
                return feilet(pingMetadata, e);
            }
        };
    }

    private CXFClient<SykefravaersoppfoelgingV1> factory() {
        return new CXFClient<>(SykefravaersoppfoelgingV1.class)
                .address(ENDEPUNKT_URL);
    }

}
