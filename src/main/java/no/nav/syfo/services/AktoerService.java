package no.nav.syfo.services;

import no.nav.syfo.rest.feil.SyfoException;
import no.nav.tjeneste.virksomhet.aktoer.v2.*;
import no.nav.tjeneste.virksomhet.aktoer.v2.meldinger.WSHentAktoerIdForIdentRequest;
import no.nav.tjeneste.virksomhet.aktoer.v2.meldinger.WSHentIdentForAktoerIdRequest;
import org.slf4j.Logger;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

import static no.nav.syfo.rest.feil.Feilmelding.Feil.AKTOER_IKKE_FUNNET;
import static org.slf4j.LoggerFactory.getLogger;

@Service
public class AktoerService {

    private final AktoerV2 aktoerV2;

    @Inject
    public AktoerService(AktoerV2 aktoerV2) {
        this.aktoerV2 = aktoerV2;
    }

    private static final Logger LOG = getLogger(AktoerService.class);

    @Cacheable(cacheNames = "aktorByFnr", key = "#fnr", condition = "#fnr != null")
    public String hentAktoerIdForFnr(String fnr) {
        if (!fnr.matches("\\d{11}$")) {
            LOG.error("Pprøvde å hente navn med fnr");
            throw new RuntimeException();
        }
        try {
            return aktoerV2.hentAktoerIdForIdent(
                    new WSHentAktoerIdForIdentRequest()
                            .withIdent(fnr)
            ).getAktoerId();
        } catch (HentAktoerIdForIdentPersonIkkeFunnet e) {
            LOG.warn("AktoerID ikke funnet for fødselsnummer!", e);
            throw new SyfoException(AKTOER_IKKE_FUNNET);
        }
    }

    @Cacheable(cacheNames = "aktorByAktorId", key = "#aktoerId", condition = "#aktoerId != null")
    public String hentFnrForAktoer(String aktoerId) {
        if (!aktoerId.matches("\\d{13}$")) {
            LOG.error("Prøvde å hente navn med aktoerId {}", aktoerId);
            throw new RuntimeException();
        }
        try {
            return aktoerV2.hentIdentForAktoerId(
                    new WSHentIdentForAktoerIdRequest()
                            .withAktoerId(aktoerId)
            ).getIdent();
        } catch (HentIdentForAktoerIdPersonIkkeFunnet e) {
            LOG.warn("FNR ikke funnet for aktoerId!", e);
            throw new SyfoException(AKTOER_IKKE_FUNNET);
        }
    }
}
