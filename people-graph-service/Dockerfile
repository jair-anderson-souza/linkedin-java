# Use official OpenJDK 17 as base image
FROM openjdk:17-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Install basic dependencies
RUN apt-get update && \
    apt-get install -y curl unzip && \
    rm -rf /var/lib/apt/lists/*

# Copy Gradle wrapper files
COPY gradlew .
COPY gradle gradle/
COPY build.gradle .
COPY settings.gradle .

# Copy source code
COPY src src/

# Create logs directory
RUN mkdir -p logs

# Build the application using Gradle wrapper
RUN ./gradlew clean bootJar --no-daemon

# Expose port
EXPOSE 8081

# Create a non-root user
RUN groupadd -r appuser && useradd -r -g appuser appuser
RUN chown -R appuser:appuser /app
USER appuser

# Set JVM options
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC"

# Run the jar file
CMD ["sh", "-c", "java $JAVA_OPTS -jar build/libs/people-graph-service-1.0.0.jar"]