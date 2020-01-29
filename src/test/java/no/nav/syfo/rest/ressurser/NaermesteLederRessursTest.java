package no.nav.syfo.rest.ressurser;

import no.nav.security.oidc.context.OIDCRequestContextHolder;
import no.nav.syfo.LocalApplication;
import no.nav.syfo.model.NaermesteLederStatus;
import no.nav.syfo.narmesteleder.Naermesteleder;
import no.nav.syfo.narmesteleder.NarmesteLederConsumer;
import no.nav.syfo.rest.domain.RSNaermesteLeder;
import no.nav.syfo.services.*;
import no.nav.tjeneste.virksomhet.sykefravaersoppfoelging.v1.SykefravaersoppfoelgingV1;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;
import java.util.Optional;

import static no.nav.syfo.testhelper.OidcTestHelper.loggInnBruker;
import static no.nav.syfo.testhelper.OidcTestHelper.loggUtAlle;
import static no.nav.syfo.testhelper.UserConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = LocalApplication.class)
@DirtiesContext
public class NaermesteLederRessursTest {

    @Inject
    private NaermestelederRessurs naermestelederRessurs;

    @Inject
    public OIDCRequestContextHolder oidcRequestContextHolder;
    @Inject
    private AktoerService aktoerService;

    @MockBean
    private TilgangskontrollService tilgangskontrollService;
    @MockBean
    private SykefravaersoppfoelgingV1 sykefravaersoppfoelgingV1;
    @MockBean
    private NarmesteLederConsumer narmesteLederConsumer;
    @MockBean
    private NaermesteLederService naermesteLederService;

    @After
    public void tearDown() {
        loggUtAlle(oidcRequestContextHolder);
    }

    @Test
    public void hentNaermesteLederSuccess() {
        loggInnBruker(oidcRequestContextHolder, LEDER_FNR);

        when(tilgangskontrollService.sporOmNoenAndreEnnSegSelvEllerEgneAnsatte(LEDER_FNR, ARBEIDSTAKER_FNR)).thenReturn(false);

        Naermesteleder leder = new Naermesteleder()
                .naermesteLederAktoerId(LEDER_AKTORID)
                .orgnummer(VIRKSOMHETSNUMMER)
                .naermesteLederStatus(new NaermesteLederStatus()
                        .erAktiv(true));
        when(narmesteLederConsumer.narmesteLeder(anyString(), anyString())).thenReturn(Optional.of(leder));

        RSNaermesteLeder rsNaermesteLeder = naermestelederRessurs.hentNaermesteLeder(ARBEIDSTAKER_FNR, VIRKSOMHETSNUMMER);

        assertEquals(LEDER_FNR, rsNaermesteLeder.fnr);
    }

    @Test
    public void returnerer404ResponseVedIngeLedere() {
        loggInnBruker(oidcRequestContextHolder, ARBEIDSTAKER_FNR);

        when(tilgangskontrollService.sporOmNoenAndreEnnSegSelvEllerEgneAnsatte(anyString(), anyString())).thenReturn(false);
        when(naermesteLederService.hentForrigeNaermesteLeder(any(), any())).thenReturn(Optional.empty());

        ResponseEntity responseEntity = naermestelederRessurs.hentForrigeNaermesteLeder(ARBEIDSTAKER_FNR, VIRKSOMHETSNUMMER);
        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(404);
    }
}
