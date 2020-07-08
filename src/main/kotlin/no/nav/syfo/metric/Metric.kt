package no.nav.syfo.metric

import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tags
import org.springframework.stereotype.Controller
import javax.inject.Inject

@Controller
class Metric @Inject constructor(
    private val registry: MeterRegistry
) {
    fun countEvent(navn: String) {
        registry.counter(
            addPrefix(navn),
            Tags.of("type", "info")
        ).increment()
    }

    fun countEndpointRequest(navn: String) {
        registry.counter(
            addPrefix(navn),
            Tags.of("type", "info")
        ).increment()
    }

    fun countOutgoingReponses(navn: String, statusCode: Int) {
        registry.counter(
            addPrefix(navn),
            Tags.of(
                "type", "info",
                "status", statusCode.toString()
            )
        ).increment()
    }

    fun countHttpReponse(kode: Int) {
        registry.counter(
            addPrefix("httpstatus"),
            Tags.of(
                "type", "info",
                "kode", kode.toString())
        ).increment()
    }

    private fun addPrefix(navn: String): String {
        val METRIKK_PREFIX = "syfooprest_"
        return METRIKK_PREFIX + navn
    }
}
