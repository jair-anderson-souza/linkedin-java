#!/bin/bash

echo "🚀 Starting LinkedIn Clone Application (Spring Boot with Gradle)..."

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker is not running. Please start Docker first."
    exit 1
fi

# Check if Docker Compose is available
if ! command -v docker-compose > /dev/null 2>&1; then
    echo "❌ Docker Compose is not installed. Please install Docker Compose first."
    exit 1
fi

# Check if Java is installed
if ! command -v java > /dev/null 2>&1; then
    echo "❌ Java is not installed. Please install Java 17+ first."
    exit 1
fi

# Check if curl is available for health checks
if ! command -v curl > /dev/null 2>&1; then
    echo "❌ curl is not installed. Please install curl for health checks."
    exit 1
fi

echo "🧹 Cleaning up existing containers..."
docker-compose down -v --remove-orphans

echo "🏗️  Building Java applications with Gradle..."
echo "   Building core-service..."
cd core-service && ./gradlew clean bootJar --no-daemon
if [ $? -ne 0 ]; then
    echo "❌ Failed to build core-service"
    echo "💡 Try running: cd core-service && ./gradlew clean bootJar --stacktrace"
    exit 1
fi

cd ../people-graph-service
echo "   Building people-graph-service..."
./gradlew clean bootJar --no-daemon
if [ $? -ne 0 ]; then
    echo "❌ Failed to build people-graph-service"
    echo "💡 Try running: cd people-graph-service && ./gradlew clean bootJar --stacktrace"
    exit 1
fi
cd ..

echo "🐳 Starting all services with Docker Compose..."
docker-compose up -d

echo "⏳ Waiting for services to initialize..."
echo "   This may take 30-60 seconds for first startup..."
sleep 30

# Health check function
check_service_health() {
    local service_name=$1
    local url=$2
    local max_attempts=40
    local attempt=1

    echo "   Checking $service_name..."
    while [ $attempt -le $max_attempts ]; do
        if curl -s -f "$url" > /dev/null 2>&1; then
            echo "   ✅ $service_name is healthy"
            return 0
        fi
        
        if [ $((attempt % 10)) -eq 0 ]; then
            echo "   ⏳ Still waiting for $service_name (attempt $attempt/$max_attempts)..."
        fi
        
        sleep 3
        attempt=$((attempt + 1))
    done
    
    echo "   ❌ $service_name failed to start after $((max_attempts * 3)) seconds"
    return 1
}

# Check service health
echo "🔍 Checking service health..."

# Check databases first
echo "   Verifying databases are ready..."
sleep 10

check_service_health "Core Service" "http://localhost:8080/api/health"
core_health=$?

if [ $core_health -ne 0 ]; then
    echo ""
    echo "🔍 Core Service failed - checking logs:"
    docker-compose logs --tail=20 core-service
    echo ""
fi

check_service_health "Graph Service" "http://localhost:8081/api/health"
graph_health=$?

if [ $graph_health -ne 0 ]; then
    echo ""
    echo "🔍 Graph Service failed - checking logs:"
    docker-compose logs --tail=20 people-graph-service
    echo ""
fi

echo ""
if [ $core_health -eq 0 ] && [ $graph_health -eq 0 ]; then
    echo "✅ Application started successfully!"
    echo ""
    echo "🔗 Available services:"
    echo "   Core Service: http://localhost:8080/api/health"
    echo "   Graph Service: http://localhost:8081/api/health"
    echo "   Neo4j Browser: http://localhost:7474 (neo4j/neo4jpassword)"
    echo "   MySQL: localhost:3306 (linkedin_user/linkedin_pass)"
    echo ""
    echo "📋 Available endpoints:"
    echo "   Auth: http://localhost:8080/api/auth/register"
    echo "   Users: http://localhost:8080/api/users"
    echo "   Graph: http://localhost:8081/api/graph"
    echo ""
    echo "🏗️  Built with Gradle:"
    echo "   core-service/build/libs/core-service-1.0.0.jar"
    echo "   people-graph-service/build/libs/people-graph-service-1.0.0.jar"
    echo ""
    echo "📋 Import postman-collection.json to test the API"
    echo ""
    echo "To stop the application:"
    echo "   docker-compose down (to stop all services)"
    echo "   docker-compose down -v (to stop and remove volumes)"
    echo ""
    echo "To view logs:"
    echo "   docker-compose logs -f [service-name]"
    echo "   Available services: core-service, people-graph-service, mysql, neo4j"
    echo ""
    echo "To rebuild locally:"
    echo "   ./gradlew clean bootJar (in each service directory)"
else
    echo "❌ Some services failed to start!"
    echo ""
    echo "🔍 Troubleshooting steps:"
    echo "1. Check Docker containers: docker-compose ps"
    echo "2. View service logs:"
    echo "   docker-compose logs core-service"
    echo "   docker-compose logs people-graph-service"
    echo "   docker-compose logs mysql"
    echo "   docker-compose logs neo4j"
    echo ""
    echo "3. Manual restart:"
    echo "   docker-compose down"
    echo "   docker-compose up -d"
    echo ""
    echo "4. Rebuild containers:"
    echo "   docker-compose down -v"
    echo "   docker-compose up -d --build"
    echo ""
    echo "5. Check system resources (Docker needs sufficient memory)"
    echo ""
    exit 1
fi