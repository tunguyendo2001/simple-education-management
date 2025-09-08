CREATE DATABASE IF NOT EXISTS education_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE education_db;

-- Grant privileges
GRANT ALL PRIVILEGES ON education_db.* TO 'mysql'@'%';
FLUSH PRIVILEGES;

-- docker/mysql/init/02_create_tables.sql
USE education_db;

-- Create teachers table with authentication fields
CREATE TABLE IF NOT EXISTS teachers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    gender ENUM('MEN', 'WOMEN'),
    hometown VARCHAR(255),
    birthday DATE,
    username VARCHAR(50) UNIQUE,
    password_hash VARCHAR(255),
    email VARCHAR(100) UNIQUE,
    is_active BOOLEAN DEFAULT TRUE,
    last_login TIMESTAMP NULL,
    created_at DATE,
    updated_at DATE
);

-- Create students table
CREATE TABLE IF NOT EXISTS students (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    gender VARCHAR(50),
    hometown VARCHAR(255),
    birthday DATE,
    created_at DATE,
    updated_at DATE
);

-- Create classes table
CREATE TABLE IF NOT EXISTS classes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    grade_level INT NOT NULL,
    academic_year INT NOT NULL,
    semester INT NOT NULL,
    subject VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create teacher_classes junction table
CREATE TABLE teacher_classes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    teacher_id BIGINT NOT NULL,
    school_class_id BIGINT NOT NULL,  -- Fixed column name
    assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (teacher_id) REFERENCES teachers(id) ON DELETE CASCADE,
    FOREIGN KEY (school_class_id) REFERENCES classes(id) ON DELETE CASCADE,
    UNIQUE KEY unique_teacher_class (teacher_id, school_class_id)
);

-- Create student_classes junction table
CREATE TABLE IF NOT EXISTS student_classes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    class_id BIGINT NOT NULL,
    enrolled_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (class_id) REFERENCES classes(id) ON DELETE CASCADE,
    UNIQUE KEY unique_student_class (student_id, class_id)
);

-- Create scores table
CREATE TABLE IF NOT EXISTS scores (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT,
    teacher_id BIGINT,
    class_id BIGINT,
    class_name VARCHAR(255), -- Keep for backward compatibility
    semester INT,
    year INT,
    ddgtx VARCHAR(255),
    ddggk INT,
    ddgck INT,
    tbm INT,
    comment TEXT,
    student_name VARCHAR(255),
    teacher_name VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (teacher_id) REFERENCES teachers(id) ON DELETE CASCADE,
    FOREIGN KEY (class_id) REFERENCES classes(id) ON DELETE CASCADE
);

-- Create indexes for performance
CREATE INDEX IF NOT EXISTS idx_teachers_username ON teachers(username);
CREATE INDEX IF NOT EXISTS idx_teachers_email ON teachers(email);
CREATE INDEX IF NOT EXISTS idx_teachers_active ON teachers(is_active);

CREATE INDEX IF NOT EXISTS idx_classes_name ON classes(name);
CREATE INDEX IF NOT EXISTS idx_classes_year_semester ON classes(academic_year, semester);

CREATE INDEX idx_teacher_classes_teacher ON teacher_classes(teacher_id);
CREATE INDEX idx_teacher_classes_school_class ON teacher_classes(school_class_id);
CREATE INDEX idx_teacher_classes_active ON teacher_classes(is_active);

CREATE INDEX IF NOT EXISTS idx_student_classes_student ON student_classes(student_id);
CREATE INDEX IF NOT EXISTS idx_student_classes_class ON student_classes(class_id);

CREATE INDEX IF NOT EXISTS idx_scores_class_student ON scores(class_id, student_id);
CREATE INDEX IF NOT EXISTS idx_scores_teacher_class ON scores(teacher_id, class_id);
CREATE INDEX IF NOT EXISTS idx_scores_year_semester ON scores(year, semester);

-- docker/mysql/init/03_insert_sample_data.sql
USE education_db;

-- -- Insert sample teachers with hashed passwords
-- -- Password for all teachers: "password123"
-- -- Hash generated with BCrypt
-- INSERT IGNORE INTO teachers (name, gender, hometown, birthday, username, password_hash, email, is_active, created_at, updated_at) VALUES 
-- ('Nguyễn Thị Thủy', 'WOMEN', 'Hà Nội', '1980-05-15', 'thuy.nguyen', '$2a$10$7Q9VPBgI8sKVXi.hOxFjcOJT2fjqfL7GBfKzMLxBzPf7UOhLGUlKK', 'thuy.nguyen@school.edu.vn', TRUE, CURDATE(), CURDATE()),
-- ('Trần Văn Nam', 'MEN', 'Hồ Chí Minh', '1978-03-22', 'nam.tran', '$2a$10$7Q9VPBgI8sKVXi.hOxFjcOJT2fjqfL7GBfKzMLxBzPf7UOhLGUlKK', 'nam.tran@school.edu.vn', TRUE, CURDATE(), CURDATE()),
-- ('Lê Thị Hoa', 'WOMEN', 'Đà Nẵng', '1985-09-10', 'hoa.le', '$2a$10$7Q9VPBgI8sKVXi.hOxFjcOJT2fjqfL7GBfKzMLxBzPf7UOhLGUlKK', 'hoa.le@school.edu.vn', TRUE, CURDATE(), CURDATE());

-- -- Insert sample classes
-- INSERT IGNORE INTO classes (name, grade_level, academic_year, semester, subject) VALUES 
-- ('10A1', 10, 2024, 1, 'Tin học'),
-- ('10A2', 10, 2024, 1, 'Tin học'),
-- ('10B1', 10, 2024, 1, 'Tin học'),
-- ('11A1', 11, 2024, 1, 'Tin học'),
-- ('11A2', 11, 2024, 1, 'Tin học'),
-- ('12A1', 12, 2024, 1, 'Tin học');

-- -- Insert sample students
-- INSERT IGNORE INTO students (name, gender, hometown, birthday, created_at, updated_at) VALUES 
-- ('Nguyễn Văn An', 'Nam', 'Hà Nội', '2007-01-15', CURDATE(), CURDATE()),
-- ('Trần Thị Bình', 'Nữ', 'Hà Nội', '2007-02-20', CURDATE(), CURDATE()),
-- ('Lê Văn Cường', 'Nam', 'Hải Phòng', '2007-03-10', CURDATE(), CURDATE()),
-- ('Phạm Thị Dung', 'Nữ', 'Nam Định', '2007-04-05', CURDATE(), CURDATE()),
-- ('Hoàng Văn Em', 'Nam', 'Thái Bình', '2007-05-12', CURDATE(), CURDATE()),
-- ('Đặng Thị Phương', 'Nữ', 'Hà Nội', '2007-06-08', CURDATE(), CURDATE());

-- -- Insert teacher-class assignments
-- INSERT IGNORE INTO teacher_classes (teacher_id, class_id, is_active) VALUES 
-- (1, 1, TRUE),  -- Nguyễn Thị Thủy teaches 10A1
-- (1, 2, TRUE),  -- Nguyễn Thị Thủy teaches 10A2
-- (2, 3, TRUE),  -- Trần Văn Nam teaches 10B1
-- (2, 4, TRUE),  -- Trần Văn Nam teaches 11A1
-- (3, 5, TRUE),  -- Lê Thị Hoa teaches 11A2
-- (3, 6, TRUE);  -- Lê Thị Hoa teaches 12A1

-- -- Insert student-class enrollments
-- INSERT IGNORE INTO student_classes (student_id, class_id, is_active) VALUES 
-- (1, 1, TRUE), (2, 1, TRUE), -- Students 1,2 in class 10A1
-- (3, 2, TRUE), (4, 2, TRUE), -- Students 3,4 in class 10A2
-- (5, 3, TRUE), (6, 3, TRUE); -- Students 5,6 in class 10B1

-- -- Insert sample scores
-- INSERT IGNORE INTO scores (student_id, teacher_id, class_id, class_name, semester, year, ddgtx, ddggk, ddgck, tbm, comment, student_name, teacher_name) VALUES 
-- -- Semester 1 scores for class 10A1 (teacher_id=1, class_id=1)
-- (1, 1, 1, '10A1', 1, 2024, '8,9,7', 8, 9, 8, 'Khá', 'Nguyễn Văn An', 'Nguyễn Thị Thủy'),
-- (2, 1, 1, '10A1', 1, 2024, '9,8,9', 9, 8, 9, 'Giỏi', 'Trần Thị Bình', 'Nguyễn Thị Thủy'),

-- -- Semester 2 scores for class 10A1
-- (1, 1, 1, '10A1', 2, 2024, '9,8,8', 9, 8, 8, 'Khá', 'Nguyễn Văn An', 'Nguyễn Thị Thủy'),
-- (2, 1, 1, '10A1', 2, 2024, '9,9,10', 10, 9, 9, 'Xuất sắc', 'Trần Thị Bình', 'Nguyễn Thị Thủy'),

-- -- Semester 1 scores for class 10A2 (teacher_id=1, class_id=2)
-- (3, 1, 2, '10A2', 1, 2024, '7,8,6', 7, 8, 7, 'Trung bình', 'Lê Văn Cường', 'Nguyễn Thị Thủy'),
-- (4, 1, 2, '10A2', 1, 2024, '8,7,9', 8, 7, 8, 'Khá', 'Phạm Thị Dung', 'Nguyễn Thị Thủy'),

-- -- Semester 1 scores for class 10B1 (teacher_id=2, class_id=3)
-- (5, 2, 3, '10B1', 1, 2024, '9,9,8', 9, 9, 9, 'Giỏi', 'Hoàng Văn Em', 'Trần Văn Nam'),
-- (6, 2, 3, '10B1', 1, 2024, '8,8,7', 8, 8, 8, 'Khá', 'Đặng Thị Phương', 'Trần Văn Nam');
