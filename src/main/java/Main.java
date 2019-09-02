import no.nav.apiapp.ApiApp;
import no.nav.syfo.config.*;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static java.lang.System.getProperty;
import static java.lang.System.getenv;
import static no.nav.sbl.util.EnvironmentUtils.Type.PUBLIC;
import static no.nav.sbl.util.EnvironmentUtils.getRequiredProperty;
import static no.nav.sbl.util.EnvironmentUtils.setProperty;

public class Main {
    public static final String SERVICEGATEWAY_URL = "SERVICEGATEWAY_URL";

    public static void main(String... args) throws Exception {
        getenv().forEach(System::setProperty);

        setProperty(AktoerConfig.ENDEPUNKT_URL_KEY, getRequiredProperty(SERVICEGATEWAY_URL, AktoerConfig.ENDEPUNKT_URL_KEY), PUBLIC);
        setProperty(AAregConfig.ENDEPUNKT_URL_KEY, getRequiredProperty(SERVICEGATEWAY_URL, AAregConfig.ENDEPUNKT_URL_KEY), PUBLIC);
        setProperty(DkifConfig.ENDEPUNKT_URL_KEY, getRequiredProperty(SERVICEGATEWAY_URL, DkifConfig.ENDEPUNKT_URL_KEY), PUBLIC);
        setProperty(EregConfig.ENDEPUNKT_URL_KEY, getRequiredProperty(SERVICEGATEWAY_URL, EregConfig.ENDEPUNKT_URL_KEY), PUBLIC);
        setProperty(SykefravaersoppfoelgingV1Config.ENDEPUNKT_URL_KEY, getRequiredProperty(SERVICEGATEWAY_URL, SykefravaersoppfoelgingV1Config.ENDEPUNKT_URL_KEY), PUBLIC);
        setProperty(TpsConfig.ENDEPUNKT_URL_KEY, getRequiredProperty(SERVICEGATEWAY_URL, TpsConfig.ENDEPUNKT_URL_KEY), PUBLIC);

        setupMetricsProperties();
        ApiApp.runApp(ApplicationConfig.class, args);
    }

    private static void setupMetricsProperties() throws UnknownHostException {
        setProperty("applicationName", "restoppfoelgingsdialog", PUBLIC);
        setProperty("node.hostname", InetAddress.getLocalHost().getHostName(), PUBLIC);
        setProperty("environment.name", getProperty("FASIT_ENVIRONMENT_NAME"), PUBLIC);
    }
}
