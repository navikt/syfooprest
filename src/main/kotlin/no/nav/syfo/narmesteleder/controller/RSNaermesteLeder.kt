package no.nav.syfo.narmesteleder.controller

import no.nav.syfo.rest.domain.RSEvaluering
import java.time.LocalDate
import java.time.LocalDateTime

data class RSNaermesteLeder(
        val virksomhetsnummer: String,
        val erAktiv: Boolean,
        val aktivFom: LocalDate,
        val aktivTom: LocalDate?,
        val navn: String = " ",
        val fnr: String,
        val epost: String?,
        val tlf: String?,
        val sistInnlogget: LocalDateTime?,
        val samtykke: Boolean?,
        val evaluering: RSEvaluering?
)
