val javaVersion: String by project
val springBootVersion: String by project
val kotestVersion: String by project
val springKotestVersion: String by project
val springMockkVersion: String by project
val querydslVersion: String by project

plugins {
    kotlin("jvm")
    kotlin("kapt")
    kotlin("plugin.spring")
    kotlin("plugin.jpa")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    id("org.jlleitschuh.gradle.ktlint")
    id("com.epages.restdocs-api-spec") version "0.19.4"
}

group = project.property("projectGroup") as String
version = project.property("applicationVersion") as String

java.sourceCompatibility = JavaVersion.toVersion(javaVersion)

repositories {
    mavenCentral()
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:$springBootVersion")
    }
}

dependencies {
    // kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    // web
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // security
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
    testImplementation("org.springframework.security:spring-security-test")

    // database
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("io.github.openfeign.querydsl:querydsl-core:$querydslVersion")
    implementation("io.github.openfeign.querydsl:querydsl-jpa:$querydslVersion")
    kapt("io.github.openfeign.querydsl:querydsl-apt:$querydslVersion:jakarta")

    runtimeOnly("org.postgresql:postgresql")
    testRuntimeOnly("com.h2database:h2")

    // S3
    implementation(platform("software.amazon.awssdk:bom:2.24.0"))
    implementation("software.amazon.awssdk:s3")
    implementation("software.amazon.awssdk:s3-transfer-manager")

    // configuration
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    kapt("org.springframework.boot:spring-boot-configuration-processor")

    // doc
    testImplementation("org.springframework.restdocs:spring-restdocs-restassured")
    testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")
    implementation("com.epages:restdocs-api-spec-mockmvc:0.19.4")
    implementation("com.epages:restdocs-api-spec-restassured:0.19.4")

    // test
    testImplementation("io.rest-assured:rest-assured")
    testImplementation("io.rest-assured:spring-mock-mvc")
    testImplementation("io.rest-assured:kotlin-extensions")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
    testImplementation("io.kotest:kotest-framework-datatest:$kotestVersion")
    testImplementation("io.kotest.extensions:kotest-extensions-spring:$springKotestVersion")
    testImplementation("com.ninja-squad:springmockk:$springMockkVersion")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = javaVersion
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    systemProperty("user.timezone", "UTC")
}

kapt {
    correctErrorTypes = true
}

val querydslDir = "build/generated"

sourceSets["main"].java.srcDirs(querydslDir)

configure<com.epages.restdocs.apispec.gradle.OpenApi3Extension> {
    setServer("http://localhost:8080")
    title = "801base API docs"
    description = "801base의 API 문서입니다."
    version = "0.0.1"
    format = "yaml"
    tagDescriptionsPropertiesFile = "docs/api/tag-descriptions.yml"
}

tasks.register<Copy>("copyOasToSwagger") {
    delete("src/main/resources/swagger-ui/openapi3.yaml")
    from(project.layout.buildDirectory.file("api-spec/openapi3.yaml"))
    into("src/main/resources/swagger-ui/")
    dependsOn("openapi3")
}

tasks.matching { it.name.contains("ktlintCheck", ignoreCase = true) }.configureEach {
    enabled = false
}
