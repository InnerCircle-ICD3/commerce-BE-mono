FROM gradle:8.11.1-jdk21 AS builder

WORKDIR /app
ENV GRADLE_USER_HOME=/home/gradle/.gradle

COPY build.gradle.kts settings.gradle.kts gradle.properties ./
COPY gradlew ./
COPY gradle ./gradle

COPY docs ./docs

COPY src ./src
COPY src/main/resources/config/secret ./src/main/resources/config/secret

RUN chmod +x gradlew
RUN ./gradlew build -x test \
    -x ktlintMainSourceSetCheck \
    -x ktlintKotlinScriptCheck \
    -x ktlintTestSourceSetCheck \
    --no-daemon \
    --parallel \
    --build-cache

FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar ./app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
