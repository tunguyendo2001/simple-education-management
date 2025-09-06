# 🎓 Education Department API

A comprehensive Spring Boot REST API for managing students, teachers, and academic scores with secure authorization and Excel export capabilities.

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.7.14-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg)](https://www.mysql.com/)
[![Docker](https://img.shields.io/badge/Docker-Ready-blue.svg)](https://www.docker.com/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

## 🚀 Features

### 🔐 **Security & Authorization**
- **JWT Authentication** - Secure token-based authentication
- **Role-Based Access Control** - Teachers can only access their assigned classes
- **Password Encryption** - BCrypt hashing for secure password storage
- **API Security** - Protected endpoints with automatic token validation

### 👥 **User Management**
- **Teacher Management** - Create, update, and manage teacher accounts
- **Student Management** - Complete CRUD operations for student records
- **Class Management** - Organize students into classes with teacher assignments

### 📊 **Score Management**
- **Grade Recording** - Track regular scores, mid-term, and final exam grades
- **Semester Support** - Manage scores across multiple semesters
- **Automated Calculations** - Calculate semester and yearly averages
- **Teacher Isolation** - Teachers can only manage scores for their assigned classes

### 📄 **Excel Integration**
- **Export to Excel** - Generate formatted gradebooks with Vietnamese headers
- **Multiple Formats** - Support for both .xls and .xlsx formats
- **Class-Based Export** - Export scores by class with proper formatting
- **Secure Export** - Teachers can only export their own classes

### 🏗️ **Architecture**
- **Clean Architecture** - Separation of concerns with proper layering
- **RESTful API** - Standard REST endpoints with proper HTTP methods
- **Database Relations** - Properly normalized database with foreign key constraints
- **Docker Support** - Fully containerized with Docker Compose

## 📋 Prerequisites

- **Java 17** or higher
- **Maven 3.6+**
- **MySQL 8.0+** 
- **Docker & Docker Compose** (for containerized deployment)

## ⚡ Quick Start

### 🐳 Docker Deployment (Recommended)

```bash
# Clone the repository
git clone <repository-url>
cd education-department-api

# Setup Docker environment
./scripts/docker-setup.sh

# Start all services
./scripts/docker-start.sh

# Access the application
# API Documentation: http://localhost:8080/swagger-ui.html
# Database Admin: http://localhost:8081
```

### 💻 Local Development

```bash
# Clone and build
git clone <repository-url>
cd education-department-api
mvn clean install

# Setup MySQL database
mysql -u root -p
CREATE DATABASE education_db;

# Configure application.properties
cp src/main/resources/application.properties.example src/main/resources/application.properties
# Edit database connection settings

# Run the application
mvn spring-boot:run
```

## 🔑 Default Login Credentials

```
Username: thuy.nguyen
Password: password123

Username: nam.tran  
Password: password123

Username: hoa.le
Password: password123
```

## 📖 API Documentation

### Authentication Endpoints
```http
POST /api/auth/login          # Teacher login
POST /api/auth/validate       # Validate JWT token
POST /api/auth/logout         # Logout (client-side)
```

### Score Management (Secured)
```http
GET  /api/scores/my-scores              # Get current teacher's scores
GET  /api/scores/class/{className}      # Get class scores (if assigned)
POST /api/scores                        # Create new score
PUT  /api/scores/{id}                   # Update score (own scores only)
DELETE /api/scores/{id}                 # Delete score (own scores only)
GET  /api/scores/export                 # Export assigned classes to Excel
```

### Student Management
```http
GET    /api/students          # List all students
GET    /api/students/{id}     # Get student by ID
POST   /api/students          # Create new student
PUT    /api/students/{id}     # Update student
DELETE /api/students/{id}     # Delete student
```

### Teacher Management
```http
GET    /api/teachers          # List all teachers
GET    /api/teachers/{id}     # Get teacher by ID
POST   /api/teachers          # Create new teacher
PUT    /api/teachers/{id}     # Update teacher
DELETE /api/teachers/{id}     # Delete teacher
```

## 🗄️ Database Schema

### Core Entities
- **teachers** - Teacher information with authentication
- **students** - Student personal information
- **classes** - Class definitions and metadata
- **scores** - Academic scores and grades

### Relationships
- **teacher_classes** - Many-to-many teacher-class assignments
- **student_classes** - Many-to-many student-class enrollments
- Teachers can only access scores for their assigned classes

## 🔧 Configuration

### Environment Variables
```bash
# Database
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/education_db
SPRING_DATASOURCE_USERNAME=mysql
SPRING_DATASOURCE_PASSWORD=mysql

# JWT Security
JWT_SECRET=your-secret-key-here
JWT_EXPIRATION=28800000

# File Upload
MAX_FILE_SIZE=10MB
MAX_REQUEST_SIZE=10MB
```

### Application Profiles
- **default** - Local development with H2 database
- **docker** - Docker container configuration
- **prod** - Production settings with security optimizations

## 🛠️ Development

### Project Structure
```
src/
├── main/java/com/example/studentapi/
│   ├── config/          # Configuration classes
│   ├── controller/      # REST Controllers
│   ├── model/          # Entity classes
│   ├── repository/     # Data access layer
│   ├── service/        # Business logic
│   └── util/           # Utility classes
├── main/resources/
│   ├── application.properties     # Configuration
│   └── application-{profile}.properties
└── test/               # Unit and integration tests
```

### Key Technologies
- **Spring Boot** - Application framework
- **Spring Security** - Authentication and authorization
- **Spring Data JPA** - Database abstraction
- **JWT** - JSON Web Tokens for stateless authentication
- **Apache POI** - Excel file generation
- **MySQL** - Primary database
- **Docker** - Containerization

## 🧪 Testing

### Run Tests
```bash
# Unit tests
mvn test

# Integration tests
mvn verify

# Test with Docker
docker-compose -f docker-compose.test.yml up --abort-on-container-exit
```

### API Testing
```bash
# Health check
curl http://localhost:8080/actuator/health

# Login test
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"thuy.nguyen","password":"password123"}'

# Access protected endpoint
curl -X GET http://localhost:8080/api/scores/my-scores \
  -H "Authorization: Bearer <jwt-token>"
```

## 🚀 Deployment

### Docker Production
```bash
# Production deployment
docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d

# Monitor logs
docker-compose logs -f app

# Backup database
./scripts/docker-backup.sh
```

### Traditional Deployment
```bash
# Build JAR
mvn clean package -DskipTests

# Run with production profile
java -jar target/education-department-fake-1.0-SNAPSHOT.jar --spring.profiles.active=prod
```

## 🔒 Security Features

### Authentication & Authorization
- **JWT Tokens** - Stateless authentication
- **Password Hashing** - BCrypt encryption
- **Role Separation** - Teachers vs. Administrators
- **Class Isolation** - Teachers cannot access other teachers' classes

### API Security
- **CORS Configuration** - Controlled cross-origin requests
- **Request Validation** - Input sanitization and validation
- **Error Handling** - Secure error messages
- **Rate Limiting** - Protection against abuse

## 📊 Monitoring & Observability

### Health Checks
- Application health endpoint: `/actuator/health`
- Database connectivity monitoring
- Container health checks in Docker

### Logging
- Structured logging with different levels
- File-based logging with rotation
- Request/response logging for debugging

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Development Guidelines
- Follow Spring Boot best practices
- Write unit tests for new features
- Update documentation for API changes
- Ensure security considerations for new endpoints

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 📞 Support

### Getting Help
- **Documentation**: Check the `/swagger-ui.html` endpoint
- **Logs**: Use `docker-compose logs -f app` for troubleshooting
- **Issues**: Create a GitHub issue with detailed information

### Common Issues
- **Port conflicts**: Change ports in `docker-compose.yml`
- **Database connection**: Verify MySQL credentials and connection string
- **JWT errors**: Check JWT secret configuration
- **File upload issues**: Verify volume mounts and permissions

## 🎯 Roadmap

### Upcoming Features
- [ ] Student portal for viewing grades
- [ ] Email notifications for grade updates
- [ ] Advanced reporting and analytics
- [ ] Mobile app support
- [ ] Integration with external learning management systems

### Performance Improvements
- [ ] Redis caching for frequently accessed data
- [ ] Database query optimization
- [ ] API response pagination
- [ ] Async processing for file operations

---

<div align="center">

[📚 API Docs](http://localhost:8080/swagger-ui.html)

</div>