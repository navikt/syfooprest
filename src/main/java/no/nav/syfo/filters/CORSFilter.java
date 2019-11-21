package no.nav.syfo.filters;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Order(Ordered.HIGHEST_PRECEDENCE)
@Component
public class CORSFilter implements Filter {

    private List<String> whitelist = Arrays.asList(
            "https://oppfolgingsplan.nais.oera-q.local",
            "https://oppfolgingsplan.nais.oera.local",
            "https://oppfolgingsplanarbeidsgiver.nais.oera-q.local",
            "https://oppfolgingsplanarbeidsgiver.nais.oera.local",
            "https://tjenester-q1.nav.no",
            "https://tjenester.nav.no"
    );

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;

        String reqUri = httpRequest.getRequestURI();
        if (requestUriErIkkeInternalEndepunkt(reqUri)) {
            String origin = httpRequest.getHeader("origin");
            if (erWhitelisted(origin)) {
                httpResponse.setHeader("Access-Control-Allow-Origin", httpRequest.getHeader("Origin"));
                httpResponse.setHeader("Access-Control-Allow-Credentials", "true");
                httpResponse.setHeader("Access-Control-Allow-Headers", "Origin, Content-Type, Accept, NAV_CSRF_PROTECTION, authorization");
                httpResponse.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
            }
        }

        filterChain.doFilter(servletRequest, httpResponse);
    }

    @Override
    public void destroy() {
    }

    private boolean requestUriErIkkeInternalEndepunkt(String reqUri) {
        return !reqUri.contains("/internal");
    }

    private boolean erWhitelisted(String origin) {
        return origin != null && whitelist.contains(origin);
    }
}
