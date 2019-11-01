package no.nav.syfo.services;

import no.nav.security.oidc.context.OIDCRequestContextHolder;
import no.nav.syfo.LocalApplication;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static no.nav.syfo.testhelper.OidcTestHelper.loggInnBruker;

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
    private NaermesteLederService naermesteLederService;

    private static final String INNLOGGET_FNR = "11990022334";
    private static final String INNLOGGET_AKTOERID = "1234567890123";
    private static final String SPOR_OM_FNR = "12345678902";
    private static final String SPOR_OM_AKTOERID = "1234567890122";

    @Before
    public void setup() {
        loggInnBruker(oidcRequestContextHolder, INNLOGGET_FNR);

        when(aktoerService.hentFnrForAktoer(INNLOGGET_AKTOERID)).thenReturn(INNLOGGET_FNR);
        when(aktoerService.hentAktoerIdForFnr(INNLOGGET_FNR)).thenReturn(INNLOGGET_AKTOERID);

        when(aktoerService.hentFnrForAktoer(SPOR_OM_AKTOERID)).thenReturn(SPOR_OM_FNR);
    }

    @Test
    public void sporOmNoenAndreEnnSegSelvGirFalseNaarManSporOmSegSelv() {
        boolean tilgang = tilgangskontrollService.sporOmNoenAndreEnnSegSelvEllerEgneAnsatte(INNLOGGET_FNR, INNLOGGET_AKTOERID);
        assertThat(tilgang).isFalse();
    }

    @Test
    public void sporOmNoenAndreEnnSegSelvGirFalseNaarManSporOmEnAnsatt() {
        when(naermesteLederService.hentAnsatteAktorId(INNLOGGET_AKTOERID)).thenReturn(asList(
                SPOR_OM_AKTOERID
        ));
        boolean tilgang = tilgangskontrollService.sporOmNoenAndreEnnSegSelvEllerEgneAnsatte(INNLOGGET_FNR, SPOR_OM_AKTOERID);
        assertThat(tilgang).isFalse();
    }

    @Test
    public void sporOmNoenAndreEnnSegSelvGirTrueNaarManSporOmEnSomIkkeErSegSelvOgIkkeAnsatt() {
        when(naermesteLederService.hentAnsatteAktorId(INNLOGGET_AKTOERID)).thenReturn(asList(

        ));
        boolean tilgang = tilgangskontrollService.sporOmNoenAndreEnnSegSelvEllerEgneAnsatte(INNLOGGET_FNR, SPOR_OM_AKTOERID);
        assertThat(tilgang).isTrue();
    }


    @Test
    public void sporOmNoenAndreEnnSegSelvEllerEgneAnsatteEllerLedereGirFalseNaaerManSporOmSegSelv() {
        boolean tilgang = tilgangskontrollService.sporOmNoenAndreEnnSegSelvEllerEgneAnsatteEllerLedere(INNLOGGET_FNR, INNLOGGET_AKTOERID);
        assertThat(tilgang).isFalse();
    }

    @Test
    public void sporOmNoenAndreEnnSegSelvEllerEgneAnsatteEllerLedereGirFalseNaarManSporOmEnAnsatt() {
        when(naermesteLederService.hentAnsatteAktorId(INNLOGGET_AKTOERID)).thenReturn(asList(
                SPOR_OM_AKTOERID
        ));
        boolean tilgang = tilgangskontrollService.sporOmNoenAndreEnnSegSelvEllerEgneAnsatteEllerLedere(INNLOGGET_FNR, SPOR_OM_AKTOERID);
        assertThat(tilgang).isFalse();
    }

    @Test
    public void sporOmNoenAndreEnnSegSelvEllerEgneAnsatteEllerLedereGirFalseNaarManSporOmEnLeder() {
        when(naermesteLederService.hentAnsatteAktorId(INNLOGGET_AKTOERID)).thenReturn(asList(

        ));
        when(naermesteLederService.hentNaermesteLederAktoerIdListe(INNLOGGET_AKTOERID)).thenReturn(asList(
                SPOR_OM_AKTOERID
        ));
        boolean tilgang = tilgangskontrollService.sporOmNoenAndreEnnSegSelvEllerEgneAnsatteEllerLedere(INNLOGGET_FNR, SPOR_OM_AKTOERID);
        assertThat(tilgang).isFalse();
    }

    @Test
    public void sporOmNoenAndreEnnSegSelvEllerEgneAnsatteEllerLedereGirTrueNaarManSporOmEnSomIkkeErSegSelvOgIkkeAnsatt() {
        when(naermesteLederService.hentAnsatteAktorId(INNLOGGET_AKTOERID)).thenReturn(asList(

        ));
        when(naermesteLederService.hentNaermesteLederAktoerIdListe(INNLOGGET_AKTOERID)).thenReturn(asList(

        ));
        boolean tilgang = tilgangskontrollService.sporOmNoenAndreEnnSegSelvEllerEgneAnsatteEllerLedere(INNLOGGET_FNR, SPOR_OM_AKTOERID);
        assertThat(tilgang).isTrue();
    }
}
