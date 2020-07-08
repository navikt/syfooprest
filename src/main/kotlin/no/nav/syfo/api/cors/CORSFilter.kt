package no.nav.syfo.api.cors

import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import java.io.IOException
import javax.servlet.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Order(Ordered.HIGHEST_PRECEDENCE)
@Component
class CORSFilter : Filter {
    private val whitelist = listOf(
        "https://oppfolgingsplan.nais.oera-q.local",
        "https://oppfolgingsplan.nais.oera.local",
        "https://oppfolgingsplanarbeidsgiver.nais.oera-q.local",
        "https://oppfolgingsplanarbeidsgiver.nais.oera.local",
        "https://tjenester-q1.nav.no",
        "https://tjenester.nav.no"
    )

    @Throws(ServletException::class)
    override fun init(filterConfig: FilterConfig) {
    }

    @Throws(IOException::class, ServletException::class)
    override fun doFilter(servletRequest: ServletRequest, servletResponse: ServletResponse, filterChain: FilterChain) {
        val httpResponse = servletResponse as HttpServletResponse
        val httpRequest = servletRequest as HttpServletRequest
        val reqUri = httpRequest.requestURI
        if (requestUriErIkkeInternalEndepunkt(reqUri)) {
            val origin = httpRequest.getHeader("origin")
            if (erWhitelisted(origin)) {
                httpResponse.setHeader("Access-Control-Allow-Origin", httpRequest.getHeader("Origin"))
                httpResponse.setHeader("Access-Control-Allow-Credentials", "true")
                httpResponse.setHeader("Access-Control-Allow-Headers", "Origin, Content-Type, Accept, NAV_CSRF_PROTECTION, authorization")
                httpResponse.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
            }
        }
        filterChain.doFilter(servletRequest, httpResponse)
    }

    override fun destroy() {}
    private fun requestUriErIkkeInternalEndepunkt(reqUri: String): Boolean {
        return !reqUri.contains("/internal")
    }

    private fun erWhitelisted(origin: String?): Boolean {
        return origin != null && whitelist.contains(origin)
    }
}
