package no.nav.syfo.tilgang

import io.mockk.every
import io.mockk.mockk
import no.nav.security.oidc.context.OIDCRequestContextHolder
import no.nav.syfo.LocalApplication
import no.nav.syfo.testhelper.OidcTestHelper.loggInnBruker
import no.nav.syfo.tilgang.consumer.BrukerTilgangConsumer
import org.assertj.core.api.Assertions
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit4.SpringRunner
import javax.inject.Inject

@RunWith(SpringRunner::class)
@SpringBootTest(classes = [LocalApplication::class])
@DirtiesContext
class TilgangskontrollServiceTest {
    @Inject
    private lateinit var oidcRequestContextHolder: OIDCRequestContextHolder

    private val brukerTilgangConsumer: BrukerTilgangConsumer = mockk()

    private val tilgangskontrollService = TilgangskontrollService(
        brukerTilgangConsumer
    )

    @Before
    fun setup() {
        loggInnBruker(oidcRequestContextHolder, INNLOGGET_FNR)
    }

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
