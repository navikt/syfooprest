package no.nav.syfo.narmesteleder.consumer

import java.time.LocalDate

data class NaermesteLederStatus(
    val erAktiv: Boolean,
    val aktivFom: LocalDate,
    val aktivTom: LocalDate?
)
