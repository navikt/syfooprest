package no.nav.syfo.mock

import no.nav.syfo.config.AAregConfig
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.binding.*
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.informasjon.arbeidsforhold.Arbeidsforhold
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.informasjon.arbeidsforhold.Organisasjon
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.meldinger.*
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service

@Service
@ConditionalOnProperty(value = [AAregConfig.MOCK_KEY], havingValue = "true")
class ArbeidsforholdMock : ArbeidsforholdV3 {
    @Throws(FinnArbeidsforholdPrArbeidsgiverForMangeForekomster::class, FinnArbeidsforholdPrArbeidsgiverSikkerhetsbegrensning::class, FinnArbeidsforholdPrArbeidsgiverUgyldigInput::class)
    override fun finnArbeidsforholdPrArbeidsgiver(parameters: FinnArbeidsforholdPrArbeidsgiverRequest): FinnArbeidsforholdPrArbeidsgiverResponse {
        throw RuntimeException("Ikke implementert i mock. Se ArbeidsforholdMock")
    }

    @Throws(FinnArbeidsforholdPrArbeidstakerSikkerhetsbegrensning::class, FinnArbeidsforholdPrArbeidstakerUgyldigInput::class)
    override fun finnArbeidsforholdPrArbeidstaker(parameters: FinnArbeidsforholdPrArbeidstakerRequest): FinnArbeidsforholdPrArbeidstakerResponse {
        val arbeidsforhold = Arbeidsforhold()
        val arbeidsgiver = Organisasjon()
        arbeidsgiver.orgnummer = "111222333"
        arbeidsforhold.arbeidsgiver = arbeidsgiver
        val response = FinnArbeidsforholdPrArbeidstakerResponse()
        response.arbeidsforhold.add(arbeidsforhold)
        return response
    }

    @Throws(HentArbeidsforholdHistorikkArbeidsforholdIkkeFunnet::class, HentArbeidsforholdHistorikkSikkerhetsbegrensning::class)
    override fun hentArbeidsforholdHistorikk(parameters: HentArbeidsforholdHistorikkRequest): HentArbeidsforholdHistorikkResponse {
        throw RuntimeException("Ikke implementert i mock. Se ArbeidsforholdMock")
    }

    @Throws(FinnArbeidstakerePrArbeidsgiverSikkerhetsbegrensning::class, FinnArbeidstakerePrArbeidsgiverUgyldigInput::class)
    override fun finnArbeidstakerePrArbeidsgiver(parameters: FinnArbeidstakerePrArbeidsgiverRequest): FinnArbeidstakerePrArbeidsgiverResponse {
        throw RuntimeException("Ikke implementert i mock. Se ArbeidsforholdMock")
    }

    override fun ping() {}
}
