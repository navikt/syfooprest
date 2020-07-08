package no.nav.syfo.kontaktinfo.consumer

import no.nav.syfo.util.getXMLGregorianCalendarNow
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.*
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.informasjon.*
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.meldinger.*
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service

@Service
@ConditionalOnProperty(value = [DkifConfig.MOCK_KEY], havingValue = "true")
class DKIFMock : DigitalKontaktinformasjonV1 {
    @Throws(HentDigitalKontaktinformasjonKontaktinformasjonIkkeFunnet::class, HentDigitalKontaktinformasjonSikkerhetsbegrensing::class, HentDigitalKontaktinformasjonPersonIkkeFunnet::class)
    override fun hentDigitalKontaktinformasjon(request: WSHentDigitalKontaktinformasjonRequest): WSHentDigitalKontaktinformasjonResponse {
        val sistVerifisert = getXMLGregorianCalendarNow(10)
        return WSHentDigitalKontaktinformasjonResponse()
            .withDigitalKontaktinformasjon(WSKontaktinformasjon()
                .withEpostadresse(WSEpostadresse().withValue("test@nav.no").withSistVerifisert(sistVerifisert))
                .withEpostadresse(WSEpostadresse().withValue("test@nav.no").withSistVerifisert(sistVerifisert))
                .withMobiltelefonnummer(WSMobiltelefonnummer().withValue("12345678").withSistVerifisert(sistVerifisert))
                .withReservasjon("false"))
    }

    @Throws(HentSikkerDigitalPostadresseBolkSikkerhetsbegrensing::class, HentSikkerDigitalPostadresseBolkForMangeForespoersler::class)
    override fun hentSikkerDigitalPostadresseBolk(request: WSHentSikkerDigitalPostadresseBolkRequest): WSHentSikkerDigitalPostadresseBolkResponse {
        throw RuntimeException("Denne er ikke implementert i mocken")
    }

    override fun hentPrintsertifikat(request: WSHentPrintsertifikatRequest): WSHentPrintsertifikatResponse {
        throw RuntimeException("Denne er ikke implementert i mocken")
    }

    @Throws(HentDigitalKontaktinformasjonBolkSikkerhetsbegrensing::class, HentDigitalKontaktinformasjonBolkForMangeForespoersler::class)
    override fun hentDigitalKontaktinformasjonBolk(request: WSHentDigitalKontaktinformasjonBolkRequest): WSHentDigitalKontaktinformasjonBolkResponse {
        throw RuntimeException("Denne er ikke implementert i mocken")
    }

    @Throws(HentSikkerDigitalPostadresseKontaktinformasjonIkkeFunnet::class, HentSikkerDigitalPostadresseSikkerhetsbegrensing::class, HentSikkerDigitalPostadressePersonIkkeFunnet::class)
    override fun hentSikkerDigitalPostadresse(request: WSHentSikkerDigitalPostadresseRequest): WSHentSikkerDigitalPostadresseResponse {
        throw RuntimeException("Denne er ikke implementert i mocken")
    }

    override fun ping() {}
}
