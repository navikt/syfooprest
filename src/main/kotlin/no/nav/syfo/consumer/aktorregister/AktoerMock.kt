package no.nav.syfo.consumer.aktorregister

import no.nav.tjeneste.virksomhet.aktoer.v2.*
import no.nav.tjeneste.virksomhet.aktoer.v2.meldinger.*
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service

@Service
@ConditionalOnProperty(value = [AktorConfig.MOCK_KEY], havingValue = "true")
class AktoerMock : AktoerV2 {
    @Throws(HentAktoerIdForIdentPersonIkkeFunnet::class)
    override fun hentAktoerIdForIdent(request: WSHentAktoerIdForIdentRequest): WSHentAktoerIdForIdentResponse {
        return WSHentAktoerIdForIdentResponse()
            .withAktoerId(mockAktorId(request.ident))
    }

    @Throws(HentIdentForAktoerIdPersonIkkeFunnet::class)
    override fun hentIdentForAktoerId(request: WSHentIdentForAktoerIdRequest): WSHentIdentForAktoerIdResponse {
        return WSHentIdentForAktoerIdResponse().withIdent(getFnrFromMockedAktorId(request.aktoerId))
    }

    override fun hentAktoerIdForIdentListe(request: WSHentAktoerIdForIdentListeRequest): WSHentAktoerIdForIdentListeResponse {
        throw RuntimeException("Denne er ikke implementert i mocken")
    }

    override fun hentIdentForAktoerIdListe(request: WSHentIdentForAktoerIdListeRequest): WSHentIdentForAktoerIdListeResponse {
        throw RuntimeException("Denne er ikke implementert i mocken")
    }

    override fun ping() {}

    companion object {
        private const val MOCK_AKTORID_PREFIX = "10"
        @JvmStatic
        fun mockAktorId(fnr: String): String {
            return MOCK_AKTORID_PREFIX + fnr
        }

        private fun getFnrFromMockedAktorId(aktorId: String): String {
            return aktorId.replace(MOCK_AKTORID_PREFIX, "")
        }
    }
}
