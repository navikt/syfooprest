package no.nav.syfo.services;

import no.nav.syfo.tilgang.BrukerTilgangConsumer;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service
public class TilgangskontrollService {

    private final BrukerTilgangConsumer brukerTilgangConsumer;

    @Inject
    public TilgangskontrollService(
            BrukerTilgangConsumer brukerTilgangConsumer
    ) {
        this.brukerTilgangConsumer = brukerTilgangConsumer;
    }

    public boolean sporOmNoenAndreEnnSegSelvEllerEgneAnsatte(String innloggetFnr, String oppslattFnr) {
        return !(sporInnloggetBrukerOmSegSelv(innloggetFnr, oppslattFnr) || sporInnloggetBrukerOmEnAnsatt(oppslattFnr));
    }

    private boolean sporInnloggetBrukerOmEnAnsatt(String oppslattFnr) {
        return brukerTilgangConsumer.hasAccessToAnsatt(oppslattFnr);
    }

    private boolean sporInnloggetBrukerOmSegSelv(String innloggetFnr, String fnr) {
        return fnr.equals(innloggetFnr);
    }
}
