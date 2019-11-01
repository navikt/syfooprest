package no.nav.syfo.mappers;

import no.nav.syfo.model.NaermesteLeder;
import no.nav.syfo.model.NaermesteLederStatus;
import no.nav.syfo.rest.domain.RSNaermesteLeder;
import no.nav.tjeneste.virksomhet.sykefravaersoppfoelging.v1.informasjon.WSNaermesteLeder;
import no.nav.tjeneste.virksomhet.sykefravaersoppfoelging.v1.informasjon.WSNaermesteLederStatus;

import java.util.function.Function;

import static no.nav.syfo.services.AktoerService.aktoerService;
import static no.nav.syfo.utils.MapUtil.map;

public class WSNaermesteLederMapper {

    private static Function<WSNaermesteLederStatus, NaermesteLederStatus> ws2naermesteLederStatus = wsNaermesteLederStatus -> new NaermesteLederStatus()
            .erAktiv(wsNaermesteLederStatus.isErAktiv())
            .aktivFom(wsNaermesteLederStatus.getAktivFom())
            .aktivTom(wsNaermesteLederStatus.getAktivTom());

    public static Function<WSNaermesteLeder, NaermesteLeder> ws2naermesteLeder = wsNaermesteLeder -> new NaermesteLeder()
            .naermesteLederId(wsNaermesteLeder.getNaermesteLederId())
            .naermesteLederAktoerId(wsNaermesteLeder.getNaermesteLederAktoerId())
            .naermesteLederStatus(map(wsNaermesteLeder.getNaermesteLederStatus(), ws2naermesteLederStatus))
            .orgnummer(wsNaermesteLeder.getOrgnummer())
            .epost(wsNaermesteLeder.getEpost())
            .mobil(wsNaermesteLeder.getMobil())
            .navn(wsNaermesteLeder.getNavn());

    public static Function<WSNaermesteLeder, RSNaermesteLeder> ws2rsnaermesteleder = wsNaermesteLeder ->
            new RSNaermesteLeder()
                    .virksomhetsnummer(wsNaermesteLeder.getOrgnummer())
                    .epost(wsNaermesteLeder.getEpost())
                    .navn(wsNaermesteLeder.getNavn())
                    .fnr(aktoerService().hentFnrForAktoer(wsNaermesteLeder.getNaermesteLederAktoerId()))
                    .tlf(wsNaermesteLeder.getMobil())
                    .erAktiv(wsNaermesteLeder.getNaermesteLederStatus().isErAktiv())
                    .aktivFom(wsNaermesteLeder.getNaermesteLederStatus().getAktivFom())
                    .aktivTom(wsNaermesteLeder.getNaermesteLederStatus().getAktivTom());
}
