package no.nav.syfo.api.exception

import javax.ws.rs.core.Response

class Feilmelding {
    enum class Feil(var status: Response.Status, var id: String) {
        ARBEIDSFORHOLD_UGYLDIG_INPUT(Response.Status.INTERNAL_SERVER_ERROR, "feilmelding.arbeidstaker.ugyldig.input"
        ),
        ARBEIDSFORHOLD_INGEN_TILGANG(Response.Status.INTERNAL_SERVER_ERROR, "feilmelding.arbeidstaker.sikkerhetsbegrensning"
        ),
        ARBEIDSFORHOLD_GENERELL_FEIL(Response.Status.INTERNAL_SERVER_ERROR, "feilmelding.arbeidstaker.generell.feil"
        ),
        GENERELL_FEIL(Response.Status.INTERNAL_SERVER_ERROR, "feilmelding.generell.feil"
        ),
        IKKE_FOEDSELSNUMMER(Response.Status.INTERNAL_SERVER_ERROR, "feilmelding.ikke.fnr"
        ),
        INGEN_AKTOER_ID(Response.Status.INTERNAL_SERVER_ERROR, "feilmelding.ingen.aktoer.id"
        ),
        AKTOER_IKKE_FUNNET(Response.Status.INTERNAL_SERVER_ERROR, "feilmelding.aktoer.ikke.funnet"
        );
    }

    private var feil: Feil? = null
    fun withFeil(feil: Feil?): Feilmelding {
        this.feil = feil
        return this
    }

    val id: String
        get() = feil!!.id

    companion object {
        const val NO_BIGIP_5XX_REDIRECT = "X-Escape-5xx-Redirect"
    }
}
