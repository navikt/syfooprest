package no.nav.syfo.mock;

import no.nav.tjeneste.virksomhet.aktoer.v2.AktoerV2;
import no.nav.tjeneste.virksomhet.aktoer.v2.HentAktoerIdForIdentPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.aktoer.v2.HentIdentForAktoerIdPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.aktoer.v2.meldinger.*;

public class AktoerMock implements AktoerV2 {

    public WSHentAktoerIdForIdentResponse hentAktoerIdForIdent(WSHentAktoerIdForIdentRequest request) throws HentAktoerIdForIdentPersonIkkeFunnet {
        return new WSHentAktoerIdForIdentResponse()
                .withAktoerId("9990001112224");
    }

    public WSHentIdentForAktoerIdResponse hentIdentForAktoerId(WSHentIdentForAktoerIdRequest request) throws HentIdentForAktoerIdPersonIkkeFunnet {
        return new WSHentIdentForAktoerIdResponse().withIdent("11990022334");
    }

    public WSHentAktoerIdForIdentListeResponse hentAktoerIdForIdentListe(WSHentAktoerIdForIdentListeRequest request) {
        throw new RuntimeException("Denne er ikke implementert i mocken");
    }

    public WSHentIdentForAktoerIdListeResponse hentIdentForAktoerIdListe(WSHentIdentForAktoerIdListeRequest request) {
        throw new RuntimeException("Denne er ikke implementert i mocken");
    }

    public void ping() {
    }
}
