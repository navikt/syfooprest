package no.nav.syfo.services;

import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service
public class TilgangskontrollService {

    private final AktoerService aktoerService;
    private final NaermesteLederService naermesteLederService;

    @Inject
    public TilgangskontrollService(
            AktoerService aktoerService,
            NaermesteLederService naermesteLederService
    ) {
        this.aktoerService = aktoerService;
        this.naermesteLederService = naermesteLederService;
    }

    public boolean sporOmNoenAndreEnnSegSelvEllerEgneAnsatteEllerLedere(String innloggetFnr, String oppslaattAktoerId) {
        String oppslaattFnr = aktoerService.hentFnrForAktoer(oppslaattAktoerId);
        return !(sporInnloggetBrukerOmSegSelv(innloggetFnr, oppslaattFnr) || sporInnloggetBrukerOmEnAnsatt(innloggetFnr, oppslaattAktoerId) || sporInnloggetBrukerOmEnLeder(innloggetFnr, oppslaattAktoerId));
    }


    public boolean sporOmNoenAndreEnnSegSelvEllerEgneAnsatte(String innloggetFnr, String oppslaattAktoerId) {
        String oppslaattFnr = aktoerService.hentFnrForAktoer(oppslaattAktoerId);
        return !(sporInnloggetBrukerOmSegSelv(innloggetFnr, oppslaattFnr) || sporInnloggetBrukerOmEnAnsatt(innloggetFnr, oppslaattAktoerId));
    }

    private boolean sporInnloggetBrukerOmEnAnsatt(String innloggetFnr, String oppslaattAktoerId) {
        String innloggetAktoerId = aktoerService.hentAktoerIdForFnr(innloggetFnr);
        return naermesteLederService.hentAnsatteAktorId(innloggetAktoerId).stream()
                .anyMatch(oppslaattAktoerId::equals);
    }

    private boolean sporInnloggetBrukerOmEnLeder(String innloggetFnr, String oppslaattAktoerId) {
        String innloggetAktoerId = aktoerService.hentAktoerIdForFnr(innloggetFnr);
        return naermesteLederService.hentNaermesteLederAktoerIdListe(innloggetAktoerId).stream()
                .anyMatch(oppslaattAktoerId::equals);
    }

    private boolean sporInnloggetBrukerOmSegSelv(String innloggetFnr, String fnr) {
        return fnr.equals(innloggetFnr);
    }
}
