# syntax=docker/dockerfile:1
# Multi-stage build for Spring Boot 3.2.1 / Java 17 backend.
# Base images (eclipse-temurin) are official multi-arch manifests with
# native linux/arm64 variants, so this builds natively on Oracle Cloud
# Ampere A1 (ARM) hosts as well as on x86_64 dev machines. No x86-only
# dependencies were found in pom.xml.

# Repo ships no Maven wrapper (mvnw), so the build stage uses the official
# multi-arch maven image instead of adding wrapper files.
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build

# Cache Maven dependencies separately from source for faster rebuilds
COPY pom.xml ./
RUN mvn -B -q dependency:go-offline || true

COPY src ./src
RUN mvn -B -q -DskipTests package \
    && mv target/real-estate-backend-*.jar target/app.jar

FROM eclipse-temurin:17-jre-jammy AS runtime
WORKDIR /app

# Non-root user
RUN groupadd --system app && useradd --system --gid app --home /app app \
    && mkdir -p /app/uploads/property-images \
    && chown -R app:app /app

COPY --from=build --chown=app:app /build/target/app.jar /app/app.jar

USER app

ENV PORT=8080 \
    UPLOAD_DIR=/app/uploads/property-images \
    JAVA_OPTS="-XX:MaxRAMPercentage=70 -XX:+UseG1GC"

EXPOSE 8080

VOLUME ["/app/uploads"]

HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=5 \
    CMD wget -q -O - http://127.0.0.1:${PORT}/actuator/health | grep -q '"status":"UP"' || exit 1

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar --spring.profiles.active=prod"]
