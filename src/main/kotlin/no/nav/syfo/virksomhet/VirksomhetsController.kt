package no.nav.syfo.virksomhet

import no.nav.security.token.support.core.api.ProtectedWithClaims
import no.nav.syfo.metric.Metric
import no.nav.syfo.api.auth.OIDCIssuer.EKSTERN
import no.nav.syfo.virksomhet.consumer.OrganisasjonConsumer
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import javax.inject.Inject

@RestController
@ProtectedWithClaims(issuer = EKSTERN)
@RequestMapping(value = ["/api/virksomhet/{virksomhetsnummer}"])
class VirksomhetsController @Inject constructor(
    private val metric: Metric,
    private val organisasjonConsumer: OrganisasjonConsumer
) {
    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun hentVirksomhet(@PathVariable("virksomhetsnummer") virksomhetsnummer: String): RSVirksomhet {
        metric.countEndpointRequest("hentVirksomhet")
        return RSVirksomhet(
            virksomhetsnummer = virksomhetsnummer,
            navn = organisasjonConsumer.hentNavn(virksomhetsnummer)
        )
    }
}
