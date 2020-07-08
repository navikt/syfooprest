package no.nav.syfo.tilgang

import no.nav.security.oidc.context.OIDCRequestContextHolder
import no.nav.syfo.LocalApplication
import no.nav.syfo.testhelper.OidcTestHelper.loggInnBruker
import no.nav.syfo.tilgang.consumer.BrukerTilgangConsumer
import org.assertj.core.api.Assertions
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit4.SpringRunner
import javax.inject.Inject

@RunWith(SpringRunner::class)
@SpringBootTest(classes = [LocalApplication::class])
@DirtiesContext
class TilgangskontrollServiceTest {
    @Inject
    private lateinit var tilgangskontrollService: TilgangskontrollService

    @Inject
    private lateinit var oidcRequestContextHolder: OIDCRequestContextHolder

    @MockBean
    private lateinit var brukerTilgangConsumer: BrukerTilgangConsumer

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
        Mockito.`when`(brukerTilgangConsumer.hasAccessToAnsatt(SPOR_OM_FNR)).thenReturn(true)
        val tilgang = tilgangskontrollService.sporOmNoenAndreEnnSegSelvEllerEgneAnsatte(INNLOGGET_FNR, SPOR_OM_FNR)
        Assertions.assertThat(tilgang).isFalse()
    }

    @Test
    fun sporOmNoenAndreEnnSegSelvGirTrueNaarManSporOmEnSomIkkeErSegSelvOgIkkeAnsatt() {
        Mockito.`when`(brukerTilgangConsumer.hasAccessToAnsatt(SPOR_OM_FNR)).thenReturn(false)
        val tilgang = tilgangskontrollService.sporOmNoenAndreEnnSegSelvEllerEgneAnsatte(INNLOGGET_FNR, SPOR_OM_FNR)
        Assertions.assertThat(tilgang).isTrue()
    }

    companion object {
        private const val INNLOGGET_FNR = "11990022334"
        private const val SPOR_OM_FNR = "12345678902"
    }
}
