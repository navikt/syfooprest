package no.nav.syfo.narmesteleder

import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import no.nav.security.token.support.core.context.TokenValidationContextHolder
import no.nav.syfo.metric.Metric
import no.nav.syfo.narmesteleder.consumer.NaermesteLederStatus
import no.nav.syfo.narmesteleder.consumer.Naermesteleder
import no.nav.syfo.narmesteleder.consumer.NarmesteLederConsumer
import no.nav.syfo.narmesteleder.controller.NaermesteLederController
import no.nav.syfo.testhelper.OidcTestHelper.getValidationContext
import no.nav.syfo.testhelper.UserConstants
import no.nav.syfo.tilgang.TilgangskontrollService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDate

@ExtendWith(SpringExtension::class)
class NaermesteLederControllerTest {
    private val oidcRequestContextHolder: TokenValidationContextHolder = mockk()

    private val tilgangskontrollService: TilgangskontrollService = mockk()

    private val narmesteLederConsumer: NarmesteLederConsumer = mockk()

    private val metric: Metric = mockk()

    private val naermestelederController = NaermesteLederController(
            metric,
            oidcRequestContextHolder,
            tilgangskontrollService,
            narmesteLederConsumer
    )

    @Test
    fun hentNaermesteLederSuccess() {
        every { oidcRequestContextHolder.tokenValidationContext }.returns(
                getValidationContext(UserConstants.LEDER_FNR)
        )
        every { metric.countEndpointRequest(any()) } just Runs
        every { tilgangskontrollService.sporOmNoenAndreEnnSegSelvEllerEgneAnsatte(UserConstants.LEDER_FNR, UserConstants.ARBEIDSTAKER_FNR) }.returns(false)

        val leder = Naermesteleder(
                naermesteLederId = 0L,
                naermesteLederFnr = UserConstants.LEDER_FNR,
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

        val rsNaermesteLeder = naermestelederController.hentNaermesteLeder(UserConstants.ARBEIDSTAKER_FNR, UserConstants.VIRKSOMHETSNUMMER)

        assertEquals(UserConstants.LEDER_FNR, rsNaermesteLeder.fnr)
    }
}
