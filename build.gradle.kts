import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import com.github.jengelman.gradle.plugins.shadow.transformers.PropertiesFileTransformer
import com.github.jengelman.gradle.plugins.shadow.transformers.ServiceFileTransformer

group = "no.nav.syfo"
version = "1.0.0"

val cxfVersion = "3.2.7"
val kotlinLibVersion = "1.3.70"
val kotlinJacksonVersion = "2.9.8"
val oidcSupportVersion = "0.2.18"

plugins {
    kotlin("jvm") version "1.3.70"
    id("java")
    id("com.github.johnrengelman.shadow") version "4.0.3"
    id("org.jetbrains.kotlin.plugin.allopen") version "1.3.70"
    id("org.springframework.boot") version "2.1.8.RELEASE"
    id("io.spring.dependency-management") version "1.0.9.RELEASE"
}

buildscript {
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.0")
    }
}

allOpen {
    annotation("org.springframework.context.annotation.Configuration")
    annotation("org.springframework.stereotype.Service")
    annotation("org.springframework.stereotype.Component")
}

repositories {
    mavenCentral()
    jcenter()
    maven(url = "https://repo.adeo.no/repository/maven-releases/")
    maven(url = "https://dl.bintray.com/kotlin/kotlinx/")
    maven(url = "http://packages.confluent.io/maven/")
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinLibVersion")
    implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinLibVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$kotlinJacksonVersion")

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-jersey")
    implementation("org.springframework.boot:spring-boot-starter-logging")
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    implementation("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    implementation("no.nav.security:oidc-spring-support:$oidcSupportVersion")
    testImplementation("no.nav.security:oidc-test-support:$oidcSupportVersion")

    implementation("net.logstash.logback:logstash-logback-encoder:4.10")
    implementation("org.slf4j:slf4j-api:1.7.25")
    implementation("javax.ws.rs:javax.ws.rs-api:2.0.1")
    implementation("javax.inject:javax.inject:1")
    implementation("org.apache.commons:commons-lang3:3.5")
    implementation("net.sf.ehcache:ehcache:2.10.6")
    implementation("io.micrometer:micrometer-registry-prometheus:1.0.6")

    implementation("org.apache.cxf:cxf-rt-features-logging:$cxfVersion")
    implementation("org.apache.cxf:cxf-rt-ws-security:$cxfVersion")
    implementation("org.apache.cxf:cxf-rt-ws-policy:$cxfVersion")
    implementation("org.apache.cxf:cxf-rt-transports-http:$cxfVersion")
    implementation("org.apache.cxf:cxf-rt-frontend-jaxws:$cxfVersion")

    implementation("no.nav.tjenestespesifikasjoner:nav-arbeidsforhold-v3-tjenestespesifikasjon:1.2019.03.05-14.13-d95264192bc7")
    implementation("no.nav.syfo.tjenester:aktoer-v2:1.0")
    implementation("no.nav.syfo.tjenester:dkif-tjenestespesifikasjon:1.2")
    implementation("no.nav.sbl.dialogarena:organisasjonv4-tjenestespesifikasjon:1.0.1")
    implementation("no.nav.tjenestespesifikasjoner:nav-fim-aktoer-v2-tjenestespesifikasjon:1.2019.07.10-12.21-b55f47790a9d")
}


tasks {
    withType<Jar> {
        manifest.attributes["Main-Class"] = "no.nav.syfo.Application"
    }

    create("printVersion") {
        doLast {
            println(project.version)
        }
    }

    withType<ShadowJar> {
        transform(ServiceFileTransformer::class.java) {
            setPath("META-INF/cxf")
            include("bus-extensions.txt")
        }
        transform(PropertiesFileTransformer::class.java) {
            paths = listOf("META-INF/spring.factories")
            mergeStrategy = "append"
        }
        mergeServiceFiles()
    }

    named<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>("compileKotlin") {
        kotlinOptions.jvmTarget = "1.8"
    }

    named<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>("compileTestKotlin") {
        kotlinOptions.jvmTarget = "1.8"
    }
}
