package no.nav.syfo.kontaktinfo.consumer

import no.nav.syfo.LocalApplication
import no.nav.syfo.util.getXMLGregorianCalendarNow
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.informasjon.WSEpostadresse
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.informasjon.WSMobiltelefonnummer
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit.jupiter.SpringExtension
import javax.inject.Inject

@ExtendWith(SpringExtension::class)
@SpringBootTest(classes = [LocalApplication::class])
@DirtiesContext
class DkifServiceTest {
    @Inject
    private lateinit var dkifConsumer: DkifConsumer

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
