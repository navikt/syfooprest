package no.nav.syfo.services;

import no.nav.syfo.rest.domain.RSKontaktinfo;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.*;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.informasjon.*;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.meldinger.WSHentDigitalKontaktinformasjonRequest;
import org.slf4j.Logger;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.OffsetDateTime;

import static java.util.Optional.ofNullable;
import static no.nav.syfo.rest.domain.RSKontaktinfo.FeilAarsak.*;
import static no.nav.syfo.util.DateUtilKt.convertToOffsetDateTime;
import static org.slf4j.LoggerFactory.getLogger;

@Service
public class DkifService {

    private static final Logger log = getLogger(DkifService.class);

    private final DigitalKontaktinformasjonV1 dkifV1;
    private final AktoerService aktoerService;

    @Inject
    public DkifService(
            DigitalKontaktinformasjonV1 dkifV1,
            AktoerService aktoerService
    ) {
        this.dkifV1 = dkifV1;
        this.aktoerService = aktoerService;
    }

    @Cacheable(cacheNames = "kontaktinfoByFnr", key = "#fnr", condition = "#fnr != null")
    public RSKontaktinfo hentRSKontaktinfoFnr(String fnr) {
        if (!fnr.matches("\\d{11}$")) {
            log.error("Prøvde å hente kontaktinfo med fnr {}", fnr);
            throw new RuntimeException();
        }

        try {
            WSKontaktinformasjon response = dkifV1.hentDigitalKontaktinformasjon(new WSHentDigitalKontaktinformasjonRequest().withPersonident(fnr)).getDigitalKontaktinformasjon();
            if ("true".equalsIgnoreCase(response.getReservasjon())) {
                return new RSKontaktinfo().fnr(fnr).skalHaVarsel(false).feilAarsak(RESERVERT);
            }

            if (!harVerfisertSiste18Mnd(response.getEpostadresse(), response.getMobiltelefonnummer())) {
                return new RSKontaktinfo().fnr(fnr).skalHaVarsel(false).feilAarsak(UTGAATT);
            }

            return new RSKontaktinfo()
                    .fnr(fnr)
                    .skalHaVarsel(true)
                    .epost(response.getEpostadresse() != null ? response.getEpostadresse().getValue() : "")
                    .tlf(response.getMobiltelefonnummer() != null ? response.getMobiltelefonnummer().getValue() : "");
        } catch (HentDigitalKontaktinformasjonKontaktinformasjonIkkeFunnet e) {
            return new RSKontaktinfo().fnr(fnr).skalHaVarsel(false).feilAarsak(KONTAKTINFO_IKKE_FUNNET);
        } catch (HentDigitalKontaktinformasjonSikkerhetsbegrensing e) {
            return new RSKontaktinfo().fnr(fnr).skalHaVarsel(false).feilAarsak(SIKKERHETSBEGRENSNING);
        } catch (HentDigitalKontaktinformasjonPersonIkkeFunnet e) {
            return new RSKontaktinfo().fnr(fnr).skalHaVarsel(false).feilAarsak(PERSON_IKKE_FUNNET);
        } catch (RuntimeException e) {
            log.error("Fikk en runtimefeil mot DKIF med fnr {}", fnr, e);
            throw e;
        }
    }

    public boolean harVerfisertSiste18Mnd(WSEpostadresse epostadresse, WSMobiltelefonnummer mobiltelefonnummer) {
        return harVerifisertEpostSiste18Mnd(epostadresse) && harVerifisertMobilSiste18Mnd(mobiltelefonnummer);
    }

    private boolean harVerifisertEpostSiste18Mnd(WSEpostadresse epostadresse) {
        return ofNullable(epostadresse)
                .map(WSEpostadresse::getSistVerifisert)
                .filter(sistVerifisertEpost -> convertToOffsetDateTime(sistVerifisertEpost).isAfter(OffsetDateTime.now().minusMonths(18)))
                .isPresent();
    }

    private boolean harVerifisertMobilSiste18Mnd(WSMobiltelefonnummer mobiltelefonnummer) {
        return ofNullable(mobiltelefonnummer)
                .map(WSMobiltelefonnummer::getSistVerifisert)
                .filter(sistVerifisertEpost -> convertToOffsetDateTime(sistVerifisertEpost).isAfter(OffsetDateTime.now().minusMonths(18)))
                .isPresent();
    }

    @Cacheable(cacheNames = "kontaktinfoByAktorId", key = "#aktoerId", condition = "#aktoerId != null")
    public RSKontaktinfo hentRSKontaktinfoAktoerId(String aktoerId) {
        return hentRSKontaktinfoFnr(aktoerService.hentFnrForAktoer(aktoerId));
    }
}
