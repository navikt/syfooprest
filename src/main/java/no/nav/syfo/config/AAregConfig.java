package no.nav.syfo.config;

import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.sbl.dialogarena.types.Pingable;
import no.nav.syfo.mock.ArbeidsforholdMock;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.binding.ArbeidsforholdV3;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static java.lang.System.getProperty;
import static no.nav.sbl.dialogarena.common.cxf.InstanceSwitcher.createMetricsProxyWithInstanceSwitcher;
import static no.nav.sbl.dialogarena.types.Pingable.Ping.feilet;
import static no.nav.sbl.dialogarena.types.Pingable.Ping.lyktes;

@Configuration
public class AAregConfig {
    public static final String ENDEPUNKT_URL_KEY = "VIRKSOMHET_ARBEIDSFORHOLD_V3_ENDPOINTURL";
    private static final String ENDEPUNKT_URL = getProperty(ENDEPUNKT_URL_KEY);
    public static final String MOCK_KEY = getProperty("arbeidsforhold.aareg.withmock");
    private static final String ENDEPUNKT_NAVN = "ARBEIDSFORHOLD_V3";
    private static final boolean KRITISK = false;

    @Bean
    public ArbeidsforholdV3 arbeidsforholdV3() {
        ArbeidsforholdV3 prod = factory().configureStsForSystemUser().build();
        ArbeidsforholdV3 mock = new ArbeidsforholdMock();
        return createMetricsProxyWithInstanceSwitcher(ENDEPUNKT_NAVN, prod, mock, MOCK_KEY, ArbeidsforholdV3.class);
    }

    @Bean
    public Pingable arbeidsforholdPing() {
        final ArbeidsforholdV3 arbeidsforholdPing = factory()
                .configureStsForSystemUser()
                .build();
        return () -> {
            try {
                arbeidsforholdPing.ping();
                return lyktes(new Pingable.Ping.PingMetadata(ENDEPUNKT_URL, ENDEPUNKT_NAVN, KRITISK));
            } catch (Exception e) {
                // TODO: Dette kan fjernes når Arbeidsforhold implementerer sin Ping uten avhengigheter bakover
                if (e.getMessage().contains("Organisasjon")) {
                    return lyktes(new Pingable.Ping.PingMetadata(ENDEPUNKT_URL, ENDEPUNKT_NAVN + " - FEIL:" + e.getMessage(), KRITISK));
                }
                return feilet(new Pingable.Ping.PingMetadata(ENDEPUNKT_URL, ENDEPUNKT_NAVN, KRITISK), e);
            }
        };
    }

    private CXFClient<ArbeidsforholdV3> factory() {
        return new CXFClient<>(ArbeidsforholdV3.class)
                .address(ENDEPUNKT_URL);
    }
}