# syfooprest

Syfooprest is REST-API in SBS offering information regarding `Arbeidsgiver` and `Arbeidstaker`
to the following applications in SBS:
* oppfolgingsplan (Ditt NAV)
* oppfolgingsplanarbeidsgiver (Dine Sykmeldte)

A list of the exposeddata:
* `Arbeidsforhold`
* `Kontaktinfo`
* `Name of person`
* `Nærmeste ledere`
* `Nærmeste leder`
* `Virksomhet`

External data is retrieved with SOAP.

## Technology
* Java
* Kotlin
* Spring Boot
* Gradle

## Local Development

#### Build
Run `./gradlew clean shadowJar`

#### Run application

Run `LocalApplication.main`. Runs on port 8580.

#### Run tests

Run `./gradlew test -i`

#### Lint
Run `./gradlew --continue ktlintCheck`

## Pipeline

Pipeline is run with Github Actions.
Commits to Master-branch are deployed automatically to dev-sbs and prod-sbs.
Commits to non-master-branch is built with automatic deploy.

#### Deploy

Deploy with Naiserator
