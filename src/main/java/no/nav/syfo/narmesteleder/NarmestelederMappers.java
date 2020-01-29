package no.nav.syfo.narmesteleder;

import no.nav.syfo.rest.domain.RSNaermesteLeder;

import java.util.function.Function;

public class NarmestelederMappers {

    public static Function<Naermesteleder, RSNaermesteLeder> narmesteLeder2Rs = naermesteleder ->
            new RSNaermesteLeder()
                    .virksomhetsnummer(naermesteleder.orgnummer)
                    .navn(naermesteleder.navn)
                    .epost(naermesteleder.epost)
                    .tlf(naermesteleder.mobil)
                    .erAktiv(naermesteleder.naermesteLederStatus.erAktiv)
                    .aktivFom(naermesteleder.naermesteLederStatus.aktivFom)
                    .aktivTom(naermesteleder.naermesteLederStatus.aktivTom);
}
