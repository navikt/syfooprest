package no.nav.syfo.rest.ressurser;

import no.nav.security.oidc.context.OIDCRequestContextHolder;
import no.nav.syfo.LocalApplication;
import no.nav.syfo.services.*;
import no.nav.tjeneste.virksomhet.sykefravaersoppfoelging.v1.SykefravaersoppfoelgingV1;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static no.nav.syfo.testhelper.OidcTestHelper.loggInnBruker;
import static no.nav.syfo.testhelper.UserConstants.ARBEIDSTAKER_FNR;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = LocalApplication.class)
@DirtiesContext
public class NaermesteLederRessursTest {

    @Inject
    private NaermestelederRessurs naermestelederRessurs;

    @Inject
    public OIDCRequestContextHolder oidcRequestContextHolder;

    @MockBean
    private TilgangskontrollService tilgangskontrollService;
    @MockBean
    private AktoerService aktoerService;
    @MockBean
    private SykefravaersoppfoelgingV1 sykefravaersoppfoelgingV1;
    @MockBean
    private NaermesteLederService naermesteLederService;

    @Before
    public void setup() {
        loggInnBruker(oidcRequestContextHolder, ARBEIDSTAKER_FNR);
    }

    @Test
    public void returnerer404ResponseVedIngeLedere() {
        when(tilgangskontrollService.sporOmNoenAndreEnnSegSelvEllerEgneAnsatte(anyString(), anyString())).thenReturn(false);
        when(aktoerService.hentAktoerIdForFnr(anyString())).thenReturn("1234567890123");
        when(naermesteLederService.hentForrigeNaermesteLeder(any(), any())).thenReturn(Optional.empty());

        Response response = naermestelederRessurs.hentForrigeNaermesteLeder("12345678901", "123456789");
        assertThat(response.getStatus()).isEqualTo(404);
    }
}
