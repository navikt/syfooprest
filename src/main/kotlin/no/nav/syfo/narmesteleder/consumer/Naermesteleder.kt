package no.nav.syfo.narmesteleder.consumer

data class Naermesteleder(
    val naermesteLederId: Long?,
    val naermesteLederFnr: String,
    val orgnummer: String,
    val naermesteLederStatus: NaermesteLederStatus,
    val navn: String,
    val mobil: String?,
    val epost: String?
)
