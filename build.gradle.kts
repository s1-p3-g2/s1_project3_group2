import com.github.spotbugs.snom.Confidence
import com.github.spotbugs.snom.Effort

plugins {
    java
    id("org.springframework.boot") version "4.1.2"
    id("io.spring.dependency-management") version "1.1.7"
    pmd
    id("com.github.spotbugs") version "6.5.13"
    checkstyle
}

group = "org.example"
version = "0.0.1-SNAPSHOT"
description = "backend"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }

}

sourceSets {
    main {
        java.srcDirs(
            "backend/src/main/java",
        )
        resources.srcDirs("backend/src/main/resources")
    }

    test {
        java.srcDirs("backend/src/test/java")
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security-oauth2-client")
    implementation("org.springframework.boot:spring-boot-starter-security-oauth2-resource-server")
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    runtimeOnly("org.postgresql:postgresql")
    testImplementation("org.springframework.boot:spring-boot-starter-data-jpa-test")
    testImplementation("org.springframework.boot:spring-boot-starter-security-oauth2-client-test")
    testImplementation("org.springframework.boot:spring-boot-starter-security-oauth2-resource-server-test")
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:testcontainers-junit-jupiter")
    testImplementation("org.testcontainers:testcontainers-postgresql")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    runtimeOnly("com.h2database:h2")

    spotbugs("com.github.spotbugs:spotbugs:4.9.3")
    spotbugsPlugins("com.h3xstream.findsecbugs:findsecbugs-plugin:1.14.0")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
sourceSets {
    main {
        java.setSrcDirs(listOf("backend/src/main/java"))
        resources.setSrcDirs(listOf("backend/src/main/resources"))
    }

    test {
        java.setSrcDirs(listOf("backend/src/test/java"))
        resources.setSrcDirs(listOf("backend/src/test/resources"))
    }
}

pmd {
    ruleSetFiles = files("config/pmd/ruleset.xml")
    isIgnoreFailures = false
}

spotbugs {
    ignoreFailures = false
    showStackTraces = true
    showProgress = true
    effort = Effort.DEFAULT
    reportLevel = Confidence.DEFAULT
    visitors = listOf("FindSqlInjection", "SwitchFallthrough")
    omitVisitors = listOf("FindNonShortCircuit")
    chooseVisitors = listOf("-FindNonShortCircuit", "+TestASM")
    reportsDir = file("$buildDir/spotbugs")
//    includeFilter = file("config/spotbugs/include.xml")
//    excludeFilter = file("config/spotbugs/exclude.xml")
//    baselineFile = file("config/spotbugs/baseline.xml")
//    onlyAnalyze = listOf("com.foobar.MyClass", "com.foobar.mypkg.*")
    maxHeapSize = "1g"
    extraArgs = listOf("-nested:false")
    jvmArgs = listOf("-Duser.language=ja")
}
// Sets the main Gradle task to create a report
tasks.spotbugsMain {
    reports.create("html") {
        required = true
        outputLocation = file("$buildDir/reports/spotbugs.html")
        setStylesheet("fancy-hist.xsl")
    }
}

checkstyle {
    toolVersion = "10.24.0"

    // Pulls built-in java checkstyle config
    configFile = file("config/checkstyle/google_checks.xml")
}
// Sets the main Gradle task to create a report
tasks.withType<Checkstyle>().configureEach {
    reports {
        xml.required = true
        html.required = true
        html.stylesheet = resources.text.fromFile("config/xsl/checkstyle-simple.xsl")
    }
}
