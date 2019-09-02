import localhost.ApplicationConfigTest;
import no.nav.syfo.config.*;
import no.nav.testconfig.ApiAppTest;

import static no.nav.apiapp.ApiApp.startApp;
import static no.nav.sbl.util.EnvironmentUtils.Type.PUBLIC;
import static no.nav.sbl.util.EnvironmentUtils.setProperty;
import static no.nav.testconfig.ApiAppTest.setupTestContext;

public class MainTest {
    public static final String TEST_PORT = "8580";
    private static final String APPLICATION_NAME = "oppfoelgingsdialogrest";

    public static void main(String[] args) throws Exception {
        setupTestContext(ApiAppTest.Config.builder().applicationName(APPLICATION_NAME).build());

        setupWSTestProperties();
        setupMockTestProperties();

        String[] _args = {TEST_PORT};
        startApp(ApplicationConfigTest.class, _args);
    }

    private static void setupWSTestProperties() {
        setProperty(AAregConfig.ENDEPUNKT_URL_KEY, "https://modapp-t6.adeo.no/aareg-core/ArbeidsforholdService/v3", PUBLIC);
        setProperty(AktoerConfig.ENDEPUNKT_URL_KEY, "https://app-t10.adeo.no/aktoerid/AktoerService/v2", PUBLIC);
        setProperty(DkifConfig.ENDEPUNKT_URL_KEY, "https://app-t1.adeo.no/digital-kontaktinformasjon/DigitalKontaktinformasjon/v1", PUBLIC);
        setProperty(EregConfig.ENDEPUNKT_URL_KEY, "https://modapp-t1.adeo.no/ereg/ws/OrganisasjonService/v4", PUBLIC);
        setProperty(SykefravaersoppfoelgingV1Config.ENDEPUNKT_URL_KEY, "sykefravaersoppfoelging.endpoint", PUBLIC);
        setProperty(TpsConfig.ENDEPUNKT_URL_KEY, "https://wasapp-t1.adeo.no/tpsws/Brukerprofil_v3", PUBLIC);
    }

    private static void setupMockTestProperties() {
        setProperty("NO_NAV_MODIG_SECURITY_SYSTEMUSER.USERNAME", "BD03", PUBLIC);
        setProperty("NO_NAV_MODIG_SECURITY_SYSTEMUSER.PASSWORD", "CHANGEME", PUBLIC);

        setProperty("SECURITYTOKENSERVICE_URL", "https://e34jbsl01816.devillo.no:8443/SecurityTokenServiceProvider/", PUBLIC);
        setProperty("SRVRESTOPPFOELGINGSDIALOG_USERNAME", "srvrestoppfoelging://e34jbsl01816.devillo.no:8443/SecurityTokenServiceProvider/", PUBLIC);
        setProperty("SRVRESTOPPFOELGINGSDIALOG_PASSWORD", "Sa7G3myzrkbxnl", PUBLIC);

        setProperty("OPENAM_OPENAMURL", "http://localhost:9080/openam", PUBLIC);
        setProperty("OPENAM_OPENAM_LOGOUTURL", "http://localhost:9080/openam/logout", PUBLIC);
        setProperty("OPENAM_RESTURL", "http://e34jbsl00713.devillo.no:8080/openam/", PUBLIC);

        setProperty("local.mock", "true", PUBLIC);
        setProperty("TJENESTER_URL", "https://tjenester-t4.nav.no", PUBLIC);
        setProperty("TILLATMOCK", "true", PUBLIC);

        setProperty(AAregConfig.MOCK_KEY, "true", PUBLIC);
        setProperty(AktoerConfig.MOCK_KEY, "true", PUBLIC);
        setProperty(DkifConfig.MOCK_KEY, "true", PUBLIC);
        setProperty(EregConfig.MOCK_KEY, "true", PUBLIC);
        setProperty(SykefravaersoppfoelgingV1Config.MOCK_KEY, "true", PUBLIC);
        setProperty(TpsConfig.MOCK_KEY, "true", PUBLIC);
    }
}

