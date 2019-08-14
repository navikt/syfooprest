package no.nav.syfo.config;


import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.sbl.dialogarena.types.Pingable;
import no.nav.syfo.mock.AktoerMock;
import no.nav.tjeneste.virksomhet.aktoer.v2.AktoerV2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static java.lang.System.getProperty;
import static no.nav.sbl.dialogarena.common.cxf.InstanceSwitcher.createMetricsProxyWithInstanceSwitcher;
import static no.nav.sbl.dialogarena.types.Pingable.Ping.feilet;
import static no.nav.sbl.dialogarena.types.Pingable.Ping.lyktes;

@Configuration
public class AktoerConfig {

    public static final String MOCK_KEY = "aktoer.withmock";
    public static final String ENDEPUNKT_URL_KEY = "AKTOER_V2_ENDPOINTURL";
    private static final String ENDEPUNKT_URL = getProperty(ENDEPUNKT_URL_KEY);
    private static final String ENDEPUNKT_NAVN = "AKTOER_V2";
    private static final boolean KRITISK = true;

    @Bean
    public AktoerV2 aktoerV2() {
        AktoerV2 prod =  factory().configureStsForExternalSSO().build();
        AktoerV2 mock =  new AktoerMock();
        return createMetricsProxyWithInstanceSwitcher(ENDEPUNKT_NAVN, prod, mock, MOCK_KEY, AktoerV2.class);
    }

    @Bean
    public Pingable aktoerPing() {
        Pingable.Ping.PingMetadata pingMetadata = new Pingable.Ping.PingMetadata(ENDEPUNKT_URL, ENDEPUNKT_NAVN, KRITISK);
        final AktoerV2 aktoerPing = factory()
                .configureStsForSystemUser()
                .build();
        return () -> {
            try {
                aktoerPing.ping();
                return lyktes(pingMetadata);
            } catch (Exception e) {
                return feilet(pingMetadata, e);
            }
        };
    }

    private CXFClient<AktoerV2> factory() {
        return new CXFClient<>(AktoerV2.class)
                .address(ENDEPUNKT_URL);
    }
}