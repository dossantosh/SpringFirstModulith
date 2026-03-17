# syntax=docker/dockerfile:1.7
FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /app

COPY pom.xml ./
RUN --mount=type=cache,target=/root/.m2 mvn -q -DskipTests dependency:go-offline

COPY src ./src
RUN --mount=type=cache,target=/root/.m2 mvn -q -DskipTests clean package

# Pick the executable Spring Boot jar and normalize its name for runtime copy.
RUN set -eu; \
    jar="$(find target -maxdepth 1 -type f -name '*.jar' \
      ! -name '*-sources.jar' ! -name '*-javadoc.jar' ! -name 'original-*.jar' | head -n 1)"; \
    test -n "$jar"; \
    cp "$jar" target/app.jar

FROM eclipse-temurin:25-jre
WORKDIR /app

RUN useradd -r -u 1001 appuser
COPY --from=build /app/target/app.jar /app/app.jar

USER 1001
ENV SERVER_PORT=7070
EXPOSE 7070
ENTRYPOINT ["java","-jar","/app/app.jar"]
