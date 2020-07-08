package no.nav.syfo.testhelper

import no.nav.syfo.consumer.aktorregister.AktoerMock.Companion.mockAktorId

object UserConstants {
    const val ARBEIDSTAKER_FNR = "12345678912"
    val ARBEIDSTAKER_AKTORID = mockAktorId(ARBEIDSTAKER_FNR)
    const val LEDER_FNR = "12987654321"
    val LEDER_AKTORID = mockAktorId(LEDER_FNR)
    const val VIRKSOMHETSNUMMER = "123456789"
}
