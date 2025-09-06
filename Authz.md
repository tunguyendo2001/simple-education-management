# Implementation Guide: Security Authorization and Database Improvements

## Summary of Changes

This guide addresses the missing `findByClassNameAndYearAndSemester` method and implements comprehensive security authorization to ensure teachers can only access their own classes.

## 🚀 Quick Fix for Current Error

### 1. Update ScoreService Interface
Add the missing method to your `ScoreService.java`:
```java
List<Score> findByClassNameAndYearAndSemester(String className, int year, int semester);
```

### 2. Update ScoreRepository  
Add the method to your `ScoreRepository.java`:
```java
List<Score> findByClassNameAndYearAndSemester(String className, int year, int semester);
```

### 3. Update ScoreServiceImpl
Implement the method in your `ScoreServiceImpl.java`:
```java
@Override
public List<Score> findByClassNameAndYearAndSemester(String className, int year, int semester) {
    return scoreRepository.findByClassNameAndYearAndSemester(className, year, semester);
}
```

## 📊 Database Schema Improvements

### New Tables to Create

1. **classes table** - Proper class management
2. **teacher_classes table** - Many-to-many relationship
3. **student_classes table** - Student enrollment tracking
4. **Updated teachers table** - Authentication fields
5. **Updated scores table** - Foreign key to classes

### Migration Steps

1. Run the provided SQL migration scripts in order (V1 to V7)
2. Update your entity classes with the new relationships
3. Test the new schema with sample data

## 🔐 Security Implementation

### Authentication Flow

1. **Teacher Login**: POST `/api/auth/login`
   ```json
   {
     "username": "thuy.nguyen",
     "password": "password123"
   }
   ```

2. **Receive JWT Token**: Include in all subsequent requests
   ```
   Authorization: Bearer <jwt-token>
   ```

3. **Access Control**: Teachers can only access their own classes

### Key Security Features

- **JWT Token Authentication**: Secure token-based auth
- **Role-Based Access**: Teachers vs. Admins
- **Class-Level Authorization**: Teachers can only access assigned classes
- **Automatic Security Interceptor**: Validates all requests
- **Password Hashing**: BCrypt encryption

## 🛠️ Updated API Endpoints

### Secure Endpoints (Require Authentication)

```
GET /api/scores/my-scores              - Get current teacher's scores
GET /api/scores/class/{className}      - Get class scores (if assigned)
GET /api/scores/export                 - Export my classes only
POST /api/scores                       - Create score (auto-assign to teacher)
PUT /api/scores/{id}                   - Update own scores only
DELETE /api/scores/{id}                - Delete own scores only
```

### Authentication Endpoints

```
POST /api/auth/login                   - Teacher login
POST /api/auth/validate                - Validate JWT token
POST /api/auth/logout                  - Logout (client-side)
```

## 📝 Implementation Checklist

### Phase 1: Quick Fix (Immediate)
- [ ] Add missing method to ScoreService interface
- [ ] Add missing method to ScoreRepository
- [ ] Implement method in ScoreServiceImpl
- [ ] Test existing functionality

### Phase 2: Database Migration (1-2 days)
- [ ] Backup existing database
- [ ] Run migration scripts V1-V7
- [ ] Update entity classes
- [ ] Test data integrity

### Phase 3: Security Implementation (2-3 days)
- [ ] Add JWT dependencies to pom.xml
- [ ] Create authentication service and controller
- [ ] Update Teacher entity with auth fields
- [ ] Implement JWT interceptor
- [ ] Update controllers with security checks
- [ ] Test authentication flow

### Phase 4: Frontend Integration (1-2 days)
- [ ] Update frontend login page
- [ ] Handle JWT tokens in requests
- [ ] Add error handling for authorization
- [ ] Update API calls to use secure endpoints

## 🧪 Testing Strategy

### Unit Tests
```java
// Test teacher authorization
@Test
void testTeacherCanOnlyAccessOwnClasses() {
    // Given: Teacher A and Teacher B with different classes
    // When: Teacher A tries to access Teacher B's class
    // Then: Should receive 403 Forbidden
}

// Test JWT token validation
@Test
void testJwtTokenValidation() {
    // Given: Valid and invalid tokens
    // When: Making requests with tokens
    // Then: Should authorize/reject appropriately
}
```

### Integration Tests
- Test complete login flow
- Test score access with different teachers
- Test export functionality with authorization
- Test CRUD operations with security

## 🔧 Configuration Updates

### application.properties
```properties
# JWT Configuration
jwt.secret=your-secret-key-here
jwt.expiration=28800000
# 8 hours in milliseconds

# Security Configuration
spring.security.user.password=none
```

### Environment Variables (Production)
```bash
JWT_SECRET=your-production-secret-key
DB_PASSWORD=your-db-password
```

## 🚨 Security Best Practices

1. **Never expose JWT secrets** in code repositories
2. **Use HTTPS** in production
3. **Implement rate limiting** for login attempts
4. **Log security events** for monitoring
5. **Regularly rotate JWT secrets**
6. **Use strong password policies**

## 📈 Performance Considerations

- **Database Indexes**: Added for teacher-class queries
- **Lazy Loading**: Used for entity relationships
- **Query Optimization**: Efficient score retrieval
- **Caching**: Consider Redis for JWT blacklisting

## 🔍 Troubleshooting

### Common Issues

1. **JWT Token Expired**
   - Solution: Implement token refresh mechanism

2. **Teacher Can't Access Class**
   - Check: teacher_classes table assignments
   - Verify: is_active = TRUE

3. **Performance Issues**
   - Check: Database indexes are created
   - Monitor: Query execution times

### Debug Queries

```sql
-- Check teacher-class assignments
SELECT t.name, c.name, tc.is_active 
FROM teachers t 
JOIN teacher_classes tc ON t.id = tc.teacher_id 
JOIN classes c ON tc.class_id = c.id;

-- Check scores authorization
SELECT sc.*, t.username 
FROM scores sc 
JOIN teachers t ON sc.teacher_id = t.id;
```

## 📞 Support and Next Steps

After implementing these changes:

1. **Monitor logs** for authentication errors
2. **Gather user feedback** on the new security flow
3. **Plan Phase 2** improvements (role-based permissions, audit logs)
4. **Consider additional features** (password reset, account lockout)

## 🎯 Success Metrics

- [ ] Zero unauthorized access attempts succeed
- [ ] Teachers can access only their assigned classes
- [ ] Export functionality respects teacher boundaries
- [ ] No performance degradation in score queries
- [ ] All existing functionality continues to work