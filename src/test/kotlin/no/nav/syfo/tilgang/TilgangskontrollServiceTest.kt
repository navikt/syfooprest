package no.nav.syfo.tilgang

import io.mockk.every
import io.mockk.mockk
import no.nav.syfo.tilgang.consumer.BrukerTilgangConsumer
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
class TilgangskontrollServiceTest {
    private val brukerTilgangConsumer: BrukerTilgangConsumer = mockk()

    private val tilgangskontrollService = TilgangskontrollService(
        brukerTilgangConsumer
    )

    @Test
    fun sporOmNoenAndreEnnSegSelvGirFalseNaarManSporOmSegSelv() {
        val tilgang = tilgangskontrollService.sporOmNoenAndreEnnSegSelvEllerEgneAnsatte(INNLOGGET_FNR, INNLOGGET_FNR)
        Assertions.assertThat(tilgang).isFalse()
    }

    @Test
    fun sporOmNoenAndreEnnSegSelvGirFalseNaarManSporOmEnAnsatt() {
        every { brukerTilgangConsumer.hasAccessToAnsatt(SPOR_OM_FNR) } returns true

        val tilgang = tilgangskontrollService.sporOmNoenAndreEnnSegSelvEllerEgneAnsatte(INNLOGGET_FNR, SPOR_OM_FNR)
        Assertions.assertThat(tilgang).isFalse()
    }

    @Test
    fun sporOmNoenAndreEnnSegSelvGirTrueNaarManSporOmEnSomIkkeErSegSelvOgIkkeAnsatt() {
        every { brukerTilgangConsumer.hasAccessToAnsatt(SPOR_OM_FNR) } returns false
        val tilgang = tilgangskontrollService.sporOmNoenAndreEnnSegSelvEllerEgneAnsatte(INNLOGGET_FNR, SPOR_OM_FNR)
        Assertions.assertThat(tilgang).isTrue()
    }

    companion object {
        private const val INNLOGGET_FNR = "11990022334"
        private const val SPOR_OM_FNR = "12345678902"
    }
}
