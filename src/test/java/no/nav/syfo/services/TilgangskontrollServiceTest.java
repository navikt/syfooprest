package no.nav.syfo.services;

import localhost.TestData;
import no.nav.brukerdialog.security.context.SubjectRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static java.lang.System.setProperty;
import static java.util.Arrays.asList;
//import static no.nav.modig.core.context.SubjectHandlerUtils.setEksternBruker;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class TilgangskontrollServiceTest {

    @Mock
    private AktoerService aktoerService;
    @Mock
    private NaermesteLederService naermesteLederService;
    @InjectMocks
    private TilgangskontrollService tilgangskontrollService;

    private static final String INNLOGGET_FNR = "11990022334";
    private static final String INNLOGGET_AKTOERID = "1234567890123";
    private static final String SPOR_OM_FNR = "12345678902";
    private static final String SPOR_OM_AKTOERID = "1234567890122";

    @Rule
    public SubjectRule subjectRule = new SubjectRule(TestData.TEST_SUBJECT);

    @Before
    public void setup() {
        when(aktoerService.hentFnrForAktoer(INNLOGGET_AKTOERID)).thenReturn(INNLOGGET_FNR);
        when(aktoerService.hentAktoerIdForFnr(INNLOGGET_FNR)).thenReturn(INNLOGGET_AKTOERID);

        when(aktoerService.hentFnrForAktoer(SPOR_OM_AKTOERID)).thenReturn(SPOR_OM_FNR);
    }

    @Test
    public void sporOmNoenAndreEnnSegSelvGirFalseNaarManSporOmSegSelv() {
        boolean tilgang = tilgangskontrollService.sporOmNoenAndreEnnSegSelvEllerEgneAnsatte(INNLOGGET_AKTOERID);
        assertThat(tilgang).isFalse();
    }

    @Test
    public void sporOmNoenAndreEnnSegSelvGirFalseNaarManSporOmEnAnsatt() {
        when(naermesteLederService.hentAnsatteAktorId(INNLOGGET_AKTOERID)).thenReturn(asList(
                SPOR_OM_AKTOERID
        ));
        boolean tilgang = tilgangskontrollService.sporOmNoenAndreEnnSegSelvEllerEgneAnsatte(SPOR_OM_AKTOERID);
        assertThat(tilgang).isFalse();
    }

    @Test
    public void sporOmNoenAndreEnnSegSelvGirTrueNaarManSporOmEnSomIkkeErSegSelvOgIkkeAnsatt() {
        when(naermesteLederService.hentAnsatteAktorId(INNLOGGET_AKTOERID)).thenReturn(asList(

        ));
        boolean tilgang = tilgangskontrollService.sporOmNoenAndreEnnSegSelvEllerEgneAnsatte(SPOR_OM_AKTOERID);
        assertThat(tilgang).isTrue();
    }


    @Test
    public void sporOmNoenAndreEnnSegSelvEllerEgneAnsatteEllerLedereGirFalseNaaerManSporOmSegSelv() {
        boolean tilgang = tilgangskontrollService.sporOmNoenAndreEnnSegSelvEllerEgneAnsatteEllerLedere(INNLOGGET_AKTOERID);
        assertThat(tilgang).isFalse();
    }

    @Test
    public void sporOmNoenAndreEnnSegSelvEllerEgneAnsatteEllerLedereGirFalseNaarManSporOmEnAnsatt() {
        when(naermesteLederService.hentAnsatteAktorId(INNLOGGET_AKTOERID)).thenReturn(asList(
                SPOR_OM_AKTOERID
        ));
        boolean tilgang = tilgangskontrollService.sporOmNoenAndreEnnSegSelvEllerEgneAnsatteEllerLedere(SPOR_OM_AKTOERID);
        assertThat(tilgang).isFalse();
    }

    @Test
    public void sporOmNoenAndreEnnSegSelvEllerEgneAnsatteEllerLedereGirFalseNaarManSporOmEnLeder() {
        when(naermesteLederService.hentAnsatteAktorId(INNLOGGET_AKTOERID)).thenReturn(asList(

        ));
        when(naermesteLederService.hentNaermesteLederAktoerIdListe(INNLOGGET_AKTOERID)).thenReturn(asList(
                SPOR_OM_AKTOERID
        ));
        boolean tilgang = tilgangskontrollService.sporOmNoenAndreEnnSegSelvEllerEgneAnsatteEllerLedere(SPOR_OM_AKTOERID);
        assertThat(tilgang).isFalse();
    }

    @Test
    public void sporOmNoenAndreEnnSegSelvEllerEgneAnsatteEllerLedereGirTrueNaarManSporOmEnSomIkkeErSegSelvOgIkkeAnsatt() {
        when(naermesteLederService.hentAnsatteAktorId(INNLOGGET_AKTOERID)).thenReturn(asList(

        ));
        when(naermesteLederService.hentNaermesteLederAktoerIdListe(INNLOGGET_AKTOERID)).thenReturn(asList(

        ));
        boolean tilgang = tilgangskontrollService.sporOmNoenAndreEnnSegSelvEllerEgneAnsatteEllerLedere(SPOR_OM_AKTOERID);
        assertThat(tilgang).isTrue();
    }
}