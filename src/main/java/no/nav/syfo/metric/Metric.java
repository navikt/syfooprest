package no.nav.syfo.metric;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller
public class Metric {

    private final MeterRegistry registry;

    @Inject
    public Metric(MeterRegistry registry) {
        this.registry = registry;
    }

    public void countEvent(String navn) {
        registry.counter(
                addPrefix(navn),
                Tags.of("type", "info")
        ).increment();
    }

    public void countEndpointRequest(String navn) {
        registry.counter(
                addPrefix(navn),
                Tags.of("type", "info")
        ).increment();
    }

    public void countOutgoingReponses(String navn, Integer statusCode) {
        registry.counter(
                addPrefix(navn),
                Tags.of(
                        "type", "info",
                        "status", statusCode.toString()
                )
        ).increment();
    }

    public void countHttpReponse(int kode) {
        registry.counter(
                addPrefix("httpstatus"),
                Tags.of(
                        "type", "info",
                        "kode", String.valueOf(kode)
                )
        ).increment();
    }

    private String addPrefix(String navn) {
        String METRIKK_PREFIX = "syfooprest_";
        return METRIKK_PREFIX + navn;
    }
}
