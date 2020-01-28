package no.nav.syfo.narmesteleder;

import lombok.Data;
import lombok.experimental.Accessors;
import no.nav.syfo.model.NaermesteLederStatus;

@Data
@Accessors(fluent = true)
public class Naermesteleder {
    public long naermesteLederId;
    public String naermesteLederAktoerId;
    public String orgnummer;
    public NaermesteLederStatus naermesteLederStatus;
    public String navn;
    public String mobil;
    public String epost;
}
