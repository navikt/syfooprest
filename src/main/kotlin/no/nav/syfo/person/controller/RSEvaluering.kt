package no.nav.syfo.person.controller

data class RSEvaluering(
    val effekt: String? = null,
    val hvorfor: String? = null,
    val videre: String? = null,
    val interneaktiviteter: Boolean = false,
    val ekstratid: Boolean = false,
    val bistand: Boolean = false,
    val ingen: Boolean = false
)
