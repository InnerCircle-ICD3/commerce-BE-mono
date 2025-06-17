val javaVersion: String by project
val springBootVersion: String by project
val kotestVersion: String by project
val springKotestVersion: String by project
val springMockkVersion: String by project
val querydslVersion: String by project
val jjwtVersion = "0.12.5"
val restdocsApiSpecVersion: String by project

plugins {
    kotlin("jvm")
    kotlin("kapt")
    kotlin("plugin.spring")
    kotlin("plugin.jpa")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    id("org.jlleitschuh.gradle.ktlint")
    id("com.epages.restdocs-api-spec")
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

    implementation("io.viascom.nanoid:nanoid:1.0.1")

    // JWT support (via JJWT)
    implementation("io.jsonwebtoken:jjwt-api:$jjwtVersion")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:$jjwtVersion")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:$jjwtVersion")

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
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.8")

    testImplementation("org.springframework.restdocs:spring-restdocs-restassured")
    testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")
    implementation("com.epages:restdocs-api-spec-mockmvc:$restdocsApiSpecVersion")
    implementation("com.epages:restdocs-api-spec-restassured:$restdocsApiSpecVersion")

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

    // TODO: 로컬 실행을 위해 설정, 추후 제거 필요
    runtimeOnly("com.h2database:h2")
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
    setServer("http://3.39.233.3:8080")
    title = "801base API docs"
    description = "801base의 API 문서입니다."
    version = "0.0.1"
    format = "yaml"
    tagDescriptionsPropertiesFile = "docs/api/tag-descriptions.yml"
}

tasks.register<Copy>("copyOasToDocs") {
    dependsOn("clean", "openapi3")
    delete("src/main/resources/static/swagger-ui/openapi3.yaml")
    from(project.layout.buildDirectory.file("api-spec/openapi3.yaml"))
    into("src/main/resources/static/swagger-ui/.")
}

tasks.matching { it.name.contains("ktlintCheck", ignoreCase = true) }.configureEach {
    enabled = false
}
