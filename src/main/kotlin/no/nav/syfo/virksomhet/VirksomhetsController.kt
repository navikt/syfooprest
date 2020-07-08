package no.nav.syfo.virksomhet

import no.nav.security.oidc.api.ProtectedWithClaims
import no.nav.syfo.metric.Metric
import no.nav.syfo.oidc.OIDCIssuer.EKSTERN
import no.nav.syfo.service.OrganisasjonService
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import javax.inject.Inject

@RestController
@ProtectedWithClaims(issuer = EKSTERN)
@RequestMapping(value = ["/api/virksomhet/{virksomhetsnummer}"])
class VirksomhetsController @Inject constructor(
    private val metric: Metric,
    private val organisasjonService: OrganisasjonService
) {
    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun hentVirksomhet(@PathVariable("virksomhetsnummer") virksomhetsnummer: String): RSVirksomhet {
        metric.countEndpointRequest("hentVirksomhet")
        return RSVirksomhet(
            virksomhetsnummer = virksomhetsnummer,
            navn = organisasjonService.hentNavn(virksomhetsnummer)
        )
    }
}
