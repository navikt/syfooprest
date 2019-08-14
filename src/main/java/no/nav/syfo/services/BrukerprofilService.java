package no.nav.syfo.services;

import no.nav.common.auth.SubjectHandler;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.BrukerprofilV3;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.HentKontaktinformasjonOgPreferanserPersonIdentErUtgaatt;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.HentKontaktinformasjonOgPreferanserPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.HentKontaktinformasjonOgPreferanserSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.informasjon.WSNorskIdent;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.informasjon.WSPerson;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.informasjon.WSPersonidenter;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.meldinger.WSHentKontaktinformasjonOgPreferanserRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;

import javax.inject.Inject;
import javax.ws.rs.ForbiddenException;

import static no.nav.common.auth.SubjectHandler.getIdent;
import static org.apache.commons.lang3.text.WordUtils.capitalize;

public class BrukerprofilService {
    private static final Logger LOG = LoggerFactory.getLogger(BrukerprofilService.class);
    @Inject
    private BrukerprofilV3 brukerprofilV3;

    @Cacheable(value = "tps", keyGenerator = "userkeygenerator")
    public boolean erKode6eller7(String fnr) {
        if (!fnr.matches("\\d{11}$")) {
            LOG.error("{} prøvde å hente kode6-7 info med fnr {}", SubjectHandler.getIdentType(), fnr);
            throw new RuntimeException();
        }
        try {
            WSPerson wsPerson = brukerprofilV3.hentKontaktinformasjonOgPreferanser(new WSHentKontaktinformasjonOgPreferanserRequest()
                    .withIdent(new WSNorskIdent()
                            .withIdent(fnr)
                            .withType(new WSPersonidenter()
                                    .withKodeRef("http://nav.no/kodeverk/Term/Personidenter/FNR/nb/F_c3_b8dselnummer?v=1")
                                    .withValue("FNR")
                            ))).getBruker();
            return ((wsPerson.getDiskresjonskode() != null) && "6".equals(wsPerson.getDiskresjonskode().getValue())) || ((wsPerson.getDiskresjonskode() != null) && "7".equals(wsPerson.getDiskresjonskode().getValue()));
        } catch (RuntimeException e) {
            LOG.error("Exception mot TPS med ident {}  -  {}", fnr, e.getMessage());
            throw e;
        } catch (HentKontaktinformasjonOgPreferanserPersonIdentErUtgaatt e) {
            LOG.error("Identen er utgått", e);
            throw new RuntimeException();
        } catch (HentKontaktinformasjonOgPreferanserSikkerhetsbegrensning e) {
            return true;
        } catch (HentKontaktinformasjonOgPreferanserPersonIkkeFunnet e) {
            LOG.error("Person ikke funnet", e);
            throw new RuntimeException();
        }
    }

    @Cacheable(value = "tps", keyGenerator = "userkeygenerator")
    public String hentNavnByFnr(String fnr) {
        if (!fnr.matches("\\d{11}$")) {
            LOG.error("{} prøvde å hente navn med fnr {}", getIdent(), fnr);
            throw new RuntimeException();
        }
        try {
            WSPerson wsPerson = brukerprofilV3.hentKontaktinformasjonOgPreferanser(new WSHentKontaktinformasjonOgPreferanserRequest()
                    .withIdent(new WSNorskIdent()
                            .withIdent(fnr)
                            .withType(new WSPersonidenter()
                                    .withKodeRef("http://nav.no/kodeverk/Term/Personidenter/FNR/nb/F_c3_b8dselnummer?v=1")
                                    .withValue("FNR")
                            ))).getBruker();
            String mellomnavn = wsPerson.getPersonnavn().getMellomnavn() == null ? "" : wsPerson.getPersonnavn().getMellomnavn();
            if (!"".equals(mellomnavn)) {
                mellomnavn = mellomnavn + " ";
            }
            final String navnFraTps = wsPerson.getPersonnavn().getFornavn() + " " + mellomnavn + wsPerson.getPersonnavn().getEtternavn();
            return capitalize(navnFraTps.toLowerCase(), '-', ' ');
        } catch (HentKontaktinformasjonOgPreferanserPersonIdentErUtgaatt e) {
            LOG.error("HentKontaktinformasjonOgPreferanserPersonIdentErUtgaatt for {} med FNR", getIdent(), fnr, e);
            throw new RuntimeException();
        } catch (HentKontaktinformasjonOgPreferanserSikkerhetsbegrensning e) {
            LOG.error("Sikkerhetsbegrensning for {} med FNR {}", getIdent(), fnr, e);
            throw new ForbiddenException();
        } catch (HentKontaktinformasjonOgPreferanserPersonIkkeFunnet e) {
            LOG.error("HentKontaktinformasjonOgPreferanserPersonIkkeFunnet for {} med FNR", getIdent(), fnr, e);
            throw new RuntimeException();
        } catch (RuntimeException e) {
            LOG.error("{} fikk RuntimeException mot TPS med ved oppslag av {}", getIdent(), fnr, e);
            return "Vi fant ikke navnet";
        }
    }

}
