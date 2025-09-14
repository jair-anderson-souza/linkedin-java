#!/bin/bash

echo "🏗️  Building LinkedIn Clone Java Applications with Gradle..."

# Check if Java is installed
if ! command -v java > /dev/null 2>&1; then
    echo "❌ Java is not installed. Please install Java 17+ first."
    exit 1
fi

# Check if Gradle is installed (optional, we can use wrapper)
if ! command -v gradle > /dev/null 2>&1; then
    echo "⚠️  Gradle is not installed globally, but that's okay - we'll use Gradle wrapper"
fi

echo "Building core-service..."
cd core-service
./gradlew clean bootJar
if [ $? -ne 0 ]; then
    echo "❌ Failed to build core-service"
    exit 1
fi
echo "✅ core-service built successfully"

cd ../people-graph-service
echo "Building people-graph-service..."
./gradlew clean bootJar
if [ $? -ne 0 ]; then
    echo "❌ Failed to build people-graph-service"
    exit 1
fi
echo "✅ people-graph-service built successfully"

cd ..
echo "✅ All services built successfully with Gradle!"
echo ""
echo "To run the applications:"
echo "   npm start (or ./start.sh)"
echo ""
echo "To run with Docker:"
echo "   npm run docker:up"
echo ""
echo "Built JAR files:"
echo "   core-service/build/libs/core-service-1.0.0.jar"
echo "   people-graph-service/build/libs/people-graph-service-1.0.0.jar"