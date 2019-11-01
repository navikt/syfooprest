package no.nav.syfo.services;

import no.nav.security.oidc.context.OIDCRequestContextHolder;
import no.nav.syfo.config.SykefravaersoppfoelgingV1Config;
import no.nav.syfo.model.NaermesteLeder;
import no.nav.syfo.oidc.OIDCIssuer;
import no.nav.syfo.rest.domain.RSNaermesteLeder;
import no.nav.tjeneste.virksomhet.sykefravaersoppfoelging.v1.*;
import no.nav.tjeneste.virksomhet.sykefravaersoppfoelging.v1.meldinger.*;
import org.slf4j.Logger;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotFoundException;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static no.nav.syfo.mappers.WSAnsattMapper.wsAnsatt2AktorId;
import static no.nav.syfo.mappers.WSNaermesteLederMapper.ws2naermesteLeder;
import static no.nav.syfo.mappers.WSNaermesteLederMapper.ws2rsnaermesteleder;
import static no.nav.syfo.utils.MapUtil.map;
import static no.nav.syfo.utils.MapUtil.mapListe;
import static no.nav.syfo.utils.OIDCUtil.getIssuerToken;
import static org.slf4j.LoggerFactory.getLogger;

@Service
public class NaermesteLederService {
    private static final Logger LOG = getLogger(NaermesteLederService.class);

    private final OIDCRequestContextHolder oidcContextHolder;
    private final SykefravaersoppfoelgingV1Config sykefravaersoppfoelgingConfig;

    @Inject
    public NaermesteLederService(
            OIDCRequestContextHolder oidcContextHolder,
            SykefravaersoppfoelgingV1Config sykefravaersoppfoelgingConfig
    ) {
        this.oidcContextHolder = oidcContextHolder;
        this.sykefravaersoppfoelgingConfig = sykefravaersoppfoelgingConfig;
    }


    @Cacheable(cacheNames = "ansatteByAktorId", key = "#aktorId", condition = "#aktorId != null")
    public List<String> hentAnsatteAktorId(String aktorId) {
        try {
            String oidcToken = getIssuerToken(this.oidcContextHolder, OIDCIssuer.EKSTERN);
            WSHentNaermesteLedersAnsattListeRequest request = new WSHentNaermesteLedersAnsattListeRequest()
                    .withAktoerId(aktorId);
            WSHentNaermesteLedersAnsattListeResponse response = sykefravaersoppfoelgingConfig.hentNaermesteLedersAnsattListe(request, oidcToken);

            return mapListe(response.getAnsattListe(), wsAnsatt2AktorId);
        } catch (HentNaermesteLedersAnsattListeSikkerhetsbegrensning e) {
            LOG.warn("Fikk sikkerhetsbegrensning ved henting av ansatte for person {}", aktorId);
            throw new ForbiddenException();
        } catch (RuntimeException e) {
            LOG.error("Fikk Runtimefeil ved henting av ansatte for person {}. " +
                    "Antar dette er tilgang nektet fra modig-security, og kaster ForbiddenException videre.", aktorId, e);
            //TODO RuntimeException når SyfoService kaster sikkerhetsbegrensing riktig igjen
            throw new ForbiddenException();
        }
    }

    @Cacheable(cacheNames = "ledereByAktorId", key = "#aktoerId", condition = "#aktoerId != null")
    public List<NaermesteLeder> hentNaermesteLedere(String aktoerId) {
        try {
            String oidcToken = getIssuerToken(this.oidcContextHolder, OIDCIssuer.EKSTERN);
            WSHentNaermesteLederListeRequest request = new WSHentNaermesteLederListeRequest()
                    .withAktoerId(aktoerId)
                    .withKunAktive(false);
            WSHentNaermesteLederListeResponse response = sykefravaersoppfoelgingConfig.hentNaermesteLederListe(request, oidcToken);

            return mapListe(response.getNaermesteLederListe(), ws2naermesteLeder);
        } catch (HentNaermesteLederListeSikkerhetsbegrensning e) {
            LOG.warn("Fikk sikkerhetsbegrensning ved henting av naermeste ledere for person {}", aktoerId);
            throw new ForbiddenException();
        } catch (RuntimeException e) {
            LOG.error("Fikk Runtimefeil ved henting av naermeste ledere for person {}" +
                    "Antar dette er tilgang nektet fra modig-security, og kaster ForbiddenException videre.", aktoerId, e);
            //TODO RuntimeException når SyfoService kaster sikkerhetsbegrensing riktig igjen
            throw new ForbiddenException();
        }
    }

    public List<String> hentNaermesteLederAktoerIdListe(String aktoerId) {
        return hentNaermesteLedere(aktoerId).stream()
                .map(ansatt -> ansatt.naermesteLederAktoerId)
                .collect(toList());
    }

    @Cacheable(cacheNames = "lederByAktorIdAndVirksomhet", key = "#aktoerId.concat(#virksomhetsnummer)", condition = "#aktoerId != null && #virksomhetsnummer != null")
    public RSNaermesteLeder hentNaermesteLeder(String aktoerId, String virksomhetsnummer) {
        try {
            String oidcToken = getIssuerToken(this.oidcContextHolder, OIDCIssuer.EKSTERN);

            WSHentNaermesteLederRequest request = new WSHentNaermesteLederRequest()
                    .withAktoerId(aktoerId)
                    .withOrgnummer(virksomhetsnummer);
            WSHentNaermesteLederResponse response = sykefravaersoppfoelgingConfig.hentNaermesteLeder(request, oidcToken);

            if (response.getNaermesteLeder() == null) {
                throw new NotFoundException();
            }
            return map(response.getNaermesteLeder(), ws2rsnaermesteleder);

        } catch (HentNaermesteLederSikkerhetsbegrensning e) {
            throw new ForbiddenException(e);
        }

    }

    @Cacheable(cacheNames = "forrigeLederByAktorIdAndVirksomhet", key = "#aktoerid.concat(#virksomhetsnummer)", condition = "#aktoerid != null && #virksomhetsnummer != null")
    public Optional<RSNaermesteLeder> hentForrigeNaermesteLeder(String aktoerid, String virksomhetsnummer) {
        List<RSNaermesteLeder> naermesteLedere = hentNaermesteLedere(aktoerid, false, virksomhetsnummer);

        return naermesteLedere.stream()
                .filter(leder -> !leder.erAktiv)
                .sorted((o1, o2) -> o2.aktivTom.compareTo(o1.aktivTom))
                .findFirst();
    }

    protected List<RSNaermesteLeder> hentNaermesteLedere(String aktoerId, boolean kunAktive, String virksomhetsnummer) {
        try {
            String oidcToken = getIssuerToken(this.oidcContextHolder, OIDCIssuer.EKSTERN);
            WSHentNaermesteLederListeRequest request = new WSHentNaermesteLederListeRequest()
                    .withAktoerId(aktoerId)
                    .withKunAktive(kunAktive);
            WSHentNaermesteLederListeResponse response = sykefravaersoppfoelgingConfig.hentNaermesteLederListe(request, oidcToken);

            return mapListe(response.getNaermesteLederListe(), ws2rsnaermesteleder)
                    .stream()
                    .filter(rsNaermesteLeder -> rsNaermesteLeder.virksomhetsnummer.equals(virksomhetsnummer))
                    .collect(toList());
        } catch (HentNaermesteLederListeSikkerhetsbegrensning e) {
            throw new ForbiddenException();
        }
    }
}
