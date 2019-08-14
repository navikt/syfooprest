package no.nav.syfo.mock;

import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.binding.*;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.informasjon.arbeidsforhold.Arbeidsforhold;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.informasjon.arbeidsforhold.Organisasjon;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.meldinger.*;

public class ArbeidsforholdMock implements ArbeidsforholdV3 {
    public FinnArbeidsforholdPrArbeidsgiverResponse finnArbeidsforholdPrArbeidsgiver(FinnArbeidsforholdPrArbeidsgiverRequest parameters)
            throws FinnArbeidsforholdPrArbeidsgiverForMangeForekomster, FinnArbeidsforholdPrArbeidsgiverSikkerhetsbegrensning, FinnArbeidsforholdPrArbeidsgiverUgyldigInput {
        throw new RuntimeException("Ikke implementert i mock. Se ArbeidsforholdMock");
    }

    public FinnArbeidsforholdPrArbeidstakerResponse finnArbeidsforholdPrArbeidstaker(FinnArbeidsforholdPrArbeidstakerRequest parameters)
            throws FinnArbeidsforholdPrArbeidstakerSikkerhetsbegrensning, FinnArbeidsforholdPrArbeidstakerUgyldigInput {
        Arbeidsforhold arbeidsforhold = new Arbeidsforhold();
        Organisasjon arbeidsgiver = new Organisasjon();
        arbeidsgiver.setOrgnummer("111222333");
        arbeidsforhold.setArbeidsgiver(arbeidsgiver);
        FinnArbeidsforholdPrArbeidstakerResponse response = new FinnArbeidsforholdPrArbeidstakerResponse();
        response.getArbeidsforhold().add(arbeidsforhold);
        return response;
    }

    public HentArbeidsforholdHistorikkResponse hentArbeidsforholdHistorikk(HentArbeidsforholdHistorikkRequest parameters)
            throws HentArbeidsforholdHistorikkArbeidsforholdIkkeFunnet, HentArbeidsforholdHistorikkSikkerhetsbegrensning {
        throw new RuntimeException("Ikke implementert i mock. Se ArbeidsforholdMock");
    }

    public FinnArbeidstakerePrArbeidsgiverResponse finnArbeidstakerePrArbeidsgiver(FinnArbeidstakerePrArbeidsgiverRequest parameters)
            throws FinnArbeidstakerePrArbeidsgiverSikkerhetsbegrensning, FinnArbeidstakerePrArbeidsgiverUgyldigInput {
        throw new RuntimeException("Ikke implementert i mock. Se ArbeidsforholdMock");
    }

    public void ping() {
    }
}
