package no.nav.syfo.mock;

import no.nav.tjeneste.virksomhet.sykefravaersoppfoelging.v1.*;
import no.nav.tjeneste.virksomhet.sykefravaersoppfoelging.v1.informasjon.*;
import no.nav.tjeneste.virksomhet.sykefravaersoppfoelging.v1.meldinger.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;

import static java.time.LocalDate.now;
import static java.util.Arrays.asList;

public class OppfolgingMock implements SykefravaersoppfoelgingV1 {


    @Override
    public WSHentNaermesteLederListeResponse hentNaermesteLederListe(WSHentNaermesteLederListeRequest request) throws HentNaermesteLederListeSikkerhetsbegrensning {
        return new WSHentNaermesteLederListeResponse()
                .withNaermesteLederListe(
                        asList(
                                new WSNaermesteLeder()
                                        .withNavn("Ledulf Ledermann")
                                        .withEpost("Ledulf@Ledermann.no")
                                        .withMobil("12345678")
                                        .withNaermesteLederAktoerId("0001112223334")
                                        .withNaermesteLederId(16L)
                                        .withNaermesteLederStatus(new WSNaermesteLederStatus()
                                                .withErAktiv(true)
                                                .withAktivFom(LocalDate.now().minusMonths(1))
                                        )
                                        .withOrgnummer("999000999")));
    }

    @Override
    public WSHentSykeforlopperiodeResponse hentSykeforlopperiode(WSHentSykeforlopperiodeRequest wsHentSykeforlopperiodeRequest) throws HentSykeforlopperiodeSikkerhetsbegrensning {
        return new WSHentSykeforlopperiodeResponse()
                .withSykeforlopperiodeListe(new WSSykeforlopperiode()
                        .withAktivitet("Aktivitet")
                        .withGrad(10)
                        .withFom(now().minusDays(2))
                        .withTom(now().plusDays(2)));
    }

    @Override
    public WSHentHendelseListeResponse hentHendelseListe(WSHentHendelseListeRequest wsHentHendelseListeRequest) throws HentHendelseListeSikkerhetsbegrensning {
        return new WSHentHendelseListeResponse()
                .withHendelseListe(new WSHendelse()
                        .withAktoerId("12345678")
                        .withId(1)
                        .withTidspunkt(LocalDateTime.now())
                        .withType("Type"));
    }

    @Override
    public WSHentNaermesteLedersHendelseListeResponse hentNaermesteLedersHendelseListe(WSHentNaermesteLedersHendelseListeRequest wsHentNaermesteLedersHendelseListeRequest) throws HentNaermesteLedersHendelseListeSikkerhetsbegrensning {
        return new WSHentNaermesteLedersHendelseListeResponse()
                .withHendelseListe(new WSHendelseNyNaermesteLeder()
                        .withAktoerId("12345678")
                        .withId(1)
                        .withTidspunkt(LocalDateTime.now())
                        .withType("Type"));
    }

    @Override
    public WSBerikNaermesteLedersAnsattBolkResponse berikNaermesteLedersAnsattBolk(WSBerikNaermesteLedersAnsattBolkRequest request) throws BerikNaermesteLedersAnsattBolkSikkerhetsbegrensning {
        return null;
    }

    @Override
    public void ping() {
    }

    @Override
    public WSHentNaermesteLederResponse hentNaermesteLeder(WSHentNaermesteLederRequest request) throws HentNaermesteLederSikkerhetsbegrensning {
        return new WSHentNaermesteLederResponse()
                .withNaermesteLeder(
                        new WSNaermesteLeder()
                                .withNavn("Ledulf Ledermann")
                                .withOrgnummer("000555888")
                                .withNaermesteLederId(123)
                                .withNaermesteLederStatus(new WSNaermesteLederStatus()
                                        .withErAktiv(true)
                                        .withAktivFom(LocalDate.now().minusDays(22))
                                )
                                .withNaermesteLederAktoerId("0001112223334")
                                .withEpost("Ledulf@Ledermann.no")
                                .withMobil("12345678"));
    }

    @Override
    public WSHentNaermesteLedersAnsattListeResponse hentNaermesteLedersAnsattListe(WSHentNaermesteLedersAnsattListeRequest request) throws HentNaermesteLedersAnsattListeSikkerhetsbegrensning {
        return new WSHentNaermesteLedersAnsattListeResponse().withAnsattListe(Arrays.asList(
                new WSAnsatt()
                        .withNaermesteLederStatus(new WSNaermesteLederStatus().withAktivFom(now().minusDays(10)).withErAktiv(true))
                        .withAktoerId("5554446669997")
                        .withNaermesteLederId(345)
                        .withNavn("Test Testesen")
                        .withOrgnummer("999000999"),
                new WSAnsatt()
                        .withNaermesteLederStatus(new WSNaermesteLederStatus().withAktivTom(now().minusDays(10)).withAktivFom(now().minusDays(20)).withErAktiv(false))
                        .withAktoerId("5554446667778")
                        .withNaermesteLederId(234)
                        .withNavn("Test Testesen")
                        .withOrgnummer("999000999"),
                new WSAnsatt()
                        .withNaermesteLederStatus(new WSNaermesteLederStatus().withAktivFom(now().minusDays(10)).withErAktiv(true))
                        .withAktoerId("5554446668889")
                        .withNaermesteLederId(346)
                        .withNavn("Test Testesen")
                        .withOrgnummer("000555888")

        ));
    }
}