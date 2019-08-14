package no.nav.syfo.config;

import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.sbl.dialogarena.types.Pingable;
import no.nav.syfo.mock.OrganisasjonMock;
import no.nav.tjeneste.virksomhet.organisasjon.v4.OrganisasjonV4;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static java.lang.System.getProperty;
import static no.nav.sbl.dialogarena.common.cxf.InstanceSwitcher.createMetricsProxyWithInstanceSwitcher;
import static no.nav.sbl.dialogarena.types.Pingable.Ping.feilet;
import static no.nav.sbl.dialogarena.types.Pingable.Ping.lyktes;

@Configuration
public class EregConfig {

    public static final String MOCK_KEY = "ereg.withmock";
    public static final String ENDEPUNKT_URL_KEY = "VIRKSOMHET_ORGANISASJON_V4_ENDPOINTURL";
    private static final String ENDEPUNKT_URL = getProperty("VIRKSOMHET_ORGANISASJON_V4_ENDPOINTURL");
    private static final String ENDEPUNKT_NAVN = "ORGANISASJON_V4";
    private static final boolean KRITISK = false;

    @Bean
    public OrganisasjonV4 organisasjonV4() {
        OrganisasjonV4 prod = factory().configureStsForExternalSSO().build();
        OrganisasjonV4 mock = new OrganisasjonMock();

        return createMetricsProxyWithInstanceSwitcher(ENDEPUNKT_NAVN, prod, mock, MOCK_KEY, OrganisasjonV4.class);
    }

    @Bean
    public Pingable organisasjonPing() {
        Pingable.Ping.PingMetadata pingMetadata = new Pingable.Ping.PingMetadata(ENDEPUNKT_URL, ENDEPUNKT_NAVN, KRITISK);

        final OrganisasjonV4 organisasjonPing = factory()
                .configureStsForSystemUser()
                .build();
        return () -> {
            try {
                organisasjonPing.ping();
                return lyktes(pingMetadata);
            } catch (Exception e) {
                return feilet(pingMetadata, e);
            }
        };
    }

    private CXFClient<OrganisasjonV4> factory() {
        return new CXFClient<>(OrganisasjonV4.class).address(ENDEPUNKT_URL);
    }

}