package no.nav.syfo.rest.ressurser.teknisk;

import no.nav.syfo.metric.Metrikk;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/logging")
public class ClientLogController {
    private final Logger logger = LoggerFactory.getLogger("frontendlog");

    private static long lastTime = 0;
    private static final int maxLogPerSec = 10;
    private static final int minWaitTime = 1000 / maxLogPerSec;

    private final Metrikk metrikk;

    @Inject
    public ClientLogController(
            Metrikk metrikk
    ) {
        this.metrikk = metrikk;
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    public void log(LogLinje logLinje) {
        // Throttling calls to log
        long currentTime = System.currentTimeMillis();
        if (logLinje == null || currentTime - lastTime < minWaitTime) {
            return;
        } else {
            lastTime = currentTime;
        }

        if (logLinje.url.contains("oppfolgingsplaner")) {
            metrikk.countEvent("oppfolgingsplaner-frontendfeil");
        }

        switch (logLinje.level) {
            case "INFO":
                logger.info(logLinje.toString());
                break;
            case "ERROR":
                metrikk.countEvent("frontendlog-error");
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
