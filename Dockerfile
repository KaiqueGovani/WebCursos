FROM openjdk:17-jdk-slim

WORKDIR /app

# Install Maven and any other needed packages
RUN apt-get update && apt-get install -y maven && rm -rf /var/lib/apt/lists/*

# Copy project files
COPY . .

# Build the project (skip tests for speed)
RUN mvn clean package -DskipTests

EXPOSE 8080

CMD ["java", "-Dspring.profiles.active=dev", "-jar", "target/WebCursos-0.0.1-SNAPSHOT.jar"]
