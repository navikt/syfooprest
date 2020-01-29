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

    public Naermesteleder narmesteLeder(String ansattFnr, String virksomhetsnummer) {
        metric.countEvent("call_syfoapi_narmesteleder_ansatt");
        try {
            ResponseEntity<Naermesteleder> response = restTemplate.exchange(
                    getNarmesteLederUrl(virksomhetsnummer),
                    HttpMethod.GET,
                    entity(ansattFnr),
                    new ParameterizedTypeReference<Naermesteleder>() {
                    }
            );
            metric.countEvent("call_syfoapi_narmesteleder_success");
            return response.getBody();
        } catch (RestClientException e) {
            LOG.error("Error requesting ansatt access from syfoppfolgingsplanservice via syfoapi", e);
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
