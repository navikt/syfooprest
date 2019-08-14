package no.nav.syfo.rest.ressurser.teknisk;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static no.nav.metrics.MetricsFactory.createEvent;

@Path("/logging")
@Controller
public class ClientLogController {
    private final Logger logger = LoggerFactory.getLogger("frontendlog");

    private static long lastTime = 0;
    private static final int maxLogPerSec = 10;
    private static final int minWaitTime = 1000 / maxLogPerSec;

    @POST
    @Consumes(APPLICATION_JSON)
    public void log(LogLinje logLinje) {
        // Throttling calls to log
        long currentTime = System.currentTimeMillis();
        if (logLinje == null || currentTime - lastTime < minWaitTime) {
            return;
        } else {
            lastTime = currentTime;
        }

        if (logLinje.url.contains("oppfolgingsplaner")) {
            createEvent("oppfolgingsplaner-frontendfeil").report();
        }

        switch (logLinje.level) {
            case "INFO":
                logger.info(logLinje.toString());
                break;
            case "ERROR":
                createEvent("frontendlog-error").report();
            case "WARN":
                logger.warn(logLinje.toString());
                break;
            default:
                logger.warn("Level-field for LogLinje ikke godkjent.", logLinje);
        }
    }

    private static class LogLinje {
        public String level;
        public String message;
        public String url;
        public String jsFileUrl;
        public String userAgent;
        public int lineNumber;
        public int columnNumber;

        @Override
        public String toString() {
            return message + " [url='" + url + "', jsFile='" + jsFileUrl + "', line='" + lineNumber + "', column='" + columnNumber + "', userAgent='" + userAgent + "']";
        }
    }
}