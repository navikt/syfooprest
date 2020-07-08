package no.nav.syfo.narmesteleder

import no.nav.security.oidc.context.OIDCRequestContextHolder
import no.nav.syfo.LocalApplication
import no.nav.syfo.narmesteleder.consumer.NaermesteLederStatus
import no.nav.syfo.narmesteleder.consumer.Naermesteleder
import no.nav.syfo.narmesteleder.consumer.NarmesteLederConsumer
import no.nav.syfo.narmesteleder.controller.NaermestelederController
import no.nav.syfo.consumer.aktorregister.AktorregisterConsumer
import no.nav.syfo.testhelper.OidcTestHelper
import no.nav.syfo.testhelper.UserConstants
import no.nav.syfo.tilgang.TilgangskontrollService
import org.junit.After
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit4.SpringRunner
import java.time.LocalDate
import javax.inject.Inject

@RunWith(SpringRunner::class)
@SpringBootTest(classes = [LocalApplication::class])
@DirtiesContext
class NaermesteLederRessursTest {
    @Inject
    private lateinit var naermestelederRessurs: NaermestelederController
    @Inject
    private lateinit var oidcRequestContextHolder: OIDCRequestContextHolder
    @Inject
    private lateinit var aktorregisterConsumer: AktorregisterConsumer
    @MockBean
    private lateinit var tilgangskontrollService: TilgangskontrollService
    @MockBean
    private lateinit var narmesteLederConsumer: NarmesteLederConsumer

    @After
    fun tearDown() {
        OidcTestHelper.loggUtAlle(oidcRequestContextHolder)
    }

    @Test
    fun hentNaermesteLederSuccess() {
        OidcTestHelper.loggInnBruker(oidcRequestContextHolder, UserConstants.LEDER_FNR)
        Mockito.`when`(tilgangskontrollService.sporOmNoenAndreEnnSegSelvEllerEgneAnsatte(UserConstants.LEDER_FNR, UserConstants.ARBEIDSTAKER_FNR)).thenReturn(false)
        val leder = Naermesteleder(
                naermesteLederId = 0L,
                naermesteLederAktoerId = UserConstants.LEDER_AKTORID,
                naermesteLederStatus = NaermesteLederStatus(
                        erAktiv = true,
                        aktivFom = LocalDate.now().minusDays(1),
                        aktivTom = null
                ),
                orgnummer = UserConstants.VIRKSOMHETSNUMMER,
                navn = "",
                epost = null,
                mobil = null
        )
        Mockito.`when`(narmesteLederConsumer.narmesteLeder(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(leder)
        val rsNaermesteLeder = naermestelederRessurs.hentNaermesteLeder(UserConstants.ARBEIDSTAKER_FNR, UserConstants.VIRKSOMHETSNUMMER)
        Assert.assertEquals(UserConstants.LEDER_FNR, rsNaermesteLeder.fnr)
    }
}
