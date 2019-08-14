package localhost;

import no.nav.brukerdialog.security.domain.IdentType;
import no.nav.common.auth.SsoToken;
import no.nav.common.auth.Subject;

public class TestData {

    public static final String KJENT_IDENT = "11990022334";

    public static final Subject TEST_SUBJECT = new Subject(KJENT_IDENT, IdentType.EksternBruker, SsoToken.eksternOpenAM("test"));

}