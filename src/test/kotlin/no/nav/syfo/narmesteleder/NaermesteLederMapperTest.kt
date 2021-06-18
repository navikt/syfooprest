package no.nav.syfo.narmesteleder

import io.mockk.every
import io.mockk.mockk
import no.nav.syfo.consumer.aktorregister.AktorregisterConsumer
import no.nav.syfo.narmesteleder.consumer.NaermesteLederStatus
import no.nav.syfo.narmesteleder.consumer.Naermesteleder
import no.nav.syfo.narmesteleder.controller.NarmesteLederMapper
import no.nav.syfo.narmesteleder.controller.RSNaermesteLeder
import no.nav.syfo.testhelper.UserConstants
import no.nav.syfo.testhelper.UserConstants.LEDER_FNR
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDate

@ExtendWith(SpringExtension::class)
class NaermesteLederMapperTest {

    private val aktorregisterConsumer: AktorregisterConsumer = mockk()

    private val narmesteLederMapper = NarmesteLederMapper(aktorregisterConsumer)

    @Test
    fun mapNaermesteLederMedAktorIdSuccess() {
        val leder = Naermesteleder(
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
        )

        val rsLeder = RSNaermesteLeder(
                virksomhetsnummer = leder.orgnummer,
                navn = leder.navn,
                epost = leder.epost,
                tlf = leder.mobil,
                erAktiv = leder.naermesteLederStatus.erAktiv,
                aktivFom = leder.naermesteLederStatus.aktivFom,
                aktivTom = leder.naermesteLederStatus.aktivTom,
                fnr = LEDER_FNR,
                samtykke = null,
                sistInnlogget = null
        )

        every { aktorregisterConsumer.hentFnrForAktor(UserConstants.LEDER_AKTORID) }.returns(LEDER_FNR)

        val rsNaermesteLeder = narmesteLederMapper.map(leder)

        assertEquals(rsLeder, rsNaermesteLeder)
    }

    @Test
    fun mapNaermesteLederMedFnrSuccess() {
        val leder = Naermesteleder(
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
        )

        val rsLeder = RSNaermesteLeder(
                virksomhetsnummer = leder.orgnummer,
                navn = leder.navn,
                epost = leder.epost,
                tlf = leder.mobil,
                erAktiv = leder.naermesteLederStatus.erAktiv,
                aktivFom = leder.naermesteLederStatus.aktivFom,
                aktivTom = leder.naermesteLederStatus.aktivTom,
                fnr = LEDER_FNR,
                samtykke = null,
                sistInnlogget = null
        )

        val rsNaermesteLeder = narmesteLederMapper.map(leder)

        assertEquals(rsLeder, rsNaermesteLeder)
    }
}
