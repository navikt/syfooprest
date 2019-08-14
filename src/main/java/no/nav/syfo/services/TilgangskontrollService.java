package no.nav.syfo.services;

import javax.inject.Inject;

import static no.nav.common.auth.SubjectHandler.getIdent;


public class TilgangskontrollService {

    @Inject
    private AktoerService aktoerService;
    @Inject
    private NaermesteLederService naermesteLederService;

    public boolean sporOmNoenAndreEnnSegSelvEllerEgneAnsatteEllerLedere(String oppslaattAktoerId) {
        String innloggetIdent = getIdent().orElseThrow(IllegalArgumentException::new);
        String oppslaattFnr = aktoerService.hentFnrForAktoer(oppslaattAktoerId);
        return !(sporInnloggetBrukerOmSegSelv(innloggetIdent, oppslaattFnr) || sporInnloggetBrukerOmEnAnsatt(innloggetIdent, oppslaattAktoerId) || sporInnloggetBrukerOmEnLeder(innloggetIdent, oppslaattAktoerId));
    }


    public boolean sporOmNoenAndreEnnSegSelvEllerEgneAnsatte(String oppslaattAktoerId) {
        String innloggetIdent = getIdent().orElseThrow(IllegalArgumentException::new);
        String oppslaattFnr = aktoerService.hentFnrForAktoer(oppslaattAktoerId);
        return !(sporInnloggetBrukerOmSegSelv(innloggetIdent, oppslaattFnr) || sporInnloggetBrukerOmEnAnsatt(innloggetIdent, oppslaattAktoerId));
    }

    private boolean sporInnloggetBrukerOmEnAnsatt(String innloggetIdent, String oppslaattAktoerId) {
        String innloggetAktoerId = aktoerService.hentAktoerIdForFnr(innloggetIdent);
        return naermesteLederService.hentAnsatteAktorId(innloggetAktoerId).stream()
                .anyMatch(oppslaattAktoerId::equals);
    }

    private boolean sporInnloggetBrukerOmEnLeder(String innloggetIdent, String oppslaattAktoerId) {
        String innloggetAktoerId = aktoerService.hentAktoerIdForFnr(innloggetIdent);
        return naermesteLederService.hentNaermesteLederAktoerIdListe(innloggetAktoerId).stream()
                .anyMatch(oppslaattAktoerId::equals);
    }

    private boolean sporInnloggetBrukerOmSegSelv(String innloggetIdent, String fnr) {
        return fnr.equals(innloggetIdent);
    }
}
