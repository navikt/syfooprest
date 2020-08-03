package no.nav.syfo.narmesteleder

import io.mockk.*
import no.nav.security.oidc.context.OIDCRequestContextHolder
import no.nav.syfo.LocalApplication
import no.nav.syfo.consumer.aktorregister.AktorregisterConsumer
import no.nav.syfo.metric.Metric
import no.nav.syfo.narmesteleder.consumer.*
import no.nav.syfo.narmesteleder.controller.NaermestelederController
import no.nav.syfo.testhelper.OidcTestHelper.getValidationContext
import no.nav.syfo.testhelper.UserConstants
import no.nav.syfo.tilgang.TilgangskontrollService
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit4.SpringRunner
import java.time.LocalDate

@RunWith(SpringRunner::class)
@SpringBootTest(classes = [LocalApplication::class])
@DirtiesContext
class NaermesteLederRessursTest {
    private val aktorregisterConsumer: AktorregisterConsumer = mockk()

    private val oidcRequestContextHolder: OIDCRequestContextHolder = mockk()

    private val tilgangskontrollService: TilgangskontrollService = mockk()

    private val narmesteLederConsumer: NarmesteLederConsumer = mockk()

    private val metric: Metric = mockk()

    private val naermestelederRessurs = NaermestelederController(
        metric,
        oidcRequestContextHolder,
        tilgangskontrollService,
        aktorregisterConsumer,
        narmesteLederConsumer
    )

    @Test
    fun hentNaermesteLederSuccess() {
        every { oidcRequestContextHolder.oidcValidationContext }.returns(
            getValidationContext(UserConstants.LEDER_FNR)
        )
        every { aktorregisterConsumer.hentFnrForAktor(UserConstants.LEDER_AKTORID) }.returns(UserConstants.LEDER_FNR)
        every { metric.countEndpointRequest(any()) } just Runs
        every { tilgangskontrollService.sporOmNoenAndreEnnSegSelvEllerEgneAnsatte(UserConstants.LEDER_FNR, UserConstants.ARBEIDSTAKER_FNR) }.returns(false)
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
        every { narmesteLederConsumer.narmesteLeder(any(), any()) }.returns(leder)
        val rsNaermesteLeder = naermestelederRessurs.hentNaermesteLeder(UserConstants.ARBEIDSTAKER_FNR, UserConstants.VIRKSOMHETSNUMMER)
        Assert.assertEquals(UserConstants.LEDER_FNR, rsNaermesteLeder.fnr)
    }
}
