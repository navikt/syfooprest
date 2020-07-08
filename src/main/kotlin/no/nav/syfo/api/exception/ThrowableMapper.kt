package no.nav.syfo.api.exception

import no.nav.syfo.api.exception.Feilmelding.Feil
import org.slf4j.LoggerFactory
import javax.ws.rs.WebApplicationException
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import javax.ws.rs.ext.ExceptionMapper
import javax.ws.rs.ext.Provider

@Provider
class ThrowableMapper : ExceptionMapper<Throwable> {
    override fun toResponse(e: Throwable): Response {
        LOG.error("Uventet feil", e)
        return Response.status(statuskode(e))
            .type(MediaType.APPLICATION_JSON)
            .entity(Feilmelding().withFeil(Feil.GENERELL_FEIL))
            .header(Feilmelding.NO_BIGIP_5XX_REDIRECT, true)
            .build()
    }

    private fun statuskode(e: Throwable): Int {
        return if (e is WebApplicationException) {
            e.response.status
        } else {
            Feil.GENERELL_FEIL.status.statusCode
        }
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(ThrowableMapper::class.java)
    }
}
