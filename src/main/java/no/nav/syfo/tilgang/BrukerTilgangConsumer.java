package no.nav.syfo.tilgang;

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

import static no.nav.syfo.oidc.OIDCIssuer.EKSTERN;
import static no.nav.syfo.utils.HttpHeaderUtil.NAV_PERSONIDENT;
import static no.nav.syfo.utils.HttpHeaderUtil.bearerHeader;
import static org.slf4j.LoggerFactory.getLogger;

@Service
public class BrukerTilgangConsumer {

    private static final Logger LOG = getLogger(BrukerTilgangConsumer.class);

    private final OIDCRequestContextHolder oidcContextHolder;
    private final Metric metric;
    private final RestTemplate restTemplate;
    private final String baseUrl;

    public BrukerTilgangConsumer(
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

    public boolean hasAccessToAnsatt(String ansattFnr) {
        metric.countEvent("call_syfoapi_ansatttilgang_ansatt");
        try {
            ResponseEntity<BrukerTilgang> response = restTemplate.exchange(
                    getAccessAnsattUrl(),
                    HttpMethod.GET,
                    entity(ansattFnr),
                    new ParameterizedTypeReference<BrukerTilgang>() {
                    }
            );
            metric.countEvent("call_syfoapi_ansatttilgang_success");
            return response.getBody().tilgang;
        } catch (RestClientException e) {
            LOG.error("Error requesting ansatt access from syfoppfolgingsplanservice via syfoapi", e);
            metric.countEvent("call_syfoapi_ansatttilgang_fail");
            throw e;
        }
    }

    private HttpEntity entity(String personIdent) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, bearerHeader(OIDCUtil.getIssuerToken(oidcContextHolder, EKSTERN)));
        headers.add(NAV_PERSONIDENT, personIdent);
        return new HttpEntity<>(headers);
    }

    private String getAccessAnsattUrl() {
        return baseUrl + "/syfooppfolgingsplanservice/api/tilgang/ansatt";
    }
}
