package no.nav.syfo.mock;

import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.*;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.informasjon.WSEpostadresse;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.informasjon.WSKontaktinformasjon;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.informasjon.WSMobiltelefonnummer;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.meldinger.*;

import java.time.OffsetDateTime;

public class DKIFMock implements DigitalKontaktinformasjonV1 {

    @Override
    public WSHentDigitalKontaktinformasjonResponse hentDigitalKontaktinformasjon(WSHentDigitalKontaktinformasjonRequest request) throws HentDigitalKontaktinformasjonKontaktinformasjonIkkeFunnet, HentDigitalKontaktinformasjonSikkerhetsbegrensing, HentDigitalKontaktinformasjonPersonIkkeFunnet {
        return new WSHentDigitalKontaktinformasjonResponse()
                .withDigitalKontaktinformasjon(new WSKontaktinformasjon()
                        .withEpostadresse(new WSEpostadresse().withValue("test@nav.no").withSistVerifisert(OffsetDateTime.now().minusDays(10)))
                        .withMobiltelefonnummer(new WSMobiltelefonnummer().withValue("12345678").withSistVerifisert(OffsetDateTime.now().minusDays(10)))
                        .withReservasjon("false"));
    }

    @Override
    public WSHentSikkerDigitalPostadresseBolkResponse hentSikkerDigitalPostadresseBolk(WSHentSikkerDigitalPostadresseBolkRequest request) throws HentSikkerDigitalPostadresseBolkSikkerhetsbegrensing, HentSikkerDigitalPostadresseBolkForMangeForespoersler {
        throw new RuntimeException("Denne er ikke implementert i mocken");
    }

    @Override
    public WSHentPrintsertifikatResponse hentPrintsertifikat(WSHentPrintsertifikatRequest request) {
        throw new RuntimeException("Denne er ikke implementert i mocken");
    }

    @Override
    public WSHentDigitalKontaktinformasjonBolkResponse hentDigitalKontaktinformasjonBolk(WSHentDigitalKontaktinformasjonBolkRequest request) throws HentDigitalKontaktinformasjonBolkSikkerhetsbegrensing, HentDigitalKontaktinformasjonBolkForMangeForespoersler {
        throw new RuntimeException("Denne er ikke implementert i mocken");
    }

    @Override
    public WSHentSikkerDigitalPostadresseResponse hentSikkerDigitalPostadresse(WSHentSikkerDigitalPostadresseRequest request) throws HentSikkerDigitalPostadresseKontaktinformasjonIkkeFunnet, HentSikkerDigitalPostadresseSikkerhetsbegrensing, HentSikkerDigitalPostadressePersonIkkeFunnet {
        throw new RuntimeException("Denne er ikke implementert i mocken");
    }

    @Override
    public void ping() {

    }
}
