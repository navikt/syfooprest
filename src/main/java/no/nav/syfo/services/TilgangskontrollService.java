package no.nav.syfo.services;

import no.nav.syfo.narmesteleder.Naermesteleder;
import no.nav.syfo.narmesteleder.NarmesteLederConsumer;
import no.nav.syfo.tilgang.BrukerTilgangConsumer;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Optional;

@Service
public class TilgangskontrollService {

    private final AktoerService aktoerService;
    private final BrukerTilgangConsumer brukerTilgangConsumer;
    private final NarmesteLederConsumer narmesteLederConsumer;

    @Inject
    public TilgangskontrollService(
            AktoerService aktoerService,
            BrukerTilgangConsumer brukerTilgangConsumer,
            NarmesteLederConsumer narmesteLederConsumer
    ) {
        this.aktoerService = aktoerService;
        this.brukerTilgangConsumer = brukerTilgangConsumer;
        this.narmesteLederConsumer = narmesteLederConsumer;
    }

    public boolean sporOmNoenAndreEnnSegSelvEllerEgneAnsatteEllerLedere(String innloggetFnr, String oppslattFnr, String virksomhetsnummer) {
        return !(sporInnloggetBrukerOmSegSelv(innloggetFnr, oppslattFnr) || sporInnloggetBrukerOmEnAnsatt(oppslattFnr) || sporInnloggetBrukerOmEnLeder(innloggetFnr, oppslattFnr, virksomhetsnummer));
    }

    public boolean sporOmNoenAndreEnnSegSelvEllerEgneAnsatte(String innloggetFnr, String oppslattFnr) {
        return !(sporInnloggetBrukerOmSegSelv(innloggetFnr, oppslattFnr) || sporInnloggetBrukerOmEnAnsatt(oppslattFnr));
    }

    private boolean sporInnloggetBrukerOmEnAnsatt(String oppslattFnr) {
        return brukerTilgangConsumer.hasAccessToAnsatt(oppslattFnr);
    }

    private boolean sporInnloggetBrukerOmEnLeder(String innloggetFnr, String oppslattFnr, String virksomhetsnummer) {
        Optional<Naermesteleder> lederForInnloggetBruker = narmesteLederConsumer.narmesteLeder(innloggetFnr, virksomhetsnummer);

        if (lederForInnloggetBruker.isPresent()) {
            String lederForInnloggetBrukerFnr = aktoerService.hentFnrForAktoer(lederForInnloggetBruker.get().naermesteLederAktoerId);
            return oppslattFnr.equals(lederForInnloggetBrukerFnr);
        } else {
            return false;
        }
    }

    private boolean sporInnloggetBrukerOmSegSelv(String innloggetFnr, String fnr) {
        return fnr.equals(innloggetFnr);
    }
}
