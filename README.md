# syfooprest

Syfooprest er et REST-API i SBS tilbyr informasjon knyttet til nærmeste leder og
applikasjonene i SBS (oppfolgingsplan og oppfolgingsplanarbeidsgiver).

Dette er en liste over informasjon som tilbys:
* Arbeidsforhold
* Kontaktinfo
* Navn på person
* Nærmeste leder
* Virksomhet

Ekstern fnformasjon hentes med SOAP.


## Lokal utvikling

Start opp via `LocalApplication.main`. Kjører på port 8580.


## Veien til prod

Bygg ligger i jenkins: https://jenkins-digisyfo.adeo.no/job/digisyfo/job/syfooprest/


## Teknologi
* Java
* Spring Boot
* Gradle
