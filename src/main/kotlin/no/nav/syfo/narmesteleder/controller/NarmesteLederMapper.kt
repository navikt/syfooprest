package no.nav.syfo.narmesteleder.controller

import no.nav.syfo.consumer.aktorregister.AktorregisterConsumer
import no.nav.syfo.narmesteleder.consumer.Naermesteleder
import org.springframework.stereotype.Component
import javax.inject.Inject

@Component
class NarmesteLederMapper @Inject constructor(private val aktorregisterConsumer: AktorregisterConsumer) {

    fun map(naermesteleder: Naermesteleder): RSNaermesteLeder {
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
}
