package no.nav.syfo.kontaktinfo.consumer

import io.mockk.mockk
import no.nav.syfo.LocalApplication
import no.nav.syfo.util.getXMLGregorianCalendarNow
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.DigitalKontaktinformasjonV1
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.informasjon.WSEpostadresse
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.informasjon.WSMobiltelefonnummer
import org.assertj.core.api.Assertions
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit4.SpringRunner
import javax.inject.Inject

@RunWith(SpringRunner::class)
@SpringBootTest(classes = [LocalApplication::class])
@DirtiesContext
class DkifServiceTest {
    @Inject
    private lateinit var dkifConsumer: DkifConsumer

    private val dkifV1: DigitalKontaktinformasjonV1 = mockk()

    @Test
    fun verifisertSiste18mndErNullsafe() {
        var harVerifisert = dkifConsumer.harVerfisertSiste18Mnd(WSEpostadresse(), null)
        Assertions.assertThat(harVerifisert).isFalse()
        harVerifisert = dkifConsumer.harVerfisertSiste18Mnd(null, null)
        Assertions.assertThat(harVerifisert).isFalse()
        harVerifisert = dkifConsumer.harVerfisertSiste18Mnd(WSEpostadresse(), WSMobiltelefonnummer())
        Assertions.assertThat(harVerifisert).isFalse()
    }

    @Test
    fun verifisertSiste18mnd() {
        val harVerifisert = dkifConsumer.harVerfisertSiste18Mnd(WSEpostadresse()
            .withSistVerifisert(getXMLGregorianCalendarNow(2)), WSMobiltelefonnummer().withSistVerifisert(getXMLGregorianCalendarNow(4)))
        Assertions.assertThat(harVerifisert).isTrue()
    }
}
