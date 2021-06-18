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
import no.nav.syfo.narmesteleder.controller.NarmesteLederMapper
import no.nav.syfo.narmesteleder.controller.RSNaermesteLeder
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
    private val narmesteLederMapper: NarmesteLederMapper = mockk()

    private val oidcRequestContextHolder: TokenValidationContextHolder = mockk()

    private val tilgangskontrollService: TilgangskontrollService = mockk()

    private val narmesteLederConsumer: NarmesteLederConsumer = mockk()

    private val metric: Metric = mockk()

    private val naermestelederController = NaermesteLederController(
            metric,
            oidcRequestContextHolder,
            tilgangskontrollService,
            narmesteLederMapper,
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

        val rsLeder = RSNaermesteLeder(
                virksomhetsnummer = leder.orgnummer,
                navn = leder.navn,
                epost = leder.epost,
                tlf = leder.mobil,
                erAktiv = leder.naermesteLederStatus.erAktiv,
                aktivFom = leder.naermesteLederStatus.aktivFom,
                aktivTom = leder.naermesteLederStatus.aktivTom,
                fnr = UserConstants.LEDER_FNR,
                samtykke = null,
                sistInnlogget = null
        )

        every { narmesteLederConsumer.narmesteLeder(any(), any()) }.returns(leder)
        every { narmesteLederMapper.map(any()) }.returns(rsLeder)

        val rsNaermesteLeder = naermestelederController.hentNaermesteLeder(UserConstants.ARBEIDSTAKER_FNR, UserConstants.VIRKSOMHETSNUMMER)

        assertEquals(UserConstants.LEDER_FNR, rsNaermesteLeder.fnr)
    }
}
