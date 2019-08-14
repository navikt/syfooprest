package no.nav.syfo.rest.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@Accessors(fluent = true)
@EqualsAndHashCode
public class RSVirksomhet {

    public String virksomhetsnummer;
    public String navn = "";
}
