package no.nav.syfo.kontaktinfo.consumer

import no.nav.syfo.consumer.aktorregister.AktorregisterConsumer
import no.nav.syfo.kontaktinfo.controller.FeilAarsak
import no.nav.syfo.kontaktinfo.controller.RSKontaktinfo
import no.nav.syfo.util.convertToOffsetDateTime
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.*
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.informasjon.WSEpostadresse
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.informasjon.WSMobiltelefonnummer
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.meldinger.WSHentDigitalKontaktinformasjonRequest
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import java.time.OffsetDateTime
import java.util.*
import javax.inject.Inject
import javax.xml.datatype.XMLGregorianCalendar

@Service
class DkifConsumer @Inject constructor(
    private val dkifV1: DigitalKontaktinformasjonV1,
    private val aktorregisterConsumer: AktorregisterConsumer
) {
    @Cacheable(cacheNames = ["kontaktinfoByFnr"], key = "#fnr", condition = "#fnr != null")
    fun hentRSKontaktinfoFnr(fnr: String): RSKontaktinfo {
        if (!fnr.matches(Regex("\\d{11}$"))) {
            log.error("Prøvde å hente kontaktinfo med fnr")
            throw RuntimeException()
        }
        return try {
            val response = dkifV1.hentDigitalKontaktinformasjon(WSHentDigitalKontaktinformasjonRequest().withPersonident(fnr)).digitalKontaktinformasjon
            if ("true".equals(response.reservasjon, ignoreCase = true)) {
                return RSKontaktinfo(
                    fnr = fnr,
                    skalHaVarsel = false,
                    feilAarsak = FeilAarsak.RESERVERT
                )
            }
            if (!harVerfisertSiste18Mnd(response.epostadresse, response.mobiltelefonnummer)) {
                RSKontaktinfo(
                    fnr = fnr,
                    skalHaVarsel = false,
                    feilAarsak = FeilAarsak.UTGAATT
                )
            } else RSKontaktinfo(
                fnr = fnr,
                skalHaVarsel = true,
                epost = if (response.epostadresse != null) response.epostadresse.value else "",
                tlf = if (response.mobiltelefonnummer != null) response.mobiltelefonnummer.value else ""
            )
        } catch (e: HentDigitalKontaktinformasjonKontaktinformasjonIkkeFunnet) {
            RSKontaktinfo(
                fnr = fnr,
                skalHaVarsel = false,
                feilAarsak = FeilAarsak.KONTAKTINFO_IKKE_FUNNET
            )
        } catch (e: HentDigitalKontaktinformasjonSikkerhetsbegrensing) {
            RSKontaktinfo(
                fnr = fnr,
                skalHaVarsel = false,
                feilAarsak = FeilAarsak.SIKKERHETSBEGRENSNING
            )
        } catch (e: HentDigitalKontaktinformasjonPersonIkkeFunnet) {
            RSKontaktinfo(
                fnr = fnr,
                skalHaVarsel = false,
                feilAarsak = FeilAarsak.PERSON_IKKE_FUNNET
            )
        } catch (e: RuntimeException) {
            log.error("Fikk en runtimefeil mot DKIF med fnr", e)
            throw e
        }
    }

    fun harVerfisertSiste18Mnd(epostadresse: WSEpostadresse?, mobiltelefonnummer: WSMobiltelefonnummer?): Boolean {
        return harVerifisertEpostSiste18Mnd(epostadresse) && harVerifisertMobilSiste18Mnd(mobiltelefonnummer)
    }

    private fun harVerifisertEpostSiste18Mnd(epostadresse: WSEpostadresse?): Boolean {
        return Optional.ofNullable(epostadresse)
            .map { obj: WSEpostadresse -> obj.sistVerifisert }
            .filter { sistVerifisertEpost: XMLGregorianCalendar -> convertToOffsetDateTime(sistVerifisertEpost).isAfter(OffsetDateTime.now().minusMonths(18)) }
            .isPresent
    }

    private fun harVerifisertMobilSiste18Mnd(mobiltelefonnummer: WSMobiltelefonnummer?): Boolean {
        return Optional.ofNullable(mobiltelefonnummer)
            .map { obj: WSMobiltelefonnummer -> obj.sistVerifisert }
            .filter { sistVerifisertEpost: XMLGregorianCalendar -> convertToOffsetDateTime(sistVerifisertEpost).isAfter(OffsetDateTime.now().minusMonths(18)) }
            .isPresent
    }

    @Cacheable(cacheNames = ["kontaktinfoByAktorId"], key = "#aktoerId", condition = "#aktoerId != null")
    fun hentRSKontaktinfoAktoerId(aktoerId: String): RSKontaktinfo {
        return hentRSKontaktinfoFnr(aktorregisterConsumer.hentFnrForAktor(aktoerId))
    }

    companion object {
        private val log = LoggerFactory.getLogger(DkifConsumer::class.java)
    }
}
