package no.nav.syfo.mock;

import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.*;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.informasjon.*;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.meldinger.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import javax.xml.datatype.XMLGregorianCalendar;

import static no.nav.syfo.config.DkifConfig.MOCK_KEY;
import static no.nav.syfo.util.DateUtilKt.getXMLGregorianCalendarNow;

@Service
@ConditionalOnProperty(value = MOCK_KEY, havingValue = "true")
public class DKIFMock implements DigitalKontaktinformasjonV1 {

    @Override
    public WSHentDigitalKontaktinformasjonResponse hentDigitalKontaktinformasjon(WSHentDigitalKontaktinformasjonRequest request) throws HentDigitalKontaktinformasjonKontaktinformasjonIkkeFunnet, HentDigitalKontaktinformasjonSikkerhetsbegrensing, HentDigitalKontaktinformasjonPersonIkkeFunnet {
        XMLGregorianCalendar sistVerifisert = getXMLGregorianCalendarNow(10);
        return new WSHentDigitalKontaktinformasjonResponse()
                .withDigitalKontaktinformasjon(new WSKontaktinformasjon()
                        .withEpostadresse(new WSEpostadresse().withValue("test@nav.no").withSistVerifisert(sistVerifisert))
                        .withEpostadresse(new WSEpostadresse().withValue("test@nav.no").withSistVerifisert(sistVerifisert))
                        .withMobiltelefonnummer(new WSMobiltelefonnummer().withValue("12345678").withSistVerifisert(sistVerifisert))
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
