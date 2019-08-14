package no.nav.syfo.services;

import no.nav.syfo.model.NaermesteLeder;
import no.nav.syfo.rest.domain.RSNaermesteLeder;
import no.nav.tjeneste.virksomhet.sykefravaersoppfoelging.v1.*;
import no.nav.tjeneste.virksomhet.sykefravaersoppfoelging.v1.meldinger.*;
import org.slf4j.Logger;
import org.springframework.cache.annotation.Cacheable;

import javax.inject.Inject;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotFoundException;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static no.nav.common.auth.SubjectHandler.getIdent;
import static no.nav.sbl.java8utils.MapUtil.map;
import static no.nav.sbl.java8utils.MapUtil.mapListe;
import static no.nav.syfo.mappers.WSAnsattMapper.wsAnsatt2AktorId;
import static no.nav.syfo.mappers.WSNaermesteLederMapper.ws2naermesteLeder;
import static no.nav.syfo.mappers.WSNaermesteLederMapper.ws2rsnaermesteleder;
import static org.slf4j.LoggerFactory.getLogger;

public class NaermesteLederService {
    private static final Logger LOG = getLogger(NaermesteLederService.class);

    @Inject
    private SykefravaersoppfoelgingV1 sykefravaersoppfoelgingV1;


    @Cacheable(value = "syfo", keyGenerator = "userkeygenerator")
    public List<String> hentAnsatteAktorId(String aktorId) {
        try {
            WSHentNaermesteLedersAnsattListeResponse response = sykefravaersoppfoelgingV1
                    .hentNaermesteLedersAnsattListe(new WSHentNaermesteLedersAnsattListeRequest()
                            .withAktoerId(aktorId));
            return mapListe(response.getAnsattListe(), wsAnsatt2AktorId);
        } catch (HentNaermesteLedersAnsattListeSikkerhetsbegrensning e) {
            LOG.warn("{} fikk sikkerhetsbegrensning ved henting av ansatte for person {}", getIdent(), aktorId);
            throw new ForbiddenException();
        } catch (RuntimeException e) {
            LOG.error("{} fikk Runtimefeil ved henting av ansatte for person {}. " +
                    "Antar dette er tilgang nektet fra modig-security, og kaster ForbiddenException videre.", getIdent(), aktorId, e);
            //TODO RuntimeException når SyfoService kaster sikkerhetsbegrensing riktig igjen
            throw new ForbiddenException();
        }
    }

    @Cacheable(value = "syfo", keyGenerator = "userkeygenerator")
    public List<NaermesteLeder> hentNaermesteLedere(String aktoerId) {
        try {
            WSHentNaermesteLederListeResponse response = sykefravaersoppfoelgingV1.hentNaermesteLederListe(new WSHentNaermesteLederListeRequest()
                    .withAktoerId(aktoerId)
                    .withKunAktive(false));
            return mapListe(response.getNaermesteLederListe(), ws2naermesteLeder);
        } catch (HentNaermesteLederListeSikkerhetsbegrensning e) {
            LOG.warn("{} fikk sikkerhetsbegrensning ved henting av naermeste ledere for person {}", getIdent(), aktoerId);
            throw new ForbiddenException();
        } catch (RuntimeException e) {
            LOG.error("{} fikk Runtimefeil ved henting av naermeste ledere for person {}" +
                    "Antar dette er tilgang nektet fra modig-security, og kaster ForbiddenException videre.", getIdent(), aktoerId, e);
            //TODO RuntimeException når SyfoService kaster sikkerhetsbegrensing riktig igjen
            throw new ForbiddenException();
        }
    }

    public List<String> hentNaermesteLederAktoerIdListe(String aktoerId) {
        return hentNaermesteLedere(aktoerId).stream()
                .map(ansatt -> ansatt.naermesteLederAktoerId)
                .collect(toList());
    }

    @Cacheable(value = "syfo", keyGenerator = "userkeygenerator")
    public RSNaermesteLeder hentNaermesteLeder(String aktoerId, String virksomhetsnummer) {

        try {
            WSHentNaermesteLederResponse response = sykefravaersoppfoelgingV1.hentNaermesteLeder(new WSHentNaermesteLederRequest()
                    .withAktoerId(aktoerId)
                    .withOrgnummer(virksomhetsnummer));
            if (response.getNaermesteLeder() == null) {
                throw new NotFoundException();
            }
            return map(response.getNaermesteLeder(), ws2rsnaermesteleder);

        } catch (HentNaermesteLederSikkerhetsbegrensning e) {
            throw new ForbiddenException(e);
        }

    }

    @Cacheable(value = "syfo", keyGenerator = "userkeygenerator")
    public Optional<RSNaermesteLeder> hentForrigeNaermesteLeder(String aktoerid, String virksomhetsnummer) {
        List<RSNaermesteLeder> naermesteLedere = hentNaermesteLedere(aktoerid, false, virksomhetsnummer);

        return naermesteLedere.stream()
                .filter(leder -> !leder.erAktiv)
                .sorted((o1, o2) -> o2.aktivTom.compareTo(o1.aktivTom))
                .findFirst();
    }

    protected List<RSNaermesteLeder> hentNaermesteLedere(String aktoerId, boolean kunAktive, String virksomhetsnummer) {
        try {
            WSHentNaermesteLederListeResponse wsNaermesteLederListeResponse = sykefravaersoppfoelgingV1.hentNaermesteLederListe(new WSHentNaermesteLederListeRequest()
                    .withAktoerId(aktoerId)
                    .withKunAktive(kunAktive));
            return mapListe(wsNaermesteLederListeResponse.getNaermesteLederListe(), ws2rsnaermesteleder)
                    .stream()
                    .filter(rsNaermesteLeder -> rsNaermesteLeder.virksomhetsnummer.equals(virksomhetsnummer))
                    .collect(toList());
        } catch (HentNaermesteLederListeSikkerhetsbegrensning e) {
            throw new ForbiddenException();
        }
    }

}
