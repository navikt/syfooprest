package no.nav.syfo.services;

import no.nav.security.oidc.context.OIDCRequestContextHolder;
import no.nav.syfo.config.SykefravaersoppfoelgingV1Config;
import no.nav.syfo.oidc.OIDCIssuer;
import no.nav.syfo.rest.domain.RSNaermesteLeder;
import no.nav.tjeneste.virksomhet.sykefravaersoppfoelging.v1.HentNaermesteLederListeSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.sykefravaersoppfoelging.v1.meldinger.WSHentNaermesteLederListeRequest;
import no.nav.tjeneste.virksomhet.sykefravaersoppfoelging.v1.meldinger.WSHentNaermesteLederListeResponse;
import org.slf4j.Logger;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.ws.rs.ForbiddenException;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static no.nav.syfo.mappers.WSNaermesteLederMapper.ws2rsnaermesteleder;
import static no.nav.syfo.utils.MapUtil.mapListe;
import static no.nav.syfo.utils.OIDCUtil.getIssuerToken;
import static org.slf4j.LoggerFactory.getLogger;

@Service
public class NaermesteLederService {
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
