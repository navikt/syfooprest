package no.nav.syfo.narmesteleder.controller

import no.nav.syfo.narmesteleder.consumer.Naermesteleder

fun mapNarmesteLeder(naermesteleder: Naermesteleder): RSNaermesteLeder {
    return RSNaermesteLeder(
        virksomhetsnummer = naermesteleder.orgnummer,
        navn = naermesteleder.navn,
        epost = naermesteleder.epost,
        tlf = naermesteleder.mobil,
        erAktiv = naermesteleder.naermesteLederStatus.erAktiv,
        aktivFom = naermesteleder.naermesteLederStatus.aktivFom,
        aktivTom = naermesteleder.naermesteLederStatus.aktivTom,
        fnr = naermesteleder.naermesteLederFnr,
        samtykke = null,
        sistInnlogget = null
    )
}
