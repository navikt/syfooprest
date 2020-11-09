import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import com.github.jengelman.gradle.plugins.shadow.transformers.PropertiesFileTransformer
import com.github.jengelman.gradle.plugins.shadow.transformers.ServiceFileTransformer

group = "no.nav.syfo"
version = "1.0.0"

val jaxbApiVersion = "2.4.0-b180830.0359"
val jaxbRuntimeVersion = "2.4.0-b180830.0438"
val jaxwsToolsVersion = "2.3.1"

val cxfVersion = "3.2.7"
val junitJupiterVersion = "5.6.0"
val kotlinLibVersion = "1.3.70"
val kotlinJacksonVersion = "2.9.8"
val mockkVersion = "1.9.3"
val nimbusSDKVersion = "7.0.3"
val oidcSupportVersion = "0.2.18"
val tjenestespesifikasjonerVersion = "1.2019.09.25-00.21-49b69f0625e0"

plugins {
    kotlin("jvm") version "1.3.70"
    id("java")
    id("com.github.johnrengelman.shadow") version "4.0.3"
    id("org.jetbrains.kotlin.plugin.allopen") version "1.3.70"
    id("org.jlleitschuh.gradle.ktlint") version "9.2.1"
    id("org.springframework.boot") version "2.2.6.RELEASE"
    id("io.spring.dependency-management") version "1.0.9.RELEASE"
}

buildscript {
    dependencies {
        classpath("javax.xml.bind:jaxb-api:2.4.0-b180830.0359")
        classpath("org.glassfish.jaxb:jaxb-runtime:2.4.0-b180830.0438")
        classpath("com.sun.activation:javax.activation:1.2.0")
        classpath("com.sun.xml.ws:jaxws-tools:2.3.1") {
            exclude(group = "com.sun.xml.ws", module = "policy")
        }
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
    maven(url = "https://repo1.maven.org/maven2/")
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
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(module = "junit")
        exclude(module = "mockito-core")
    }
    testImplementation("org.junit.jupiter:junit-jupiter:$junitJupiterVersion")
    testImplementation("io.mockk:mockk:$mockkVersion")

    implementation("com.nimbusds:oauth2-oidc-sdk:$nimbusSDKVersion")
    implementation("no.nav.security:oidc-spring-support:$oidcSupportVersion")
    testImplementation("no.nav.security:oidc-test-support:$oidcSupportVersion")

    implementation("net.logstash.logback:logstash-logback-encoder:4.10")
    implementation("org.slf4j:slf4j-api:1.7.25")
    implementation("javax.ws.rs:javax.ws.rs-api:2.0.1")
    implementation("javax.inject:javax.inject:1")
    implementation("org.apache.commons:commons-lang3:3.5")
    implementation("net.sf.ehcache:ehcache:2.10.6")
    implementation("io.micrometer:micrometer-registry-prometheus:1.0.6")

    implementation("javax.xml.bind:jaxb-api:$jaxbApiVersion")
    implementation("org.glassfish.jaxb:jaxb-runtime:$jaxbRuntimeVersion")
    implementation("com.sun.xml.ws:jaxws-tools:$jaxwsToolsVersion") {
        exclude(group = "com.sun.xml.ws", module = "policy")
    }

    implementation("org.apache.cxf:cxf-rt-features-logging:$cxfVersion")
    implementation("org.apache.cxf:cxf-rt-ws-security:$cxfVersion")
    implementation("org.apache.cxf:cxf-rt-ws-policy:$cxfVersion")
    implementation("org.apache.cxf:cxf-rt-transports-http:$cxfVersion")
    implementation("org.apache.cxf:cxf-rt-frontend-jaxws:$cxfVersion")

    implementation("no.nav.tjenestespesifikasjoner:nav-arbeidsforhold-v3-tjenestespesifikasjon:$tjenestespesifikasjonerVersion")
    implementation("no.nav.tjenestespesifikasjoner:dkif-tjenestespesifikasjon:$tjenestespesifikasjonerVersion")
    implementation("no.nav.tjenestespesifikasjoner:organisasjonv4-tjenestespesifikasjon:$tjenestespesifikasjonerVersion")
    implementation("no.nav.tjenestespesifikasjoner:dial-nav-tjeneste-aktoer_v2:$tjenestespesifikasjonerVersion")
}

tasks {
    withType<Jar> {
        manifest.attributes["Main-Class"] = "no.nav.syfo.ApplicationKt"
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
        kotlinOptions.jvmTarget = "11"
    }

    named<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>("compileTestKotlin") {
        kotlinOptions.jvmTarget = "11"
    }

    withType<Test> {
        useJUnitPlatform {
            includeEngines("junit-jupiter")
        }
    }
}
