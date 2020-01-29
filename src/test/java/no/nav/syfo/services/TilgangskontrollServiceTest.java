package no.nav.syfo.services;

import no.nav.security.oidc.context.OIDCRequestContextHolder;
import no.nav.syfo.LocalApplication;
import no.nav.syfo.model.NaermesteLederStatus;
import no.nav.syfo.narmesteleder.Naermesteleder;
import no.nav.syfo.narmesteleder.NarmesteLederConsumer;
import no.nav.syfo.tilgang.BrukerTilgangConsumer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;
import java.util.Optional;

import static no.nav.syfo.testhelper.OidcTestHelper.loggInnBruker;
import static no.nav.syfo.testhelper.UserConstants.VIRKSOMHETSNUMMER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = LocalApplication.class)
@DirtiesContext
public class TilgangskontrollServiceTest {

    @Inject
    private TilgangskontrollService tilgangskontrollService;

    @Inject
    public OIDCRequestContextHolder oidcRequestContextHolder;

    @MockBean
    private AktoerService aktoerService;
    @MockBean
    private BrukerTilgangConsumer brukerTilgangConsumer;
    @MockBean
    private NarmesteLederConsumer narmesteLederConsumer;

    private static final String INNLOGGET_FNR = "11990022334";
    private static final String INNLOGGET_AKTOERID = "1234567890123";
    private static final String SPOR_OM_FNR = "12345678902";
    private static final String SPOR_OM_AKTOERID = "1234567890122";

    @Before
    public void setup() {
        loggInnBruker(oidcRequestContextHolder, INNLOGGET_FNR);

        when(aktoerService.hentFnrForAktoer(INNLOGGET_AKTOERID)).thenReturn(INNLOGGET_FNR);
        when(aktoerService.hentFnrForAktoer(SPOR_OM_AKTOERID)).thenReturn(SPOR_OM_FNR);
    }

    @Test
    public void sporOmNoenAndreEnnSegSelvGirFalseNaarManSporOmSegSelv() {
        boolean tilgang = tilgangskontrollService.sporOmNoenAndreEnnSegSelvEllerEgneAnsatte(INNLOGGET_FNR, INNLOGGET_FNR);
        assertThat(tilgang).isFalse();
    }

    @Test
    public void sporOmNoenAndreEnnSegSelvGirFalseNaarManSporOmEnAnsatt() {
        when(brukerTilgangConsumer.hasAccessToAnsatt(SPOR_OM_FNR)).thenReturn(true);
        boolean tilgang = tilgangskontrollService.sporOmNoenAndreEnnSegSelvEllerEgneAnsatte(INNLOGGET_FNR, SPOR_OM_FNR);
        assertThat(tilgang).isFalse();
    }

    @Test
    public void sporOmNoenAndreEnnSegSelvGirTrueNaarManSporOmEnSomIkkeErSegSelvOgIkkeAnsatt() {
        when(brukerTilgangConsumer.hasAccessToAnsatt(SPOR_OM_FNR)).thenReturn(false);
        boolean tilgang = tilgangskontrollService.sporOmNoenAndreEnnSegSelvEllerEgneAnsatte(INNLOGGET_FNR, SPOR_OM_FNR);
        assertThat(tilgang).isTrue();
    }

    @Test
    public void sporOmNoenAndreEnnSegSelvEllerEgneAnsatteEllerLedereGirFalseNaaerManSporOmSegSelv() {
        boolean tilgang = tilgangskontrollService.sporOmNoenAndreEnnSegSelvEllerEgneAnsatteEllerLedere(INNLOGGET_FNR, INNLOGGET_FNR, VIRKSOMHETSNUMMER);
        assertThat(tilgang).isFalse();
    }

    @Test
    public void sporOmNoenAndreEnnSegSelvEllerEgneAnsatteEllerLedereGirFalseNaarManSporOmEnAnsatt() {
        when(brukerTilgangConsumer.hasAccessToAnsatt(SPOR_OM_FNR)).thenReturn(true);
        boolean tilgang = tilgangskontrollService.sporOmNoenAndreEnnSegSelvEllerEgneAnsatteEllerLedere(INNLOGGET_FNR, SPOR_OM_FNR, VIRKSOMHETSNUMMER);
        assertThat(tilgang).isFalse();
    }

    @Test
    public void sporOmNoenAndreEnnSegSelvEllerEgneAnsatteEllerLedereGirFalseNaarManSporOmEnLeder() {
        Optional<Naermesteleder> leder = Optional.of(new Naermesteleder()
                .naermesteLederAktoerId(SPOR_OM_AKTOERID)
                .orgnummer(VIRKSOMHETSNUMMER)
                .naermesteLederStatus(new NaermesteLederStatus()
                        .erAktiv(true)));

        when(brukerTilgangConsumer.hasAccessToAnsatt(SPOR_OM_FNR)).thenReturn(false);
        when(narmesteLederConsumer.narmesteLeder(INNLOGGET_FNR, VIRKSOMHETSNUMMER)).thenReturn(leder);
        boolean tilgang = tilgangskontrollService.sporOmNoenAndreEnnSegSelvEllerEgneAnsatteEllerLedere(INNLOGGET_FNR, SPOR_OM_FNR, VIRKSOMHETSNUMMER);
        assertThat(tilgang).isFalse();
    }


    @Test
    public void sporOmNoenAndreEnnSegSelvEllerEgneAnsatteEllerLedereGirTrueNaarManSporOmEnSomIkkeErSegSelvOgIkkeAnsatt() {
        Optional<Naermesteleder> leder = Optional.of(new Naermesteleder()
                .naermesteLederAktoerId(INNLOGGET_AKTOERID));

        when(brukerTilgangConsumer.hasAccessToAnsatt(SPOR_OM_FNR)).thenReturn(false);
        when(narmesteLederConsumer.narmesteLeder(INNLOGGET_FNR, VIRKSOMHETSNUMMER)).thenReturn(leder);
        boolean tilgang = tilgangskontrollService.sporOmNoenAndreEnnSegSelvEllerEgneAnsatte(INNLOGGET_FNR, SPOR_OM_FNR);
        assertThat(tilgang).isTrue();
    }
}
