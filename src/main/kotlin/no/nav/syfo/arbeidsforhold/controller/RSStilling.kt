package no.nav.syfo.arbeidsforhold.controller

import java.math.BigDecimal
import java.time.LocalDate

data class RSStilling(
    val virksomhetsnummer: String? = null,
    val yrke: String? = null,
    val prosent: BigDecimal? = null,
    val fom: LocalDate? = null,
    val tom: LocalDate? = null
)
