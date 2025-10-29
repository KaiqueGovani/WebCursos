# syntax=docker/dockerfile:1

# ---------- Build stage ----------
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copy pom and pre-fetch dependencies for better caching
COPY pom.xml ./
RUN mvn -q -DskipTests dependency:go-offline

# Copy sources and build
COPY src ./src
RUN mvn -q -DskipTests package

# ---------- Runtime stage ----------
FROM eclipse-temurin:17-jre
WORKDIR /app

# Create non-root user
RUN useradd -ms /bin/bash appuser
USER appuser

# Copy fat jar from build stage
COPY --from=build /app/target/*.jar app.jar

# App port (configured in application.properties)
EXPOSE 8081

# Optional JVM opts
ENV JAVA_OPTS=""

ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar app.jar"]
