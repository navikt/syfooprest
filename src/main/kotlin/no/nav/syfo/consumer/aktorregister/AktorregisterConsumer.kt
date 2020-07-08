package no.nav.syfo.consumer.aktorregister

import no.nav.syfo.rest.feil.Feilmelding.Feil
import no.nav.syfo.rest.feil.SyfoException
import no.nav.tjeneste.virksomhet.aktoer.v2.*
import no.nav.tjeneste.virksomhet.aktoer.v2.meldinger.WSHentAktoerIdForIdentRequest
import no.nav.tjeneste.virksomhet.aktoer.v2.meldinger.WSHentIdentForAktoerIdRequest
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import javax.inject.Inject

@Service
class AktorregisterConsumer @Inject constructor(
    private val aktoerV2: AktoerV2
) {
    @Cacheable(cacheNames = ["aktorByFnr"], key = "#fnr", condition = "#fnr != null")
    fun hentAktorIdForFnr(fnr: String): String {
        if (!fnr.matches(Regex("\\d{11}$"))) {
            LOG.error("Pprøvde å hente navn med fnr")
            throw RuntimeException()
        }
        return try {
            aktoerV2.hentAktoerIdForIdent(
                WSHentAktoerIdForIdentRequest()
                    .withIdent(fnr)
            ).aktoerId
        } catch (e: HentAktoerIdForIdentPersonIkkeFunnet) {
            LOG.warn("AktoerID ikke funnet for fødselsnummer!", e)
            throw SyfoException(Feil.AKTOER_IKKE_FUNNET)
        }
    }

    @Cacheable(cacheNames = ["aktorByAktorId"], key = "#aktorId", condition = "#aktorId != null")
    fun hentFnrForAktor(aktorId: String): String {
        if (!aktorId.matches(Regex("\\d{13}$"))) {
            LOG.error("Prøvde å hente navn med aktoerId {}", aktorId)
            throw RuntimeException()
        }
        return try {
            aktoerV2.hentIdentForAktoerId(
                WSHentIdentForAktoerIdRequest()
                    .withAktoerId(aktorId)
            ).ident
        } catch (e: HentIdentForAktoerIdPersonIkkeFunnet) {
            LOG.warn("FNR ikke funnet for aktoerId!", e)
            throw SyfoException(Feil.AKTOER_IKKE_FUNNET)
        }
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(AktorregisterConsumer::class.java)
    }
}
