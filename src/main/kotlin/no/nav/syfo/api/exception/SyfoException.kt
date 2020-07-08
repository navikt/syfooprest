package no.nav.syfo.api.exception

import no.nav.syfo.api.exception.Feilmelding.Feil
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import javax.ws.rs.ext.ExceptionMapper
import javax.ws.rs.ext.Provider

@Provider
class SyfoException(
    private var feil: Feil
) : RuntimeException(), ExceptionMapper<SyfoException> {

    override fun toResponse(e: SyfoException): Response {
        val melding = Feilmelding().withFeil(e.feil)
        return Response
            .status(e.status())
            .entity(melding)
            .type(MediaType.APPLICATION_JSON)
            .header(Feilmelding.NO_BIGIP_5XX_REDIRECT, true)
            .build()
    }

    private fun status(): Int {
        return feil.status.statusCode
    }
}
