package no.nav.syfo.config;

import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.sbl.dialogarena.types.Pingable;
import no.nav.syfo.mock.DKIFMock;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.DigitalKontaktinformasjonV1;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static java.lang.System.getProperty;
import static no.nav.sbl.dialogarena.common.cxf.InstanceSwitcher.createMetricsProxyWithInstanceSwitcher;
import static no.nav.sbl.dialogarena.types.Pingable.Ping.feilet;
import static no.nav.sbl.dialogarena.types.Pingable.Ping.lyktes;

@Configuration
public class DkifConfig {

    public static final String MOCK_KEY = "dkif.withmock";
    public static final String ENDEPUNKT_URL_KEY = "VIRKSOMHET_DIGITALKONTAKTINFORMASJON_V1_ENDPOINTURL";
    private static final String ENDEPUNKT_URL = getProperty("VIRKSOMHET_DIGITALKONTAKTINFORMASJON_V1_ENDPOINTURL");
    private static final String ENDEPUNKT_NAVN = "DIGITALKONTAKTINFORMASJON_V1";
    private static final boolean KRITISK = true;

    @Bean
    public DigitalKontaktinformasjonV1 digitalKontaktinformasjonV1() {
        DigitalKontaktinformasjonV1 prod = factory().configureStsForSystemUser().build();
        DigitalKontaktinformasjonV1 mock = new DKIFMock();
        return createMetricsProxyWithInstanceSwitcher(ENDEPUNKT_NAVN, prod, mock, MOCK_KEY, DigitalKontaktinformasjonV1.class);
    }

    @Bean
    public Pingable dkifV1Ping() {
        Pingable.Ping.PingMetadata pingMetadata = new Pingable.Ping.PingMetadata(ENDEPUNKT_URL, ENDEPUNKT_NAVN, KRITISK);
        final DigitalKontaktinformasjonV1 dkifPing = factory()
                .configureStsForSystemUser()
                .build();
        return () -> {
            try {
                dkifPing.ping();
                return lyktes(pingMetadata);
            } catch (Exception e) {
                return feilet(pingMetadata, e);
            }
        };
    }

    private CXFClient<DigitalKontaktinformasjonV1> factory() {
        return new CXFClient<>(DigitalKontaktinformasjonV1.class)
                .address(ENDEPUNKT_URL);
    }
}