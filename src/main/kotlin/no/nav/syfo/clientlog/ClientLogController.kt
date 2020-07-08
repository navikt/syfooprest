package no.nav.syfo.clientlog

import no.nav.syfo.metric.Metric
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import javax.inject.Inject

@RestController
@RequestMapping("/logging")
class ClientLogController @Inject constructor(
    private val metric: Metric
) {
    private val logger = LoggerFactory.getLogger("frontendlog")

    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun log(logLinje: LogLinje?) {
        // Throttling calls to log
        val currentTime = System.currentTimeMillis()
        lastTime = if (logLinje == null || currentTime - lastTime < minWaitTime) {
            return
        } else {
            currentTime
        }
        if (logLinje.url.contains("oppfolgingsplaner")) {
            metric.countEvent("oppfolgingsplaner-frontendfeil")
        }
        when (logLinje.level) {
            "INFO" -> logger.info(logLinje.toString())
            "ERROR" -> {
                metric.countEvent("frontendlog-error")
                logger.warn(logLinje.toString())
            }
            "WARN" -> logger.warn(logLinje.toString())
            else -> logger.warn("Level-field for LogLinje ikke godkjent.", logLinje)
        }
    }

    companion object {
        private var lastTime: Long = 0
        private const val maxLogPerSec = 10
        private const val minWaitTime = 1000 / maxLogPerSec
    }
}

class LogLinje {
    var level: String? = null
    var message: String? = null
    var url: String = ""
    var jsFileUrl: String? = null
    var userAgent: String? = null
    var lineNumber = 0
    var columnNumber = 0
    override fun toString(): String {
        return "$message [url='$url', jsFile='$jsFileUrl', line='$lineNumber', column='$columnNumber', userAgent='$userAgent']"
    }
}
