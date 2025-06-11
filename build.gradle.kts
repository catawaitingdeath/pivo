plugins {
    java
    id("org.springframework.boot") version "3.4.4"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "org.example"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:2024.0.1")
    }
}

dependencies {
    implementation("org.postgresql:postgresql:42.7.5")
    implementation("org.springframework.boot:spring-boot-starter-web")
    compileOnly("org.projectlombok:lombok")
    testImplementation("org.testcontainers:postgresql:1.21.0")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation("org.springframework.boot:spring-boot-starter-validation:3.4.4")
    implementation("org.mapstruct:mapstruct:1.6.3")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.6.3")
    implementation("org.projectlombok:lombok-mapstruct-binding:0.2.0")
    testCompileOnly("org.projectlombok:lombok:1.18.30")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.30")
    testImplementation("org.assertj:assertj-core:3.27.3")
    implementation("org.liquibase:liquibase-core:4.31.1")
    implementation("com.aventrix.jnanoid:jnanoid:2.0.0")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.6")
    testImplementation("org.skyscreamer:jsonassert:1.5.0")
    testImplementation("net.javacrumbs.json-unit:json-unit-spring:4.1.1")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign")
    testImplementation("org.wiremock.integrations:wiremock-spring-boot:3.6.0")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
