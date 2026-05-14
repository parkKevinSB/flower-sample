// Root build for the flower-sample sample collection.
//
// Each sample lives in its own subproject (cafe-order, logistics-control,
// turn-battle) and is its own Spring Boot app. The root only contributes
// shared versions, repositories, the Java toolchain, and the test runner.
//
// Plugins that any sample needs are declared here with `apply false` so each
// subproject can opt in via `plugins { id("...") }` without re-declaring the
// version.

plugins {
    id("org.springframework.boot") version "3.3.5" apply false
    id("io.spring.dependency-management") version "1.1.6" apply false
}

allprojects {
    group = "io.github.parkkevinsb.flower.sample"
    version = "0.1.0-SNAPSHOT"

    repositories {
        mavenLocal()
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "java")

    the<JavaPluginExtension>().toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }

    tasks.withType<Test>().configureEach {
        useJUnitPlatform()
    }
}
