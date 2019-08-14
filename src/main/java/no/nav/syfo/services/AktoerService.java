package no.nav.syfo.services;

import no.nav.syfo.rest.feil.SyfoException;
import no.nav.tjeneste.virksomhet.aktoer.v2.AktoerV2;
import no.nav.tjeneste.virksomhet.aktoer.v2.HentAktoerIdForIdentPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.aktoer.v2.HentIdentForAktoerIdPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.aktoer.v2.meldinger.WSHentAktoerIdForIdentRequest;
import no.nav.tjeneste.virksomhet.aktoer.v2.meldinger.WSHentIdentForAktoerIdRequest;
import org.slf4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cache.annotation.Cacheable;

import javax.inject.Inject;

import static no.nav.common.auth.SubjectHandler.getIdent;
import static no.nav.syfo.rest.feil.Feilmelding.Feil.AKTOER_IKKE_FUNNET;
import static org.slf4j.LoggerFactory.getLogger;

public class AktoerService implements InitializingBean {
    private static AktoerService instance;

    @Override
    public void afterPropertiesSet() throws Exception {
        instance = this;
    }

    public static AktoerService aktoerService() {
        return instance;
    }

    private static final Logger LOG = getLogger(AktoerService.class);

    @Inject
    private AktoerV2 aktoerV2;

    @Cacheable("aktoer")
    public String hentAktoerIdForFnr(String fnr) {
        if (!fnr.matches("\\d{11}$")) {
            LOG.error("{} prøvde å hente navn med fnr {}", getIdent(), fnr);
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

    @Cacheable("aktoer")
    public String hentFnrForAktoer(String aktoerId) {
        if (!aktoerId.matches("\\d{13}$")) {
            LOG.error("{} prøvde å hente navn med aktoerId {}", getIdent(), aktoerId);
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

    //used for test purposes only
    public static void setInstance(AktoerService aktoerService) {
        instance = aktoerService;
    }
}
