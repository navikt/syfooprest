package no.nav.syfo.services;

import no.nav.security.oidc.context.OIDCRequestContextHolder;
import no.nav.syfo.LocalApplication;
import no.nav.syfo.tilgang.BrukerTilgangConsumer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;

import static java.util.Arrays.asList;
import static no.nav.syfo.testhelper.OidcTestHelper.loggInnBruker;
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
    private NaermesteLederService naermesteLederService;

    private static final String INNLOGGET_FNR = "11990022334";
    private static final String INNLOGGET_AKTOERID = "1234567890123";
    private static final String SPOR_OM_FNR = "12345678902";
    private static final String SPOR_OM_AKTOERID = "1234567890122";

    @Before
    public void setup() {
        loggInnBruker(oidcRequestContextHolder, INNLOGGET_FNR);

        when(aktoerService.hentAktoerIdForFnr(INNLOGGET_FNR)).thenReturn(INNLOGGET_AKTOERID);
        when(aktoerService.hentAktoerIdForFnr(SPOR_OM_FNR)).thenReturn(SPOR_OM_AKTOERID);
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
        boolean tilgang = tilgangskontrollService.sporOmNoenAndreEnnSegSelvEllerEgneAnsatteEllerLedere(INNLOGGET_FNR, INNLOGGET_FNR);
        assertThat(tilgang).isFalse();
    }

    @Test
    public void sporOmNoenAndreEnnSegSelvEllerEgneAnsatteEllerLedereGirFalseNaarManSporOmEnAnsatt() {
        when(brukerTilgangConsumer.hasAccessToAnsatt(SPOR_OM_FNR)).thenReturn(true);
        boolean tilgang = tilgangskontrollService.sporOmNoenAndreEnnSegSelvEllerEgneAnsatteEllerLedere(INNLOGGET_FNR, SPOR_OM_FNR);
        assertThat(tilgang).isFalse();
    }

    @Test
    public void sporOmNoenAndreEnnSegSelvEllerEgneAnsatteEllerLedereGirFalseNaarManSporOmEnLeder() {
        when(brukerTilgangConsumer.hasAccessToAnsatt(SPOR_OM_FNR)).thenReturn(false);
        when(naermesteLederService.hentNaermesteLederAktoerIdListe(INNLOGGET_AKTOERID)).thenReturn(asList(
                SPOR_OM_AKTOERID
        ));
        boolean tilgang = tilgangskontrollService.sporOmNoenAndreEnnSegSelvEllerEgneAnsatteEllerLedere(INNLOGGET_FNR, SPOR_OM_FNR);
        assertThat(tilgang).isFalse();
    }

    @Test
    public void sporOmNoenAndreEnnSegSelvEllerEgneAnsatteEllerLedereGirTrueNaarManSporOmEnSomIkkeErSegSelvOgIkkeAnsatt() {
        when(brukerTilgangConsumer.hasAccessToAnsatt(SPOR_OM_FNR)).thenReturn(false);
        when(naermesteLederService.hentNaermesteLederAktoerIdListe(INNLOGGET_AKTOERID)).thenReturn(asList(

        ));
        boolean tilgang = tilgangskontrollService.sporOmNoenAndreEnnSegSelvEllerEgneAnsatteEllerLedere(INNLOGGET_FNR, SPOR_OM_FNR);
        assertThat(tilgang).isTrue();
    }
}
