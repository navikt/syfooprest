package no.nav.syfo.services;

import no.nav.tjeneste.virksomhet.organisasjon.v4.HentOrganisasjonOrganisasjonIkkeFunnet;
import no.nav.tjeneste.virksomhet.organisasjon.v4.HentOrganisasjonUgyldigInput;
import no.nav.tjeneste.virksomhet.organisasjon.v4.OrganisasjonV4;
import no.nav.tjeneste.virksomhet.organisasjon.v4.informasjon.WSUstrukturertNavn;
import no.nav.tjeneste.virksomhet.organisasjon.v4.meldinger.WSHentOrganisasjonRequest;
import no.nav.tjeneste.virksomhet.organisasjon.v4.meldinger.WSHentOrganisasjonResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.cache.annotation.Cacheable;

import javax.inject.Inject;

import static java.util.stream.Collectors.joining;
import static no.nav.common.auth.SubjectHandler.getIdent;
import static org.slf4j.LoggerFactory.getLogger;

public class OrganisasjonService {
    private static final Logger LOG = getLogger(OrganisasjonService.class);

    @Inject
    private OrganisasjonV4 organisasjonV4;

    @Cacheable("organisasjon")
    public String hentNavn(String orgnr) {
        if (!orgnr.matches("\\d{9}$")) {
            LOG.error("{} prøvde å hente orgnavn med orgnr {}", getIdent(), orgnr);
            throw new RuntimeException();
        }
        try {
            WSHentOrganisasjonResponse response = organisasjonV4.hentOrganisasjon(request(orgnr));
            WSUstrukturertNavn ustrukturertNavn = (WSUstrukturertNavn) response.getOrganisasjon().getNavn();

            return ustrukturertNavn.getNavnelinje().stream()
                    .filter(StringUtils::isNotBlank)
                    .collect(joining(", "));

        } catch (HentOrganisasjonOrganisasjonIkkeFunnet e) {
            LOG.warn("Kunne ikke hente organisasjon for {}", orgnr, e);
        } catch (HentOrganisasjonUgyldigInput e) {
            LOG.warn("Ugyldig input for {}", orgnr, e);
        } catch (RuntimeException e) {
            LOG.error("{} fikk en runtimefeil mot organisasjon med orgnr {}", getIdent(), orgnr);
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
