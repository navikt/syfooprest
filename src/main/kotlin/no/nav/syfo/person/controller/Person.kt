package no.nav.syfo.person.controller

import java.time.LocalDateTime
import java.util.*

data class Person(
    var navn: String? = " ",
    var fnr: String? = null,
    var epost: String? = null,
    var tlf: String? = null,
    var sistInnlogget: LocalDateTime? = null,
    var samtykke: Boolean? = null,
    var evaluering: RSEvaluering? = null,
    var stillinger: List<Any>? = ArrayList()
)
