package no.nav.syfo.arbeidsforhold.aareg

import no.nav.syfo.arbeidsforhold.controller.RSStilling
import no.nav.syfo.api.exception.Feilmelding.Feil
import no.nav.syfo.api.exception.SyfoException
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.binding.*
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.informasjon.arbeidsforhold.*
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.meldinger.FinnArbeidsforholdPrArbeidstakerRequest
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import javax.inject.Inject
import javax.xml.datatype.XMLGregorianCalendar

@Service
class ArbeidsforholdConsumer @Inject constructor(
    private val arbeidsforholdV3: ArbeidsforholdV3
) {
    companion object {
        private val LOG = LoggerFactory.getLogger(ArbeidsforholdConsumer::class.java)
        private val A_ORDNINGEN = Regelverker()

        init {
            A_ORDNINGEN.value = "A_ORDNINGEN"
        }
    }

    @Cacheable(cacheNames = ["arbeidsforholdByFnrAndVirksomhet"], key = "#fnr.concat(#virksomhetsnummer)", condition = "#fnr != null && #virksomhetsnummer != null")
    fun hentBrukersArbeidsforholdHosVirksomhet(
        fnr: String,
        virksomhetsnummer: String,
        fom: String
    ): List<RSStilling> {
        if (!fnr.matches(Regex("\\d{11}$")) || !virksomhetsnummer.matches(Regex("\\d{9}$"))) {
            LOG.error("Prøvde å arbeidsforhold for bruker {} i virksomhet {}", fnr, virksomhetsnummer)
            throw RuntimeException()
        }
        try {
            val request = lagArbeidsforholdRequest(fnr)
            val response = arbeidsforholdV3.finnArbeidsforholdPrArbeidstaker(request)
            return mapToRSStilingList(
                fom,
                virksomhetsnummer,
                response.arbeidsforhold
            )
        } catch (e: FinnArbeidsforholdPrArbeidstakerUgyldigInput) {
            LOG.error("Feil ved henting av arbeidsforhold", e)
            throw SyfoException(Feil.ARBEIDSFORHOLD_UGYLDIG_INPUT)
        } catch (e: FinnArbeidsforholdPrArbeidstakerSikkerhetsbegrensning) {
            LOG.error("Feil ved henting av arbeidsforhold", e)
            throw SyfoException(Feil.ARBEIDSFORHOLD_INGEN_TILGANG)
        } catch (e: RuntimeException) {
            LOG.error("Feil ved henting av arbeidsforhold", e)
            throw SyfoException(Feil.ARBEIDSFORHOLD_GENERELL_FEIL)
        }
    }

    private fun mapToRSStilingList(
        fom: String,
        virksomhetsnummer: String,
        arbebeidsforholdList: List<Arbeidsforhold>
    ): List<RSStilling> {
        return arbebeidsforholdList
            .filter {
                arbeidsforhold: Arbeidsforhold -> arbeidsforhold.arbeidsgiver is Organisasjon
            }
            .filter {
                arbeidsforhold: Arbeidsforhold -> (arbeidsforhold.arbeidsgiver as Organisasjon).orgnummer == virksomhetsnummer
            }
            .filter {
                arbeidsforhold: Arbeidsforhold -> arbeidsforhold.ansettelsesPeriode.periode.tom == null || !tilLocalDate(arbeidsforhold.ansettelsesPeriode.periode.tom).isBefore(formaterFom(fom))
            }.map { arbeidsforhold: Arbeidsforhold ->
                arbeidsforhold.arbeidsavtale.map { arbeidsavtale: Arbeidsavtale ->
                        RSStilling(
                            virksomhetsnummer = virksomhetsnummer,
                            yrke = arbeidsavtale.yrke.value,
                            prosent = arbeidsavtale.stillingsprosent,
                            fom = tilLocalDate(arbeidsforhold.ansettelsesPeriode.periode.fom),
                            tom = if (arbeidsforhold.ansettelsesPeriode.periode.tom != null) tilLocalDate(arbeidsforhold.ansettelsesPeriode.periode.tom) else null
                        )
                    }
            }.flatten()
    }

    private fun tilLocalDate(xmlGregorianCalendar: XMLGregorianCalendar): LocalDate {
        return xmlGregorianCalendar.toGregorianCalendar().toZonedDateTime().toLocalDate()
    }

    private fun lagArbeidsforholdRequest(fnr: String): FinnArbeidsforholdPrArbeidstakerRequest {
        val request = FinnArbeidsforholdPrArbeidstakerRequest()
        request.rapportertSomRegelverk = A_ORDNINGEN
        request.ident = ident(fnr)
        return request
    }

    private fun ident(fodselsnummer: String): NorskIdent {
        val ident = NorskIdent()
        ident.ident = fodselsnummer
        return ident
    }

    private fun formaterFom(fom: String): LocalDate {
        return try {
            val fomLocalDate = fom.split("T".toRegex()).toTypedArray()[0]
            LocalDate.parse(fomLocalDate, DateTimeFormatter.ISO_LOCAL_DATE)
        } catch (e: DateTimeParseException) {
            LOG.error("Feil ved henting av arbeidsforhold, ugyldig dato, {}", fom, e)
            throw SyfoException(Feil.ARBEIDSFORHOLD_UGYLDIG_INPUT)
        }
    }
}
