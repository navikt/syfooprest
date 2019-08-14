package no.nav.syfo.config;

import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.sbl.dialogarena.types.Pingable;
import no.nav.syfo.mock.BrukerprofilMock;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.BrukerprofilV3;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static java.lang.System.getProperty;
import static no.nav.sbl.dialogarena.common.cxf.InstanceSwitcher.createMetricsProxyWithInstanceSwitcher;
import static no.nav.sbl.dialogarena.types.Pingable.Ping.feilet;
import static no.nav.sbl.dialogarena.types.Pingable.Ping.lyktes;

@Configuration
public class TpsConfig {

    public static final String MOCK_KEY = "brukerprofilv3.withmock";
    public static final String ENDEPUNKT_URL_KEY = "VIRKSOMHET_BRUKERPROFIL_V3_ENDPOINTURL";
    private static final String ENDEPUNKT_URL = getProperty(ENDEPUNKT_URL_KEY);
    private static final String ENDEPUNKT_NAVN = "BRUKERPROFIL_V3";
    private static final boolean KRITISK = true;

    @Bean
    public BrukerprofilV3 brukerprofilV3() {
        BrukerprofilV3 prod = factory()
                .configureStsForSystemUser()
                .build();
        BrukerprofilV3 mock = new BrukerprofilMock();

        return createMetricsProxyWithInstanceSwitcher(ENDEPUNKT_NAVN, prod, mock, MOCK_KEY, BrukerprofilV3.class);
    }

    @Bean
    public Pingable tpsPing() {
        Pingable.Ping.PingMetadata pingMetadata = new Pingable.Ping.PingMetadata(ENDEPUNKT_URL, ENDEPUNKT_NAVN, KRITISK);
        final BrukerprofilV3 brukerprofilV3 = factory()
                .configureStsForSystemUser()
                .build();
        return () -> {
            try {
                brukerprofilV3.ping();
                return lyktes(pingMetadata);
            } catch (Exception e) {
                return feilet(pingMetadata, e);
            }
        };
    }

    private CXFClient<BrukerprofilV3> factory() {
        return new CXFClient<>(BrukerprofilV3.class).address(ENDEPUNKT_URL);
    }
}