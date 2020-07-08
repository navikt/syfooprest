package no.nav.syfo.service

import no.nav.tjeneste.virksomhet.organisasjon.v4.*
import no.nav.tjeneste.virksomhet.organisasjon.v4.informasjon.WSUstrukturertNavn
import no.nav.tjeneste.virksomhet.organisasjon.v4.meldinger.WSHentOrganisasjonRequest
import org.apache.commons.lang3.StringUtils
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import java.util.stream.Collectors
import javax.inject.Inject

@Service
class OrganisasjonService @Inject constructor(
    private val organisasjonV4: OrganisasjonV4
) {
    @Cacheable(cacheNames = ["virksomhetsnavnByNr"], key = "#orgnr", condition = "#orgnr != null")
    fun hentNavn(orgnr: String): String {
        if (!orgnr.matches(Regex("\\d{9}$"))) {
            log.error("Prøvde å hente orgnavn med orgnr {}", orgnr)
            throw RuntimeException()
        }
        try {
            val response = organisasjonV4.hentOrganisasjon(request(orgnr))
            val ustrukturertNavn = response.organisasjon.navn as WSUstrukturertNavn
            return ustrukturertNavn.navnelinje.stream()
                .filter { cs: String? -> StringUtils.isNotBlank(cs) }
                .collect(Collectors.joining(", "))
        } catch (e: HentOrganisasjonOrganisasjonIkkeFunnet) {
            log.warn("Kunne ikke hente organisasjon for {}", orgnr, e)
        } catch (e: HentOrganisasjonUgyldigInput) {
            log.warn("Ugyldig input for {}", orgnr, e)
        } catch (e: RuntimeException) {
            log.error("Fikk en runtimefeil mot organisasjon med orgnr {}", orgnr)
        }
        return "Fant ikke navn"
    }

    private fun request(orgnr: String): WSHentOrganisasjonRequest {
        return WSHentOrganisasjonRequest()
            .withOrgnummer(orgnr)
            .withInkluderHierarki(false)
            .withInkluderHistorikk(false)
    }

    companion object {
        private val log = LoggerFactory.getLogger(OrganisasjonService::class.java)
    }
}
