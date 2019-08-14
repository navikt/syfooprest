package no.nav.syfo.services;

import no.nav.syfo.rest.domain.RSStilling;
import no.nav.syfo.rest.feil.SyfoException;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.binding.ArbeidsforholdV3;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.binding.FinnArbeidsforholdPrArbeidstakerSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.binding.FinnArbeidsforholdPrArbeidstakerUgyldigInput;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.informasjon.arbeidsforhold.NorskIdent;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.informasjon.arbeidsforhold.Organisasjon;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.informasjon.arbeidsforhold.Regelverker;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.meldinger.FinnArbeidsforholdPrArbeidstakerRequest;
import org.slf4j.Logger;
import org.springframework.cache.annotation.Cacheable;

import javax.inject.Inject;
import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collection;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static no.nav.common.auth.SubjectHandler.getIdent;
import static no.nav.syfo.rest.feil.Feilmelding.Feil.*;
import static org.slf4j.LoggerFactory.getLogger;

public class ArbeidsforholdService {
    private static final Logger LOG = getLogger(ArbeidsforholdService.class);
    private static final Regelverker A_ORDNINGEN = new Regelverker();

    static {
        A_ORDNINGEN.setValue("A_ORDNINGEN");
    }

    @Inject
    private ArbeidsforholdV3 arbeidsforholdV3;

    @Cacheable(value = "arbeidsforhold", keyGenerator = "userkeygenerator")
    public List<RSStilling> hentBrukersArbeidsforholdHosVirksomhet(String fnr, String virksomhetsnummer, String fom) {
        if (!fnr.matches("\\d{11}$") || !virksomhetsnummer.matches("\\d{9}$")) {
            LOG.error("{} prøvde å arbeidsforhold for bruker {} i virksomhet {}", getIdent(), fnr, virksomhetsnummer);
            throw new RuntimeException();
        }
        try {
            FinnArbeidsforholdPrArbeidstakerRequest request = lagArbeidsforholdRequest(fnr);
            return arbeidsforholdV3.finnArbeidsforholdPrArbeidstaker(request).getArbeidsforhold().stream()
                    .filter(arbeidsforhold -> arbeidsforhold.getArbeidsgiver() instanceof Organisasjon)
                    .filter(arbeidsforhold -> ((Organisasjon) arbeidsforhold.getArbeidsgiver()).getOrgnummer().equals(virksomhetsnummer))
                    .filter(arbeidsforhold -> arbeidsforhold.getAnsettelsesPeriode().getPeriode().getTom() == null || !tilLocalDate(arbeidsforhold.getAnsettelsesPeriode().getPeriode().getTom()).isBefore(formaterFom(fom)))
                    .map(arbeidsforhold -> arbeidsforhold.getArbeidsavtale().stream()
                            .map(arbeidsavtale -> new RSStilling()
                                    .virksomhetsnummer(virksomhetsnummer)
                                    .yrke(arbeidsavtale.getYrke().getValue())
                                    .prosent(arbeidsavtale.getStillingsprosent())
                                    .fom(tilLocalDate(arbeidsforhold.getAnsettelsesPeriode().getPeriode().getFom()))
                                    .tom(arbeidsforhold.getAnsettelsesPeriode().getPeriode().getTom() != null ? tilLocalDate(arbeidsforhold.getAnsettelsesPeriode().getPeriode().getTom()) : null)
                            ).collect(toList()))
                    .flatMap(Collection::stream)
                    .collect(toList());
        } catch (FinnArbeidsforholdPrArbeidstakerUgyldigInput e) {
            LOG.error("Feil ved henting av arbeidsforhold", e);
            throw new SyfoException(ARBEIDSFORHOLD_UGYLDIG_INPUT);
        } catch (FinnArbeidsforholdPrArbeidstakerSikkerhetsbegrensning e) {
            LOG.error("Feil ved henting av arbeidsforhold", e);
            throw new SyfoException(ARBEIDSFORHOLD_INGEN_TILGANG);
        } catch (RuntimeException e) {
            LOG.error("Feil ved henting av arbeidsforhold", e);
            throw new SyfoException(ARBEIDSFORHOLD_GENERELL_FEIL);
        }
    }

    private LocalDate tilLocalDate(XMLGregorianCalendar xmlGregorianCalendar) {
        return xmlGregorianCalendar.toGregorianCalendar().toZonedDateTime().toLocalDate();
    }

    private FinnArbeidsforholdPrArbeidstakerRequest lagArbeidsforholdRequest(String fnr) {
        FinnArbeidsforholdPrArbeidstakerRequest request = new FinnArbeidsforholdPrArbeidstakerRequest();
        request.setRapportertSomRegelverk(A_ORDNINGEN);
        request.setIdent(ident(fnr));
        return request;
    }

    private NorskIdent ident(String fodselsnummer) {
        NorskIdent ident = new NorskIdent();
        ident.setIdent(fodselsnummer);
        return ident;
    }

    private LocalDate formaterFom(String fom) {
        try {
            String fomLocalDate = fom.split("T")[0];
            return LocalDate.parse(fomLocalDate, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException e) {
            LOG.error("Feil ved henting av arbeidsforhold, ugyldig dato, {}", fom, e);
            throw new SyfoException(ARBEIDSFORHOLD_UGYLDIG_INPUT);
        }
    }
}
