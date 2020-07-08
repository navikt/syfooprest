package no.nav.syfo.tilgang

import no.nav.syfo.tilgang.consumer.BrukerTilgangConsumer
import org.springframework.stereotype.Service
import javax.inject.Inject

@Service
class TilgangskontrollService @Inject constructor(
        private val brukerTilgangConsumer: BrukerTilgangConsumer
) {
    fun sporOmNoenAndreEnnSegSelvEllerEgneAnsatte(innloggetFnr: String, oppslattFnr: String): Boolean {
        return !(sporInnloggetBrukerOmSegSelv(innloggetFnr, oppslattFnr) || sporInnloggetBrukerOmEnAnsatt(oppslattFnr))
    }

    private fun sporInnloggetBrukerOmEnAnsatt(oppslattFnr: String): Boolean {
        return brukerTilgangConsumer.hasAccessToAnsatt(oppslattFnr)
    }

    private fun sporInnloggetBrukerOmSegSelv(innloggetFnr: String, fnr: String): Boolean {
        return fnr == innloggetFnr
    }

}