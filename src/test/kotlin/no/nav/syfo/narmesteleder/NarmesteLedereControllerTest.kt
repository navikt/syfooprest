package no.nav.syfo.narmesteleder

import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import no.nav.security.token.support.core.context.TokenValidationContextHolder
import no.nav.syfo.metric.Metric
import no.nav.syfo.narmesteleder.consumer.NaermesteLederStatus
import no.nav.syfo.narmesteleder.consumer.Naermesteleder
import no.nav.syfo.narmesteleder.consumer.NarmesteLedereConsumer
import no.nav.syfo.narmesteleder.controller.NarmesteLederMapper
import no.nav.syfo.narmesteleder.controller.NarmesteLedereController
import no.nav.syfo.narmesteleder.controller.RSNaermesteLeder
import no.nav.syfo.testhelper.OidcTestHelper.getValidationContext
import no.nav.syfo.testhelper.UserConstants
import no.nav.syfo.testhelper.UserConstants.LEDER_FNR
import no.nav.syfo.tilgang.TilgangskontrollService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDate
import javax.ws.rs.ForbiddenException
import javax.ws.rs.NotFoundException

@ExtendWith(SpringExtension::class)
class NarmesteLedereControllerTest {
    private val narmesteLederMapper: NarmesteLederMapper = mockk()

    private val oidcRequestContextHolder: TokenValidationContextHolder = mockk()

    private val tilgangskontrollService: TilgangskontrollService = mockk()

    private val narmesteLedereConsumer: NarmesteLedereConsumer = mockk()

    private val metric: Metric = mockk()

    private val narmesteledereController = NarmesteLedereController(
        metric,
        oidcRequestContextHolder,
        tilgangskontrollService,
        narmesteLederMapper,
        narmesteLedereConsumer
    )

    @Test
    fun hentNaermesteLedere_success() {
        every { oidcRequestContextHolder.tokenValidationContext }.returns(
            getValidationContext(LEDER_FNR)
        )
        every { metric.countEndpointRequest(any()) } just Runs
        every { tilgangskontrollService.sporOmNoenAndreEnnSegSelvEllerEgneAnsatte(LEDER_FNR, UserConstants.ARBEIDSTAKER_FNR) }.returns(false)

        val narmesteLedere = listOf<Naermesteleder>(Naermesteleder(
            naermesteLederId = 0L,
            naermesteLederFnr = LEDER_FNR,
            naermesteLederStatus = NaermesteLederStatus(
                erAktiv = true,
                aktivFom = LocalDate.now().minusDays(1),
                aktivTom = null
            ),
            orgnummer = UserConstants.VIRKSOMHETSNUMMER,
            navn = "",
            epost = null,
            mobil = null
        ))

        val narmesteLeder = narmesteLedere.get(0)

        val rsLeder = RSNaermesteLeder(
            virksomhetsnummer = narmesteLeder.orgnummer,
            navn = narmesteLeder.navn,
            epost = narmesteLeder.epost,
            tlf = narmesteLeder.mobil,
            erAktiv = narmesteLeder.naermesteLederStatus.erAktiv,
            aktivFom = narmesteLeder.naermesteLederStatus.aktivFom,
            aktivTom = narmesteLeder.naermesteLederStatus.aktivTom,
            fnr = LEDER_FNR,
            samtykke = null,
            sistInnlogget = null
        )

        every { narmesteLedereConsumer.narmesteLedere(any()) }.returns(narmesteLedere)
        every { narmesteLederMapper.map(any()) }.returns(rsLeder)

        val rsNaermesteLedere = narmesteledereController.hentNermesteLedere(UserConstants.ARBEIDSTAKER_FNR)
        assertEquals(LEDER_FNR, rsNaermesteLedere.get(0).fnr)
    }

    @Test()
    fun hentNaermesteLedere_nullResponse_NotFoundExceptionThrown() {
        every { oidcRequestContextHolder.tokenValidationContext }.returns(
            getValidationContext(LEDER_FNR)
        )
        every { metric.countEndpointRequest(any()) } just Runs
        every { tilgangskontrollService.sporOmNoenAndreEnnSegSelvEllerEgneAnsatte(LEDER_FNR, UserConstants.ARBEIDSTAKER_FNR) }.returns(false)
        every { narmesteLedereConsumer.narmesteLedere(any()) }.returns(null)

        assertThrows(NotFoundException::class.java) {
            narmesteledereController.hentNermesteLedere(UserConstants.ARBEIDSTAKER_FNR)
        }
    }

    @Test()
    fun hentNaermesteLedere_harIkkeTilgang_ForbiddenExceptionThrown() {
        every { oidcRequestContextHolder.tokenValidationContext }.returns(
            getValidationContext(LEDER_FNR)
        )
        every { metric.countEndpointRequest(any()) } just Runs
        every { tilgangskontrollService.sporOmNoenAndreEnnSegSelvEllerEgneAnsatte(LEDER_FNR, UserConstants.ARBEIDSTAKER_FNR) }.returns(true)

        assertThrows(ForbiddenException::class.java) {
            narmesteledereController.hentNermesteLedere(UserConstants.ARBEIDSTAKER_FNR)
        }
    }
}
