package no.nav.syfo.narmesteleder;

import no.nav.security.oidc.context.OIDCRequestContextHolder;
import no.nav.syfo.metric.Metric;
import no.nav.syfo.utils.OIDCUtil;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static no.nav.syfo.oidc.OIDCIssuer.EKSTERN;
import static no.nav.syfo.utils.HttpHeaderUtil.NAV_PERSONIDENT;
import static no.nav.syfo.utils.HttpHeaderUtil.bearerHeader;
import static org.slf4j.LoggerFactory.getLogger;

@Service
public class NarmesteLederConsumer {

    private static final Logger LOG = getLogger(NarmesteLederConsumer.class);

    private final OIDCRequestContextHolder oidcContextHolder;
    private final Metric metric;
    private final RestTemplate restTemplate;
    private final String baseUrl;

    public static final String ERROR_MESSAGE_BASE = "Error requesting Naermeste Leder from syfoppfolgingsplanservice via syfoapi";

    public NarmesteLederConsumer(
            OIDCRequestContextHolder oidcContextHolder,
            Metric metric,
            RestTemplate restTemplate,
            @Value("${syfoapi.url}") String baseUrl
    ) {
        this.oidcContextHolder = oidcContextHolder;
        this.metric = metric;
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    public Optional<Naermesteleder> narmesteLeder(String ansattFnr, String virksomhetsnummer) {
        metric.countEvent("call_syfoapi_narmesteleder_ansatt");
        try {
            ResponseEntity<Naermesteleder> response = restTemplate.exchange(
                    getNarmesteLederUrl(virksomhetsnummer),
                    HttpMethod.GET,
                    entity(ansattFnr),
                    new ParameterizedTypeReference<Naermesteleder>() {
                    }
            );
            if (response.getStatusCode() == HttpStatus.NO_CONTENT) {
                LOG.warn("Did not Find Naermeste leder for Ansatt");
                return Optional.empty();
            } else if (response.getStatusCode() != HttpStatus.OK) {
                metric.countEvent("call_syfoapi_narmesteleder_fail");
                final String message = ERROR_MESSAGE_BASE + response.getStatusCode();
                LOG.error(message);
                throw new RuntimeException(message);
            }
            metric.countEvent("call_syfoapi_narmesteleder_success");
            return Optional.of(response.getBody());
        } catch (RestClientException e) {
            LOG.error(ERROR_MESSAGE_BASE, e);
            metric.countEvent("call_syfoapi_narmesteleder_fail");
            throw e;
        }
    }

    private HttpEntity entity(String personIdent) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, bearerHeader(OIDCUtil.getIssuerToken(oidcContextHolder, EKSTERN)));
        headers.add(NAV_PERSONIDENT, personIdent);
        return new HttpEntity<>(headers);
    }

    private String getNarmesteLederUrl(String virksomhetsnummer) {
        return baseUrl + "/syfooppfolgingsplanservice/api/narmesteleder/" + virksomhetsnummer;
    }
}
