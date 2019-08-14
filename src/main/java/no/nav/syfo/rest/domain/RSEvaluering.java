package no.nav.syfo.rest.domain;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@Accessors(fluent = true, chain = true)
@EqualsAndHashCode
public class RSEvaluering {

    public String effekt;
    public String hvorfor;
    public String videre;
    public boolean interneaktiviteter;
    public boolean ekstratid;
    public boolean bistand;
    public boolean ingen;
}
