# Student Management API

## Overview
The Student Management API is a Spring Boot application designed to manage student, teacher, and score entities. It provides a RESTful interface for performing CRUD operations and supports importing and exporting data in CSV and Excel formats.

## Features
- CRUD operations for Students, Teachers, and Scores
- File import/export functionality for CSV and Excel formats
- API documentation using Swagger
- Validation for request bodies and file formats

## Project Structure
```
student-management-api
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com
│   │   │       └── example
│   │   │           └── studentapi
│   │   │               ├── StudentApiApplication.java
│   │   │               ├── config
│   │   │               │   └── SwaggerConfig.java
│   │   │               ├── controller
│   │   │               │   ├── StudentController.java
│   │   │               │   ├── TeacherController.java
│   │   │               │   └── ScoreController.java
│   │   │               ├── model
│   │   │               │   ├── Student.java
│   │   │               │   ├── Teacher.java
│   │   │               │   └── Score.java
│   │   │               ├── repository
│   │   │               │   ├── StudentRepository.java
│   │   │               │   ├── TeacherRepository.java
│   │   │               │   └── ScoreRepository.java
│   │   │               ├── service
│   │   │               │   ├── StudentService.java
│   │   │               │   ├── TeacherService.java
│   │   │               │   └── ScoreService.java
│   │   │               └── util
│   │   │                   ├── FileValidator.java
│   │   │                   └── ExcelHelper.java
│   │   └── resources
│   │       └── application.properties
│   └── test
│       └── java
│           └── com
│               └── example
│                   └── studentapi
├── pom.xml
└── README.md
```

## Setup Instructions
1. Clone the repository:
   ```
   git clone <repository-url>
   ```
2. Navigate to the project directory:
   ```
   cd student-management-api
   ```
3. Build the project using Maven:
   ```
   mvn clean install
   ```
4. Run the application:
   ```
   mvn spring-boot:run
   ```

## Usage
- Access the API documentation at `http://localhost:8080/swagger-ui.html` after starting the application.
- Use the provided endpoints to manage students, teachers, and scores.

## Future Enhancements
- Implement additional validation rules for input data.
- Add authentication and authorization for secure access to the API.
- Expand the API to include more entities and relationships.

## License
This project is licensed under the MIT License. See the LICENSE file for more details.