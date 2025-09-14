# LinkedIn Clone - Spring Boot Microservices

A LinkedIn-style application with dual database architecture featuring Neo4j for relationship data and MySQL for transactional data, built with Spring Boot and Java.

## 🏗️ Architecture Overview

This project implements a microservices architecture with two main services built with Spring Boot:

### Core Service (MySQL)
- **Port**: 8080
- **Database**: MySQL 8.0
- **Technology**: Spring Boot 3.2, Spring Data JPA, Spring Security
- **Purpose**: Handles transactional data (users, posts, comments, jobs, companies)
- **Features**: JWT authentication, pagination, validation, file upload support

### People Graph Service (Neo4j)
- **Port**: 8081  
- **Database**: Neo4j 5.11
- **Technology**: Spring Boot 3.2, Spring Data Neo4j
- **Purpose**: Manages relationship data and graph operations
- **Features**: Connection recommendations, shortest paths, affinity ranking, skill endorsements

## 🚀 Quick Start

### Prerequisites
- Java 17+ (JDK)
- Gradle 8.0+ (optional, Gradle wrapper is included)
- Docker & Docker Compose
- Git

### 1. Clone and Setup
```bash
git clone <repository-url>
cd linkedin-clone
```

### 2. Build Applications
```bash
# Build both services with Gradle
./build.sh

# OR individual services:
cd core-service && ./gradlew clean bootJar --no-daemon
cd people-graph-service && ./gradlew clean bootJar --no-daemon
```

### 3. Start with Docker (Recommended)
```bash
# Start all services (databases + applications)
./start.sh
```

This will:
- Build the Java applications
- Start MySQL and Neo4j databases
- Deploy the Spring Boot applications
- Run health checks

### 4. Manual Development Setup

If you prefer to run services individually:

```bash
# Start only databases
docker-compose up -d mysql neo4j redis

# Terminal 1 - Core Service
cd core-service
./gradlew bootRun

# Terminal 2 - Graph Service  
cd people-graph-service
./gradlew bootRun
```

### 5. Verify Installation

Check that all services are running:
- Core Service: http://localhost:8080/api/health
- Graph Service: http://localhost:8081/api/health
- Neo4j Browser: http://localhost:7474 (neo4j/neo4jpassword)

## 📚 API Documentation

### Core Service Endpoints

#### Authentication
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - User login
- `GET /api/auth/profile` - Get current user profile
- `POST /api/auth/validate-token` - Validate JWT token

#### Users
- `GET /api/users` - Get all users (with filtering & pagination)
- `GET /api/users/{id}` - Get specific user
- `PUT /api/users/{id}` - Update user profile
- `DELETE /api/users/{id}` - Delete user account
- `GET /api/users/search` - Search users
- `GET /api/users/stats` - Get user statistics

### Graph Service Endpoints

#### Connections
- `POST /api/graph/users/{userId}/connections/{targetId}` - Connect two users
- `GET /api/graph/users/{userId}/connection-suggestions` - Get connection suggestions
- `GET /api/graph/users/shortest-path?from=&to=` - Find shortest connection path
- `GET /api/graph/users/{userId}/people-you-may-know` - Get people recommendations
- `GET /api/graph/users/{userId}/connection-count` - Get connection count
- `GET /api/graph/users/{userId}/mutual-connections/{targetUserId}` - Get mutual connections
- `GET /api/graph/users/{userId}/affinity-ranking` - Get affinity-based recommendations

#### Skills & Endorsements
- `POST /api/graph/users/{userId}/skills/{skillName}/endorse` - Endorse user skill

#### Company Operations
- `POST /api/graph/users/{userId}/companies/{companyId}/follow` - Follow company
- `POST /api/graph/users/{userId}/work-experience` - Add work experience

#### Sync Operations (Inter-service communication)
- `POST /api/graph/sync/user` - Sync user to graph database (accepts UserSyncRequest DTO)
- `POST /api/graph/sync/company` - Sync company to graph database

**Note**: The core-service automatically syncs user data to the graph service using RestTemplate when users register or update their profiles.

## 🛢️ Database Schemas

### MySQL Tables (JPA Entities)

#### User Entity
```java
@Entity
@Table(name = "users")
- Long id (Primary Key)
- String email (Unique)
- String password (Hashed with BCrypt)
- String firstName, lastName
- String headline, summary
- String profileImageUrl
- String location, industry
- LocalDate dateOfBirth
- UserStatus status (ACTIVE/INACTIVE/SUSPENDED)
- LocalDateTime createdAt, updatedAt
```

#### Post Entity
```java
@Entity  
@Table(name = "posts")
- Long id (Primary Key)
- User user (ManyToOne relationship)
- String content
- List<String> mediaUrls
- Integer likesCount, commentsCount, sharesCount
- PostVisibility visibility (PUBLIC/CONNECTIONS/PRIVATE)
- PostStatus status (ACTIVE/DELETED/DRAFT)
- LocalDateTime createdAt, updatedAt
```

### Neo4j Graph Model (Spring Data Neo4j)

#### Node Entities
- `UserNode`: id, email, firstName, lastName, headline, location, industry
- `CompanyNode`: id, name, description, industry, location
- `SkillNode`: name

#### Relationship Entities
- `(:User)-[:CONNECTED_TO]-(:User)`: User connections
- `(:User)-[:FOLLOWS]->(:Company)`: Following companies  
- `(:User)-[:WORKED_AT]->(:Company)`: Work experience (with properties)
- `(:User)-[:ENDORSED]->(:User)`: Skill endorsements
- `(:User)-[:HAS_SKILL]->(:Skill)`: User skills (with endorsement count)

## 🧪 Testing with Postman

Import the provided collection: `postman-collection.json`

### Collection Variables:
- `core_service_url`: http://localhost:8080/api
- `graph_service_url`: http://localhost:8081/api  
- `auth_token`: Auto-set after login
- `user_id`: Auto-set after login

### Test Flow:
1. **Register/Login** - Get JWT authentication token
2. **Create User Data** - Posts, profile updates
3. **Test Graph Operations** - Connections, recommendations
4. **Verify Relationships** - Check graph queries

## 🔧 Development

### Project Structure
```
linkedin-clone/
├── core-service/                    # Spring Boot MySQL service
│   ├── src/main/java/com/linkedin/coreservice/
│   │   ├── controller/             # REST Controllers  
│   │   ├── service/               # Business logic
│   │   ├── repository/            # JPA Repositories
│   │   ├── entity/                # JPA Entities
│   │   ├── dto/                   # Data Transfer Objects
│   │   ├── security/              # JWT & Security config
│   │   ├── config/                # Spring configuration
│   │   ├── exception/             # Custom exceptions
│   │   └── util/                  # JWT utilities
│   ├── src/main/resources/
│   │   ├── application.properties
│   │   └── application-docker.properties
│   ├── gradle/wrapper/            # Gradle wrapper files
│   ├── build.gradle               # Gradle build configuration
│   ├── settings.gradle            # Gradle settings
│   ├── gradlew                    # Gradle wrapper script (Unix)
│   ├── gradlew.bat               # Gradle wrapper script (Windows)  
│   └── Dockerfile
├── people-graph-service/           # Spring Boot Neo4j service
│   ├── src/main/java/com/linkedin/peoplegraphservice/
│   │   ├── controller/            # Graph Controllers
│   │   ├── service/              # Graph business logic  
│   │   ├── repository/           # Neo4j Repositories
│   │   ├── entity/               # Neo4j Node/Relationship entities
│   │   ├── dto/                  # Data Transfer Objects
│   │   ├── config/               # Neo4j & Security config
│   │   └── exception/            # Custom exceptions
│   ├── src/main/resources/
│   │   ├── application.properties  
│   │   └── application-docker.properties
│   ├── gradle/wrapper/            # Gradle wrapper files
│   ├── build.gradle               # Gradle build configuration
│   ├── settings.gradle            # Gradle settings
│   ├── gradlew                    # Gradle wrapper script (Unix)
│   ├── gradlew.bat               # Gradle wrapper script (Windows)
│   └── Dockerfile
├── docker-compose.yml             # All services orchestration
├── start.sh                       # Application startup script
├── build.sh                       # Build script
└── postman-collection.json        # Postman API collection for testing
```

### Adding New Features

#### Core Service
1. Create JPA Entity in `entity/` package
2. Create Repository interface extending `JpaRepository`  
3. Implement Service class with business logic
4. Create Controller with REST endpoints
5. Add DTOs for request/response objects
6. Add dependencies to `build.gradle` if needed
7. Configure security if needed
8. Configure RestTemplate beans for inter-service communication if needed

#### Graph Service
1. Create Neo4j Node/Relationship entities
2. Create Repository with custom Cypher queries
3. Implement Service class with graph operations  
4. Create Controller with graph endpoints
5. Add DTOs for data transfer
6. Add dependencies to `build.gradle` if needed

## 🔒 Security Features

- **JWT Authentication**: Token-based stateless authentication
- **Spring Security**: Comprehensive security framework
- **BCrypt Password Hashing**: Secure password storage
- **CORS Configuration**: Cross-origin request handling
- **Input Validation**: Jakarta Bean Validation (JSR-303)
- **SQL Injection Prevention**: JPA parameterized queries
- **Cypher Injection Prevention**: Parameterized Neo4j queries
- **RestTemplate Configuration**: Timeout configuration for reliable inter-service calls

## 🏃‍♂️ Running the Application

### Development Mode
```bash
# Build and start everything
./start.sh

# Individual services
cd core-service && ./gradlew bootRun
cd people-graph-service && ./gradlew bootRun
```

### Docker Mode
```bash
# Start with Docker Compose
docker-compose up -d

# Rebuild and start
docker-compose up -d --build

# View logs  
docker-compose logs -f
docker-compose logs -f core-service
docker-compose logs -f people-graph-service
```

### Available Commands
```bash
./build.sh             # Build both Java applications with Gradle
./start.sh             # Build and start all services with Docker
docker-compose up -d   # Start with Docker Compose
docker-compose down    # Stop Docker services  
docker-compose up -d --build  # Rebuild and restart
docker-compose logs -f # View all service logs

# Individual Gradle commands:
./gradlew clean        # Clean builds (run in each service directory)
./gradlew bootJar      # Build executable JAR (run in each service directory)  
./gradlew bootRun      # Run in development mode (run in each service directory)
./gradlew test         # Run tests (run in each service directory)
```

## 📊 Key Features Implemented

### Spring Boot Core Service
✅ RESTful API with Spring Web MVC  
✅ JWT Authentication with Spring Security  
✅ JPA/Hibernate with MySQL  
✅ Bean Validation with custom error handling  
✅ RestTemplate-based inter-service communication  
✅ Comprehensive logging with Logback  
✅ Docker containerization  
✅ Health checks with Spring Actuator  

### Spring Data Neo4j Graph Service  
✅ Neo4j integration with Spring Data  
✅ Complex graph queries with custom Cypher  
✅ Node and Relationship entity modeling  
✅ Connection recommendations algorithm  
✅ Shortest path calculations  
✅ Multi-factor affinity ranking  
✅ Skill endorsement system  
✅ Work experience tracking  

### Graph Algorithms Implemented
✅ **Connection Suggestions**: 2nd-degree connection recommendations  
✅ **Shortest Path**: Minimum connection path between users  
✅ **Mutual Connections**: Common connections discovery  
✅ **People You May Know**: Multi-factor algorithm considering:
   - Same company colleagues
   - Same industry professionals  
   - Same location contacts
   - Common skill matches
✅ **Affinity Ranking**: Weighted scoring system:
   - Mutual connections (weight: 3)
   - Common skills (weight: 2)
   - Same companies (weight: 4) 
   - Same industry (weight: 2)
   - Same location (weight: 1)

## 🐳 Docker Configuration

### Services in docker-compose.yml
- **MySQL 8.0**: Transactional database with health checks
- **Neo4j 5.11**: Graph database with APOC plugins  
- **Redis**: Caching layer (available for future use)
- **Core Service**: Spring Boot application (port 8080)
- **People Graph Service**: Spring Boot application (port 8081)

### Docker Features
- Multi-stage builds for optimized images
- Health checks for all services
- Volume mounts for data persistence  
- Network isolation
- Environment-specific configurations

## 🛠️ Build System (Gradle)

This project uses **Gradle** as the build system for both Spring Boot services:

### Key Gradle Features Used:
- **Spring Boot Gradle Plugin**: Builds executable JAR files
- **Dependency Management Plugin**: Manages Spring Boot dependencies
- **Gradle Wrapper**: Ensures consistent Gradle version across environments
- **Multi-module Support**: Each service is an independent Gradle project

### Common Gradle Commands:
```bash
# Build executable JAR
./gradlew bootJar

# Run application in development mode
./gradlew bootRun

# Run tests
./gradlew test

# Clean build artifacts
./gradlew clean

# Build and run tests
./gradlew build
```

### Gradle Configuration:
- `build.gradle`: Build configuration and dependencies
- `settings.gradle`: Project settings and name
- `gradle/wrapper/`: Gradle wrapper files for version consistency
- `gradlew` / `gradlew.bat`: Gradle wrapper scripts

## 📈 Performance Optimizations

- **Database Indexes**: Optimized queries on frequently accessed fields
- **JPA Query Optimization**: Efficient entity relationships and lazy loading
- **Neo4j Constraints**: Unique constraints and indexes for graph traversal
- **Connection Pooling**: MySQL connection pool configuration
- **Pagination**: Built-in Spring Data pagination support
- **RestTemplate with Timeouts**: Reliable inter-service communication with configurable timeouts
- **Gradle Build Cache**: Faster incremental builds

## 🚧 Future Enhancements

- [ ] Redis caching integration
- [ ] WebSocket real-time messaging
- [ ] Spring Batch for data processing
- [ ] Spring Cloud microservices (Config Server, Gateway, Discovery)
- [ ] Monitoring with Micrometer and Prometheus
- [ ] Comprehensive testing (Unit, Integration, TestContainers)
- [ ] API documentation with OpenAPI/Swagger
- [ ] Message queues with RabbitMQ/Apache Kafka
- [ ] Elasticsearch integration for advanced search
- [ ] Production-ready deployment (Kubernetes)

## 🔧 Configuration

### Environment Variables

#### Core Service
```properties
PORT=8080
DB_HOST=localhost  
DB_PORT=3306
DB_NAME=linkedin_core
DB_USER=linkedin_user
DB_PASSWORD=linkedin_pass
JWT_SECRET=your_secret_key
JWT_EXPIRES_IN=86400000
GRAPH_SERVICE_URL=http://localhost:8081
```

#### Graph Service  
```properties
PORT=8081
NEO4J_URI=bolt://localhost:7687
NEO4J_USER=neo4j
NEO4J_PASSWORD=neo4jpassword
CORE_SERVICE_URL=http://localhost:8080
JWT_SECRET=your_secret_key
```

## 📝 License

MIT License - See LICENSE file for details

## 🤝 Contributing

1. Fork the repository
2. Create feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'Add amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Open Pull Request

---

**Built with ❤️ using Spring Boot, Gradle, MySQL, Neo4j, and Docker**