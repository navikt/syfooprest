# syfooprest

Syfooprest is REST-API in SBS offering information regarding `Arbeidsgiver` and `Arbeidstaker`
to the following applications in SBS:
* oppfolgingsplan (Ditt NAV)
* oppfolgingsplanarbeidsgiver (Dine Sykmeldte)

A list of the exposeddata:
* `Arbeidsforhold`
* `Kontaktinfo`
* `Name of person`
* `Nærmeste leder`
* `Virksomhet`

External data is retrieved with SOAP.

## Technology
* Java
* Spring Boot
* Gradle

## Local Development

#### Build
Run `./gradlew clean shadowJar`

#### Run Application

Run `LocalApplication.main`. Runs on port 8580.

#### Run tests

Run `./gradlew test -i`


## Pipeline

Pipeline in jenkins: https://jenkins-digisyfo.adeo.no/job/digisyfo/job/syfooprest/

#### Deploy

Deploy with Naiserator
