package no.nav.syfo.mock;

import no.nav.tjeneste.virksomhet.organisasjon.v4.*;
import no.nav.tjeneste.virksomhet.organisasjon.v4.informasjon.WSOrganisasjon;
import no.nav.tjeneste.virksomhet.organisasjon.v4.informasjon.WSUstrukturertNavn;
import no.nav.tjeneste.virksomhet.organisasjon.v4.meldinger.*;

public class OrganisasjonMock implements OrganisasjonV4 {

    @Override
    public WSHentOrganisasjonResponse hentOrganisasjon(WSHentOrganisasjonRequest request) throws HentOrganisasjonOrganisasjonIkkeFunnet, HentOrganisasjonUgyldigInput {
        return new WSHentOrganisasjonResponse().withOrganisasjon(new WSOrganisasjon().withNavn(new WSUstrukturertNavn().withNavnelinje("NAV Karasjok Consulting AS")));
    }

    @Override
    public WSHentOrganisasjonsnavnBolkResponse hentOrganisasjonsnavnBolk(WSHentOrganisasjonsnavnBolkRequest request) {
        throw new RuntimeException("Ikke implementert i mock");
    }

    @Override
    public WSFinnOrganisasjonsendringerListeResponse finnOrganisasjonsendringerListe(WSFinnOrganisasjonsendringerListeRequest request) throws FinnOrganisasjonsendringerListeUgyldigInput {
        throw new RuntimeException("Ikke implementert i mock");
    }

    @Override
    public WSFinnOrganisasjonResponse finnOrganisasjon(WSFinnOrganisasjonRequest request) throws FinnOrganisasjonUgyldigInput, FinnOrganisasjonForMangeForekomster {
        throw new RuntimeException("Ikke implementert i mock");
    }

    @Override
    public WSHentNoekkelinfoOrganisasjonResponse hentNoekkelinfoOrganisasjon(WSHentNoekkelinfoOrganisasjonRequest request) throws HentNoekkelinfoOrganisasjonOrganisasjonIkkeFunnet, HentNoekkelinfoOrganisasjonUgyldigInput {
        throw new RuntimeException("Ikke implementert i mock");
    }

    @Override
    public WSHentVirksomhetsOrgnrForJuridiskOrgnrBolkResponse hentVirksomhetsOrgnrForJuridiskOrgnrBolk(WSHentVirksomhetsOrgnrForJuridiskOrgnrBolkRequest request) {
        throw new RuntimeException("Ikke implementert i mock");
    }

    @Override
    public WSValiderOrganisasjonResponse validerOrganisasjon(WSValiderOrganisasjonRequest request) throws ValiderOrganisasjonOrganisasjonIkkeFunnet, ValiderOrganisasjonUgyldigInput {
        throw new RuntimeException("Ikke implementert i mock");
    }

    @Override
    public void ping() {
    }
}
