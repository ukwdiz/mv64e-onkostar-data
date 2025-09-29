plugins {
    id("java")
    id("com.gradleup.shadow") version "8.3.8"
}

group = "dev.pcvolkmer.onco"
version = "0.1.0-SNAPSHOT"

var versions = mapOf(
    "commons-cli" to "1.10.0",
    "mtb-dto" to "0.1.0-SNAPSHOT",
    "commons-csv" to "1.14.0",
    "slf4j" to "2.0.17",
    "spring-jdbc" to "5.3.39",
    "mariadb" to "3.5.3"
)

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(11)
    }
}

repositories {
    maven {
        url = uri("https://git.dnpm.dev/api/packages/public-snapshots/maven")
    }
    maven {
        url = uri("https://git.dnpm.dev/api/packages/public/maven")
    }
    mavenCentral()
}

dependencies {
    implementation("dev.pcvolkmer.onco:mv64e-onkostar-data:${version}") { isChanging = true }
    implementation("commons-cli:commons-cli:${versions["commons-cli"]}")
    implementation("org.springframework:spring-jdbc:${versions["spring-jdbc"]}")
    implementation("org.apache.commons:commons-csv:${versions["commons-csv"]}")
    implementation("org.slf4j:slf4j-api:${versions["slf4j"]}")
    implementation("org.mariadb.jdbc:mariadb-java-client:${versions["mariadb"]}")
}

// Include dependencies in resulting JAR file
tasks.shadowJar {
    minimize {
        exclude(dependency("org.mariadb.jdbc:.*:.*"))
    }
}
tasks.jar {
    manifest {
        attributes["Main-Class"] = "dev.pcvolkmer.onco.datamapper.app.ExportApplication"
    }
}

// Build fat JAR using task build
tasks.build.get().dependsOn(tasks.shadowJar)
