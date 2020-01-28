package no.nav.syfo.narmesteleder;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;

@Data
@Accessors(fluent = true)
public class NaermesteLederStatus {
    public boolean erAktiv;
    public LocalDate aktivFom;
    public LocalDate aktivTom;
}
