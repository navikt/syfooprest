package no.nav.syfo.rest.ressurser.mocks;

import no.nav.syfo.rest.domain.RSStilling;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static java.util.Arrays.asList;

public class ArbeidsforholdMock {

    public static final String VIRKSOMHETSNUMMER = "999666333";

    public static List<RSStilling> arbeidsforhold() {
        return asList(new RSStilling()
                .virksomhetsnummer(VIRKSOMHETSNUMMER)
                .yrke("Kontormedarbeider")
                .prosent(new BigDecimal(100.0))
                .fom(LocalDate.now().minusYears(4))
        );
    }
}
