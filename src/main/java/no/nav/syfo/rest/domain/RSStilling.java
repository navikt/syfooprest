package no.nav.syfo.rest.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDate;


@Data
@Accessors(fluent = true, chain = true)
@EqualsAndHashCode
public class RSStilling {
    public String virksomhetsnummer;
    public String yrke;
    public BigDecimal prosent;
    public LocalDate fom;
    public LocalDate tom;

}
