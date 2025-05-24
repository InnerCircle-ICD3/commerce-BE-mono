FROM gradle:8.11.1-jdk21 AS builder

WORKDIR /app
ENV GRADLE_USER_HOME=/home/gradle/.gradle

# 의존성 관련 파일 먼저 복사
COPY build.gradle.kts settings.gradle.kts gradle.properties ./
COPY gradlew ./
COPY gradle ./gradle

# 의존성 먼저 다운로드
RUN chmod +x gradlew && \
    ./gradlew dependencies --no-daemon

# 그 다음 소스 코드 복사
COPY src ./src

# 빌드 실행
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
