package no.nav.syfo.rest.ressurser;

import localhost.TestData;
import no.nav.brukerdialog.security.context.SubjectRule;
import no.nav.syfo.services.*;
import no.nav.tjeneste.virksomhet.sykefravaersoppfoelging.v1.SykefravaersoppfoelgingV1;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.ws.rs.core.Response;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class NaermesteLederRessursTest {

    @Mock
    private TilgangskontrollService tilgangskontrollService;
    @Mock
    private AktoerService aktoerService;
    @Mock
    private SykefravaersoppfoelgingV1 sykefravaersoppfoelgingV1;
    @Mock
    private NaermesteLederService naermesteLederService;
    @InjectMocks
    private NaermestelederRessurs naermestelederRessurs;

    @Rule
    public SubjectRule subjectRule = new SubjectRule(TestData.TEST_SUBJECT);

    @Before
    public void setup() {
    }


    @Test
    public void returnerer404ResponseVedIngeLedere() {
        when(tilgangskontrollService.sporOmNoenAndreEnnSegSelvEllerEgneAnsatte(anyString())).thenReturn(false);
        when(aktoerService.hentAktoerIdForFnr(anyString())).thenReturn("1234567890123");
        when(naermesteLederService.hentForrigeNaermesteLeder(any(), any())).thenReturn(Optional.empty());

        Response response = naermestelederRessurs.hentForrigeNaermesteLeder("12345678901", "123456789");
        assertThat(response.getStatus()).isEqualTo(404);
    }

}