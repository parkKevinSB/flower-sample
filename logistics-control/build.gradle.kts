plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

dependencies {
    // Flower starter pulls flower-core + spring-boot autoconfiguration.
    implementation("io.github.parkkevinsb.flower:flower-spring-boot-starter:0.1.0-SNAPSHOT")
    // Adapter that makes a Bloom EventBus visible to Flower as a flower-core EventBus.
    implementation("io.github.parkkevinsb.flower:flower-bloom-adapter:0.1.0-SNAPSHOT")
    // Optional: SLF4J / Micrometer listeners + StepLogger.
    implementation("io.github.parkkevinsb.flower:flower-observability:0.1.0-SNAPSHOT")
    // Bloom event bus implementation we wrap.
    implementation("io.github.parkkevinsb:bloom-core:0.1.0-SNAPSHOT")

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}
