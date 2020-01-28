package no.nav.syfo.utils;

public class HttpHeaderUtil {

    public static final String NAV_PERSONIDENT = "nav-personident";

    public static String bearerHeader(String token) {
        return "Bearer " + token;
    }
}
