package no.nav.syfo.kontaktinfo.controller

data class RSKontaktinfo(
    val fnr: String,
    val epost: String? = null,
    val tlf: String? = null,
    val skalHaVarsel: Boolean,
    val feilAarsak: FeilAarsak? = null
)

enum class FeilAarsak {
    RESERVERT, UTGAATT, KONTAKTINFO_IKKE_FUNNET, SIKKERHETSBEGRENSNING, PERSON_IKKE_FUNNET
}
