package no.nav.syfo.rest.feil;

import javax.ws.rs.core.Response.Status;

import static javax.ws.rs.core.Response.Status.*;

public class Feilmelding {

    static final String NO_BIGIP_5XX_REDIRECT = "X-Escape-5xx-Redirect";

    public enum Feil {
        ARBEIDSFORHOLD_UGYLDIG_INPUT (
                INTERNAL_SERVER_ERROR, "feilmelding.arbeidstaker.ugyldig.input"
        ),
        ARBEIDSFORHOLD_INGEN_TILGANG (
                INTERNAL_SERVER_ERROR, "feilmelding.arbeidstaker.sikkerhetsbegrensning"
        ),
        ARBEIDSFORHOLD_GENERELL_FEIL (
                INTERNAL_SERVER_ERROR, "feilmelding.arbeidstaker.generell.feil"
        ),
        GENERELL_FEIL (
                INTERNAL_SERVER_ERROR, "feilmelding.generell.feil"
        ),
        IKKE_FOEDSELSNUMMER (
                INTERNAL_SERVER_ERROR, "feilmelding.ikke.fnr"
        ),
        INGEN_AKTOER_ID (
                INTERNAL_SERVER_ERROR, "feilmelding.ingen.aktoer.id"
        ),
        AKTOER_IKKE_FUNNET (
                INTERNAL_SERVER_ERROR, "feilmelding.aktoer.ikke.funnet"
        );

        Status status;
        String id;
        Feil(Status status, String id) {
            this.status = status;
            this.id = id;
        }
    }

    private Feil feil;

    Feilmelding withFeil(final Feil feil) {
        this.feil = feil;
        return this;
    }

    @SuppressWarnings("unused")
    public String getId() {
        return feil.id;
    }
}
