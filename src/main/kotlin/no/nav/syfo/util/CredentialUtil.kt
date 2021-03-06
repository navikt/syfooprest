package no.nav.syfo.util

import java.nio.charset.StandardCharsets
import java.util.*

fun basicHeader(credentialUsername: String, credentialPassword: String): String {
    return "Basic " + Base64.getEncoder().encodeToString(java.lang.String.format("%s:%s", credentialUsername, credentialPassword).toByteArray(StandardCharsets.UTF_8))
}

fun bearerHeader(token: String): String {
    return "Bearer $token"
}
