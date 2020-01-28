package no.nav.syfo.services;

import no.nav.syfo.tilgang.BrukerTilgangConsumer;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service
public class TilgangskontrollService {

    private final AktoerService aktoerService;
    private final BrukerTilgangConsumer brukerTilgangConsumer;
    private final NaermesteLederService naermesteLederService;

    @Inject
    public TilgangskontrollService(
            AktoerService aktoerService,
            BrukerTilgangConsumer brukerTilgangConsumer,
            NaermesteLederService naermesteLederService
    ) {
        this.aktoerService = aktoerService;
        this.brukerTilgangConsumer = brukerTilgangConsumer;
        this.naermesteLederService = naermesteLederService;
    }

    public boolean sporOmNoenAndreEnnSegSelvEllerEgneAnsatteEllerLedere(String innloggetFnr, String oppslattFnr) {
        return !(sporInnloggetBrukerOmSegSelv(innloggetFnr, oppslattFnr) || sporInnloggetBrukerOmEnAnsatt(oppslattFnr) || sporInnloggetBrukerOmEnLeder(innloggetFnr, oppslattFnr));
    }


    public boolean sporOmNoenAndreEnnSegSelvEllerEgneAnsatte(String innloggetFnr, String oppslattFnr) {
        return !(sporInnloggetBrukerOmSegSelv(innloggetFnr, oppslattFnr) || sporInnloggetBrukerOmEnAnsatt(oppslattFnr));
    }

    private boolean sporInnloggetBrukerOmEnAnsatt(String oppslattFnr) {
        return brukerTilgangConsumer.hasAccessToAnsatt(oppslattFnr);
    }

    private boolean sporInnloggetBrukerOmEnLeder(String innloggetFnr, String oppslattFnr) {
        String innloggetAktorId = aktoerService.hentAktoerIdForFnr(innloggetFnr);
        String oppslaattAktorId = aktoerService.hentAktoerIdForFnr(oppslattFnr);
        return naermesteLederService.hentNaermesteLederAktoerIdListe(innloggetAktorId).stream()
                .anyMatch(oppslaattAktorId::equals);
    }

    private boolean sporInnloggetBrukerOmSegSelv(String innloggetFnr, String fnr) {
        return fnr.equals(innloggetFnr);
    }
}
