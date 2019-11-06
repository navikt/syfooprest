package no.nav.syfo.testhelper;

import no.nav.syfo.mock.AktoerMock;

import static no.nav.syfo.mock.BrukerprofilMock.PERSON_ETTERNAVN;
import static no.nav.syfo.mock.BrukerprofilMock.PERSON_FORNAVN;

public class UserConstants {

    public static final String ARBEIDSTAKER_FNR = "12345678912";
    public static final String ARBEIDSTAKER_AKTORID = AktoerMock.mockAktorId(ARBEIDSTAKER_FNR);
    public static final String LEDER_FNR = "12987654321";
    public static final String LEDER_AKTORID = AktoerMock.mockAktorId(LEDER_FNR);
    public static final String VIRKSOMHETSNUMMER = "123456789";

    public final static String PERSON_NAVN = PERSON_FORNAVN + " " + PERSON_ETTERNAVN;
}
