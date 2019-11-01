package no.nav.syfo.services;

import no.nav.tjeneste.virksomhet.organisasjon.v4.*;
import no.nav.tjeneste.virksomhet.organisasjon.v4.informasjon.WSUstrukturertNavn;
import no.nav.tjeneste.virksomhet.organisasjon.v4.meldinger.WSHentOrganisasjonRequest;
import no.nav.tjeneste.virksomhet.organisasjon.v4.meldinger.WSHentOrganisasjonResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

import static java.util.stream.Collectors.joining;
import static org.slf4j.LoggerFactory.getLogger;

@Service
public class OrganisasjonService {

    private static final Logger log = getLogger(OrganisasjonService.class);

    private final OrganisasjonV4 organisasjonV4;

    @Inject
    public OrganisasjonService(OrganisasjonV4 organisasjonV4) {
        this.organisasjonV4 = organisasjonV4;
    }

    @Cacheable(cacheNames = "virksomhetsnavnByNr", key = "#orgnr", condition = "#orgnr != null")
    public String hentNavn(String orgnr) {
        if (!orgnr.matches("\\d{9}$")) {
            log.error("Prøvde å hente orgnavn med orgnr {}", orgnr);
            throw new RuntimeException();
        }
        try {
            WSHentOrganisasjonResponse response = organisasjonV4.hentOrganisasjon(request(orgnr));
            WSUstrukturertNavn ustrukturertNavn = (WSUstrukturertNavn) response.getOrganisasjon().getNavn();

            return ustrukturertNavn.getNavnelinje().stream()
                    .filter(StringUtils::isNotBlank)
                    .collect(joining(", "));

        } catch (HentOrganisasjonOrganisasjonIkkeFunnet e) {
            log.warn("Kunne ikke hente organisasjon for {}", orgnr, e);
        } catch (HentOrganisasjonUgyldigInput e) {
            log.warn("Ugyldig input for {}", orgnr, e);
        } catch (RuntimeException e) {
            log.error("Fikk en runtimefeil mot organisasjon med orgnr {}", orgnr);
        }
        return "Fant ikke navn";
    }

    private WSHentOrganisasjonRequest request(String orgnr) {
        return new WSHentOrganisasjonRequest()
                .withOrgnummer(orgnr)
                .withInkluderHierarki(false)
                .withInkluderHistorikk(false);
    }
}
