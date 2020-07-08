package no.nav.syfo.virksomhet.consumer

import no.nav.tjeneste.virksomhet.organisasjon.v4.*
import no.nav.tjeneste.virksomhet.organisasjon.v4.informasjon.WSOrganisasjon
import no.nav.tjeneste.virksomhet.organisasjon.v4.informasjon.WSUstrukturertNavn
import no.nav.tjeneste.virksomhet.organisasjon.v4.meldinger.*
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service

@Service
@ConditionalOnProperty(value = [EregConfig.MOCK_KEY], havingValue = "true")
class OrganisasjonMock : OrganisasjonV4 {
    @Throws(HentOrganisasjonOrganisasjonIkkeFunnet::class, HentOrganisasjonUgyldigInput::class)
    override fun hentOrganisasjon(request: WSHentOrganisasjonRequest): WSHentOrganisasjonResponse {
        return WSHentOrganisasjonResponse().withOrganisasjon(WSOrganisasjon().withNavn(WSUstrukturertNavn().withNavnelinje("NAV Karasjok Consulting AS")))
    }

    override fun hentOrganisasjonsnavnBolk(request: WSHentOrganisasjonsnavnBolkRequest): WSHentOrganisasjonsnavnBolkResponse {
        throw RuntimeException("Ikke implementert i mock")
    }

    @Throws(FinnOrganisasjonsendringerListeUgyldigInput::class)
    override fun finnOrganisasjonsendringerListe(request: WSFinnOrganisasjonsendringerListeRequest): WSFinnOrganisasjonsendringerListeResponse {
        throw RuntimeException("Ikke implementert i mock")
    }

    @Throws(FinnOrganisasjonUgyldigInput::class, FinnOrganisasjonForMangeForekomster::class)
    override fun finnOrganisasjon(request: WSFinnOrganisasjonRequest): WSFinnOrganisasjonResponse {
        throw RuntimeException("Ikke implementert i mock")
    }

    @Throws(HentNoekkelinfoOrganisasjonOrganisasjonIkkeFunnet::class, HentNoekkelinfoOrganisasjonUgyldigInput::class)
    override fun hentNoekkelinfoOrganisasjon(request: WSHentNoekkelinfoOrganisasjonRequest): WSHentNoekkelinfoOrganisasjonResponse {
        throw RuntimeException("Ikke implementert i mock")
    }

    override fun hentVirksomhetsOrgnrForJuridiskOrgnrBolk(request: WSHentVirksomhetsOrgnrForJuridiskOrgnrBolkRequest): WSHentVirksomhetsOrgnrForJuridiskOrgnrBolkResponse {
        throw RuntimeException("Ikke implementert i mock")
    }

    @Throws(ValiderOrganisasjonOrganisasjonIkkeFunnet::class, ValiderOrganisasjonUgyldigInput::class)
    override fun validerOrganisasjon(request: WSValiderOrganisasjonRequest): WSValiderOrganisasjonResponse {
        throw RuntimeException("Ikke implementert i mock")
    }

    override fun ping() {}
}
