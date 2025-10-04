-- Create database with proper character set
CREATE DATABASE IF NOT EXISTS education_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE education_db;

-- Grant privileges
GRANT ALL PRIVILEGES ON education_db.* TO 'mysql'@'%';
FLUSH PRIVILEGES;

-- ========== CREATE TABLES ==========

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

-- Create classes table with enhanced fields
CREATE TABLE IF NOT EXISTS classes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    class_name VARCHAR(100) NOT NULL UNIQUE, -- For backward compatibility
    grade_level INT NOT NULL,
    academic_year INT NOT NULL,
    semester VARCHAR(10) NOT NULL,
    subject VARCHAR(100) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create teacher_classes junction table with enhanced fields
CREATE TABLE IF NOT EXISTS teacher_classes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    teacher_id BIGINT NOT NULL,
    class_id BIGINT NOT NULL,
    subject VARCHAR(100) NOT NULL,
    academic_year INT NOT NULL,
    semester VARCHAR(20) NOT NULL,
    assignment_role ENUM('PRIMARY_TEACHER', 'SUBJECT_TEACHER', 'ASSISTANT_TEACHER', 'SUBSTITUTE_TEACHER') DEFAULT 'SUBJECT_TEACHER',
    is_primary_teacher BOOLEAN DEFAULT FALSE,
    assigned_by VARCHAR(100),
    assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (teacher_id) REFERENCES teachers(id) ON DELETE CASCADE,
    FOREIGN KEY (class_id) REFERENCES classes(id) ON DELETE CASCADE,
    UNIQUE KEY unique_teacher_class_subject (teacher_id, class_id, subject, academic_year, semester)
);

-- Create student_class_assignments table with enhanced fields
CREATE TABLE IF NOT EXISTS student_class_assignments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    class_id BIGINT NOT NULL,
    academic_year INT NOT NULL,
    semester VARCHAR(20) NOT NULL,
    student_number VARCHAR(20), -- Order number in class
    enrolled_by VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (class_id) REFERENCES classes(id) ON DELETE CASCADE,
    UNIQUE KEY unique_student_class_year_semester (student_id, class_id, academic_year, semester)
);

-- Create scores table with subject support
CREATE TABLE IF NOT EXISTS scores (
    id VARCHAR(500) PRIMARY KEY,
    student_id BIGINT,
    teacher_id BIGINT,
    class_id BIGINT,
    class_name VARCHAR(100) NOT NULL, -- Keep for backward compatibility
    subject VARCHAR(100) NOT NULL, -- Added subject field
    semester VARCHAR(10) NOT NULL,
    year INT NOT NULL,
    ddgtx TEXT, -- Store as comma-separated string
    ddggk INT DEFAULT 0,
    ddgck INT DEFAULT 0,
    tbm DECIMAL(3,1) DEFAULT 0.0, -- Changed to DECIMAL for better precision
    comment TEXT,
    student_name VARCHAR(255),
    teacher_name VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (teacher_id) REFERENCES teachers(id) ON DELETE CASCADE,
    FOREIGN KEY (class_id) REFERENCES classes(id) ON DELETE CASCADE
);

-- Create semester_schedules table
CREATE TABLE IF NOT EXISTS semester_schedules (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    schedule_name VARCHAR(255) NOT NULL,
    semester VARCHAR(10) NOT NULL,
    year INT NOT NULL,
    class_name VARCHAR(100) NOT NULL,
    start_date_time TIMESTAMP NOT NULL,
    end_date_time TIMESTAMP NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    is_locked BOOLEAN DEFAULT FALSE,
    description TEXT,
    created_by VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- ========== ADD CONSTRAINTS ==========

-- Add constraints for scores table
ALTER TABLE scores 
ADD CONSTRAINT chk_scores_subject_not_empty 
CHECK (subject IS NOT NULL AND subject != '');

ALTER TABLE scores 
ADD CONSTRAINT chk_scores_semester 
CHECK (semester IN ('1', '2', '3'));

ALTER TABLE scores 
ADD CONSTRAINT chk_scores_ddggk_range 
CHECK (ddggk >= 0 AND ddggk <= 10);

ALTER TABLE scores 
ADD CONSTRAINT chk_scores_ddgck_range 
CHECK (ddgck >= 0 AND ddgck <= 10);

ALTER TABLE scores 
ADD CONSTRAINT chk_scores_tbm_range 
CHECK (tbm >= 0 AND tbm <= 10);

ALTER TABLE scores 
ADD CONSTRAINT uniq_score 
UNIQUE (teacher_id, student_id, subject, semester, year);

-- Add constraints for semester_schedules
ALTER TABLE semester_schedules 
ADD CONSTRAINT chk_schedule_semester 
CHECK (semester IN ('1', '2', '3'));

ALTER TABLE semester_schedules 
ADD CONSTRAINT chk_schedule_dates 
CHECK (end_date_time > start_date_time);

-- ========== CREATE INDEXES FOR PERFORMANCE ==========

-- Teachers table indexes
ALTER TABLE teachers
  ADD INDEX idx_teachers_username (username),
  ADD INDEX idx_teachers_email (email),
  ADD INDEX idx_teachers_active (is_active),
  ADD INDEX idx_teachers_name (name);

-- Students table indexes
ALTER TABLE students
  ADD INDEX idx_students_name (name),
  ADD INDEX idx_students_gender (gender),
  ADD INDEX idx_students_birthday (birthday);

-- Classes table indexes
ALTER TABLE classes
  ADD INDEX idx_classes_name (name),
  ADD INDEX idx_classes_class_name (class_name),
  ADD INDEX idx_classes_year_semester (academic_year, semester),
  ADD INDEX idx_classes_subject (subject),
  ADD INDEX idx_classes_active (is_active);

-- Teacher_classes table indexes
ALTER TABLE teacher_classes
  ADD INDEX idx_teacher_classes_teacher (teacher_id),
  ADD INDEX idx_teacher_classes_class (class_id),
  ADD INDEX idx_teacher_classes_active (is_active),
  ADD INDEX idx_teacher_classes_subject (subject),
  ADD INDEX idx_teacher_classes_year_semester (academic_year, semester),
  ADD INDEX idx_teacher_classes_composite (teacher_id, class_id, subject, academic_year, semester);

-- Student_class_assignments table indexes
ALTER TABLE student_class_assignments
  ADD INDEX idx_student_assignments_student (student_id),
  ADD INDEX idx_student_assignments_class (class_id),
  ADD INDEX idx_student_assignments_active (is_active),
  ADD INDEX idx_student_assignments_year_semester (academic_year, semester),
  ADD INDEX idx_student_assignments_composite (student_id, class_id, academic_year, semester);

-- Scores table indexes (comprehensive indexing for performance)
ALTER TABLE scores
  ADD INDEX idx_scores_student (student_id),
  ADD INDEX idx_scores_teacher (teacher_id),
  ADD INDEX idx_scores_class (class_id),
  ADD INDEX idx_scores_class_name (class_name),
  ADD INDEX idx_scores_subject (subject),
  ADD INDEX idx_scores_year_semester (year, semester),
  ADD INDEX idx_scores_tbm (tbm),
  ADD INDEX idx_scores_class_subject (class_name, subject),
  ADD INDEX idx_scores_teacher_subject (teacher_id, subject),
  ADD INDEX idx_scores_class_subject_year_semester (class_name, subject, year, semester),
  ADD INDEX idx_scores_composite (teacher_id, class_name, subject, year, semester),
  ADD INDEX idx_scores_student_subject (student_id, subject),
  ADD INDEX idx_scores_teacher_class (teacher_id, class_name),
  ADD INDEX idx_scores_student_year_semester (student_id, year, semester),
  ADD INDEX idx_scores_created_updated (created_at, updated_at);

-- Semester_schedules table indexes
ALTER TABLE semester_schedules
  ADD INDEX idx_schedule_class_name (class_name),
  ADD INDEX idx_schedule_year_semester (year, semester),
  ADD INDEX idx_schedule_active (is_active),
  ADD INDEX idx_schedule_locked (is_locked),
  ADD INDEX idx_schedule_dates (start_date_time, end_date_time),
  ADD INDEX idx_schedule_composite (semester, year, class_name, is_active);

-- ========== INSERT SAMPLE DATA ==========

-- Clear existing data (in reverse dependency order) if needed
-- DELETE FROM scores;
-- DELETE FROM semester_schedules;
-- DELETE FROM student_class_assignments;
-- DELETE FROM teacher_classes;
-- DELETE FROM classes;
-- DELETE FROM students;
-- DELETE FROM teachers;

-- Reset auto-increment counters
ALTER TABLE teachers AUTO_INCREMENT = 1;
ALTER TABLE students AUTO_INCREMENT = 1;
ALTER TABLE classes AUTO_INCREMENT = 1;
ALTER TABLE teacher_classes AUTO_INCREMENT = 1;
ALTER TABLE student_class_assignments AUTO_INCREMENT = 1;
ALTER TABLE scores AUTO_INCREMENT = 1;
ALTER TABLE semester_schedules AUTO_INCREMENT = 1;

-- Insert Teachers (passwords are bcrypt hashes of '123456')
INSERT INTO teachers (name, gender, hometown, birthday, username, password_hash, email, is_active, created_at, updated_at) VALUES
('Nguyễn Thị Thủy', 'WOMEN', 'Hà Nội', '1985-03-15', 'thuynguyen', '$2a$10$VA8kgIIzdChsNzKerGB7/.u.V3EpBsq50fKY.w7OVxHxYR.nOGIyW', 'thuy.nguyen@school.edu.vn', true, '2023-01-15', '2024-01-15'),
('Trần Văn Minh', 'MEN', 'Hồ Chí Minh', '1982-07-22', 'minhtran', '$2a$10$VA8kgIIzdChsNzKerGB7/.u.V3EpBsq50fKY.w7OVxHxYR.nOGIyW', 'minh.tran@school.edu.vn', true, '2023-01-15', '2024-01-15'),
('Phạm Thị Lan', 'WOMEN', 'Đà Nẵng', '1988-11-08', 'lanpham', '$2a$10$VA8kgIIzdChsNzKerGB7/.u.V3EpBsq50fKY.w7OVxHxYR.nOGIyW', 'lan.pham@school.edu.vn', true, '2023-01-15', '2024-01-15'),
('Lê Văn Đức', 'MEN', 'Hải Phòng', '1980-12-03', 'ducle', '$2a$10$VA8kgIIzdChsNzKerGB7/.u.V3EpBsq50fKY.w7OVxHxYR.nOGIyW', 'duc.le@school.edu.vn', true, '2023-01-15', '2024-01-15'),
('Hoàng Thị Mai', 'WOMEN', 'Huế', '1987-05-20', 'maihoang', '$2a$10$VA8kgIIzdChsNzKerGB7/.u.V3EpBsq50fKY.w7OVxHxYR.nOGIyW', 'mai.hoang@school.edu.vn', true, '2023-01-15', '2024-01-15');

-- Insert Students (Middle school ages: 2010-2011 births)
INSERT INTO students (name, gender, hometown, birthday, created_at, updated_at) VALUES
('Nguyễn Văn An', 'Nam', 'Hà Nội', '2010-01-15', '2024-09-01', '2024-09-01'),
('Trần Thị Bình', 'Nữ', 'Hồ Chí Minh', '2010-02-20', '2024-09-01', '2024-09-01'),
('Lê Văn Cường', 'Nam', 'Đà Nẵng', '2010-03-10', '2024-09-01', '2024-09-01'),
('Phạm Thị Dung', 'Nữ', 'Hải Phòng', '2010-04-25', '2024-09-01', '2024-09-01'),
('Hoàng Văn Em', 'Nam', 'Cần Thơ', '2010-05-30', '2024-09-01', '2024-09-01'),
('Vũ Thị Giang', 'Nữ', 'Nha Trang', '2010-06-12', '2024-09-01', '2024-09-01'),
('Đỗ Văn Hùng', 'Nam', 'Vũng Tàu', '2010-07-08', '2024-09-01', '2024-09-01'),
('Bùi Thị Linh', 'Nữ', 'Đà Lạt', '2010-08-22', '2024-09-01', '2024-09-01'),
('Ngô Văn Khang', 'Nam', 'Quảng Ninh', '2010-09-15', '2024-09-01', '2024-09-01'),
('Cao Thị Mai', 'Nữ', 'Thái Nguyên', '2010-10-05', '2024-09-01', '2024-09-01'),
('Đinh Văn Nam', 'Nam', 'Vinh', '2010-11-18', '2024-09-01', '2024-09-01'),
('Tạ Thị Oanh', 'Nữ', 'Huế', '2010-12-03', '2024-09-01', '2024-09-01'),
('Lý Văn Phong', 'Nam', 'Biên Hòa', '2011-01-20', '2024-09-01', '2024-09-01'),
('Dương Thị Quỳnh', 'Nữ', 'Long Xuyên', '2011-02-14', '2024-09-01', '2024-09-01'),
('Võ Văn Sơn', 'Nam', 'Rạch Giá', '2011-03-28', '2024-09-01', '2024-09-01'),
('Phan Thị Trang', 'Nữ', 'Phan Thiết', '2011-04-16', '2024-09-01', '2024-09-01'),
('Mai Văn Tuấn', 'Nam', 'Thanh Hóa', '2011-05-11', '2024-09-01', '2024-09-01'),
('Chu Thị Uyên', 'Nữ', 'Nam Định', '2011-06-25', '2024-09-01', '2024-09-01'),
('Hà Văn Việt', 'Nam', 'Hạ Long', '2011-07-19', '2024-09-01', '2024-09-01'),
('Lài Thị Xuân', 'Nữ', 'Sapa', '2011-08-30', '2024-09-01', '2024-09-01');

-- Insert Classes (Middle school grades 7-9 with Vietnamese subjects)
INSERT INTO classes (name, class_name, grade_level, academic_year, semester, subject, is_active, created_at, updated_at) VALUES
('8A1', '8A1', 8, 2024, '1', 'Tin học', true, '2024-08-15', '2024-08-15'),
('8A2', '8A2', 8, 2024, '1', 'Tin học', true, '2024-08-15', '2024-08-15'),
('8B1', '8B1', 8, 2024, '1', 'Toán học', true, '2024-08-15', '2024-08-15'),
('9A1', '9A1', 9, 2024, '1', 'Tin học', true, '2024-08-15', '2024-08-15'),
('9A2', '9A2', 9, 2024, '1', 'Văn học', true, '2024-08-15', '2024-08-15'),
('7A1', '7A1', 7, 2024, '1', 'Tin học', true, '2024-08-15', '2024-08-15'),
('7B1', '7B1', 7, 2024, '1', 'Tiếng Anh', true, '2024-08-15', '2024-08-15'),
('8C1', '8C1', 8, 2024, '1', 'Vật lý', true, '2024-08-15', '2024-08-15');

-- Insert Teacher-Class Assignments
INSERT INTO teacher_classes (teacher_id, class_id, subject, academic_year, semester, assignment_role, is_primary_teacher, assigned_by, is_active, assigned_at, updated_at) VALUES
(1, 1, 'Tin học', 2024, '1', 'PRIMARY_TEACHER', true, 'Admin', true, '2024-08-20', '2024-08-20'),
(1, 2, 'Tin học', 2024, '1', 'SUBJECT_TEACHER', false, 'Admin', true, '2024-08-20', '2024-08-20'),
(1, 4, 'Tin học', 2024, '1', 'SUBJECT_TEACHER', false, 'Admin', true, '2024-08-20', '2024-08-20'),
(1, 6, 'Tin học', 2024, '1', 'SUBJECT_TEACHER', false, 'Admin', true, '2024-08-20', '2024-08-20'),
(2, 3, 'Toán học', 2024, '1', 'PRIMARY_TEACHER', true, 'Admin', true, '2024-08-20', '2024-08-20'),
(2, 8, 'Vật lý', 2024, '1', 'SUBJECT_TEACHER', false, 'Admin', true, '2024-08-20', '2024-08-20'),
(3, 1, 'Văn học', 2024, '1', 'PRIMARY_TEACHER', true, 'Admin', true, '2024-08-20', '2024-08-20'),
(3, 5, 'Văn học', 2024, '1', 'PRIMARY_TEACHER', true, 'Admin', true, '2024-08-20', '2024-08-20'),
(4, 7, 'Tiếng Anh', 2024, '1', 'PRIMARY_TEACHER', true, 'Admin', true, '2024-08-20', '2024-08-20'),
(5, 1, 'Chủ nhiệm', 2024, 'BOTH', 'PRIMARY_TEACHER', true, 'Admin', true, '2024-08-20', '2024-08-20');

-- Insert Student-Class Assignments (distributed across different classes)
INSERT INTO student_class_assignments (student_id, class_id, academic_year, semester, is_active, student_number, enrolled_by, created_at, updated_at) VALUES
-- Class 8A1 (Class ID: 1)
(1, 1, 2024, '1', true, '01', 'Admin', '2024-09-01', '2024-09-01'),
(2, 1, 2024, '1', true, '02', 'Admin', '2024-09-01', '2024-09-01'),
(3, 1, 2024, '1', true, '03', 'Admin', '2024-09-01', '2024-09-01'),
(4, 1, 2024, '1', true, '04', 'Admin', '2024-09-01', '2024-09-01'),
(5, 1, 2024, '1', true, '05', 'Admin', '2024-09-01', '2024-09-01'),
-- Class 8A2 (Class ID: 2)
(6, 2, 2024, '1', true, '01', 'Admin', '2024-09-01', '2024-09-01'),
(7, 2, 2024, '1', true, '02', 'Admin', '2024-09-01', '2024-09-01'),
(8, 2, 2024, '1', true, '03', 'Admin', '2024-09-01', '2024-09-01'),
(9, 2, 2024, '1', true, '04', 'Admin', '2024-09-01', '2024-09-01'),
(10, 2, 2024, '1', true, '05', 'Admin', '2024-09-01', '2024-09-01'),
-- Class 8B1 (Class ID: 3)
(11, 3, 2024, '1', true, '01', 'Admin', '2024-09-01', '2024-09-01'),
(12, 3, 2024, '1', true, '02', 'Admin', '2024-09-01', '2024-09-01'),
(13, 3, 2024, '1', true, '03', 'Admin', '2024-09-01', '2024-09-01'),
(14, 3, 2024, '1', true, '04', 'Admin', '2024-09-01', '2024-09-01'),
-- Class 9A1 (Class ID: 4)
(15, 4, 2024, '1', true, '01', 'Admin', '2024-09-01', '2024-09-01'),
(16, 4, 2024, '1', true, '02', 'Admin', '2024-09-01', '2024-09-01'),
(17, 4, 2024, '1', true, '03', 'Admin', '2024-09-01', '2024-09-01'),
-- Class 7A1 (Class ID: 6)
(18, 6, 2024, '1', true, '01', 'Admin', '2024-09-01', '2024-09-01'),
(19, 6, 2024, '1', true, '02', 'Admin', '2024-09-01', '2024-09-01'),
(20, 6, 2024, '1', true, '03', 'Admin', '2024-09-01', '2024-09-01');

-- Insert Scores (with subject field matching the assignments)

INSERT INTO scores (id, student_id, teacher_id, class_id, class_name, subject, semester, year, ddgtx, ddggk, ddgck, tbm, comment, student_name, teacher_name, created_at, updated_at) VALUES
-- Scores for Class 8A1 - Computer Science (Tin học) - Teacher Nguyễn Thị Thủy (ID: 1) - Semester 1
('1_1_2024_1_8a1_tinhoc', 1, 1, 1, '8A1', 'Tin học', '1', 2024, '8,9,7,8', 8, 9, 8.0, 'Học sinh có tiến bộ rõ rệt', 'Nguyễn Văn An', 'Nguyễn Thị Thủy', '2024-09-15', '2024-09-15'),
('1_2_2024_1_8a1_tinhoc', 2, 1, 1, '8A1', 'Tin học', '1', 2024, '9,8,9,10', 9, 9, 9.0, 'Rất tốt, tiếp tục phát huy', 'Trần Thị Bình', 'Nguyễn Thị Thủy', '2024-09-15', '2024-09-15'),
('1_3_2024_1_8a1_tinhoc', 3, 1, 1, '8A1', 'Tin học', '1', 2024, '7,6,7,8', 7, 8, 7.0, 'Cần cố gắng hơn', 'Lê Văn Cường', 'Nguyễn Thị Thủy', '2024-09-15', '2024-09-15'),
('1_4_2024_1_8a1_tinhoc', 4, 1, 1, '8A1', 'Tin học', '1', 2024, '8,8,9,8', 8, 8, 8.0, 'Kết quả ổn định', 'Phạm Thị Dung', 'Nguyễn Thị Thủy', '2024-09-15', '2024-09-15'),
('1_5_2024_1_8a1_tinhoc', 5, 1, 1, '8A1', 'Tin học', '1', 2024, '6,7,6,7', 6, 7, 7.0, 'Cần bổ sung kiến thức cơ bản', 'Hoàng Văn Em', 'Nguyễn Thị Thủy', '2024-09-15', '2024-09-15'),

-- Scores for Class 8A2 - Computer Science (Tin học) - Teacher Nguyễn Thị Thủy (ID: 1) - Semester 1
('1_6_2024_1_8a2_tinhoc', 6, 1, 2, '8A2', 'Tin học', '1', 2024, '9,9,8,10', 9, 10, 9.0, 'Xuất sắc', 'Vũ Thị Giang', 'Nguyễn Thị Thủy', '2024-09-15', '2024-09-15'),
('1_7_2024_1_8a2_tinhoc', 7, 1, 2, '8A2', 'Tin học', '1', 2024, '8,7,8,9', 8, 8, 8.0, 'Tốt', 'Đỗ Văn Hùng', 'Nguyễn Thị Thủy', '2024-09-15', '2024-09-15'),
('1_8_2024_1_8a2_tinhoc', 8, 1, 2, '8A2', 'Tin học', '1', 2024, '7,8,7,8', 7, 8, 8.0, 'Khá', 'Bùi Thị Linh', 'Nguyễn Thị Thủy', '2024-09-15', '2024-09-15'),
('1_9_2024_1_8a2_tinhoc', 9, 1, 2, '8A2', 'Tin học', '1', 2024, '8,9,8,9', 8, 9, 9.0, 'Tốt', 'Ngô Văn Khang', 'Nguyễn Thị Thủy', '2024-09-15', '2024-09-15'),
('1_10_2024_1_8a2_tinhoc', 10, 1, 2, '8A2', 'Tin học', '1', 2024, '9,10,9,9', 9, 10, 10.0, 'Rất tốt', 'Cao Thị Mai', 'Nguyễn Thị Thủy', '2024-09-15', '2024-09-15'),

-- Scores for Class 8B1 - Mathematics (Toán học) - Teacher Trần Văn Minh (ID: 2) - Semester 1
('2_11_2024_1_8b1_toanhoc', 11, 2, 3, '8B1', 'Toán học', '1', 2024, '8,9,8,9', 8, 9, 8.0, 'Giỏi toán', 'Đinh Văn Nam', 'Trần Văn Minh', '2024-09-15', '2024-09-15'),
('2_12_2024_1_8b1_toanhoc', 12, 2, 3, '8B1', 'Toán học', '1', 2024, '7,7,8,7', 7, 8, 7.0, 'Khá', 'Tạ Thị Oanh', 'Trần Văn Minh', '2024-09-15', '2024-09-15'),
('2_13_2024_1_8b1_toanhoc', 13, 2, 3, '8B1', 'Toán học', '1', 2024, '9,8,9,10', 9, 9, 9.0, 'Rất tốt', 'Lý Văn Phong', 'Trần Văn Minh', '2024-09-15', '2024-09-15'),
('2_14_2024_1_8b1_toanhoc', 14, 2, 3, '8B1', 'Toán học', '1', 2024, '6,7,6,7', 6, 7, 7.0, 'Trung bình', 'Dương Thị Quỳnh', 'Trần Văn Minh', '2024-09-15', '2024-09-15'),

-- Scores for Class 9A1 - Computer Science (Tin học) - Teacher Nguyễn Thị Thủy (ID: 1) - Semester 1
('1_15_2024_1_9a1_tinhoc', 15, 1, 4, '9A1', 'Tin học', '1', 2024, '10,9,10,9', 10, 10, 10.0, 'Xuất sắc', 'Võ Văn Sơn', 'Nguyễn Thị Thủy', '2024-09-15', '2024-09-15'),
('1_16_2024_1_9a1_tinhoc', 16, 1, 4, '9A1', 'Tin học', '1', 2024, '8,9,8,8', 8, 9, 8.0, 'Tốt', 'Phan Thị Trang', 'Nguyễn Thị Thủy', '2024-09-15', '2024-09-15'),
('1_17_2024_1_9a1_tinhoc', 17, 1, 4, '9A1', 'Tin học', '1', 2024, '9,9,9,8', 9, 9, 9.0, 'Rất tốt', 'Mai Văn Tuấn', 'Nguyễn Thị Thủy', '2024-09-15', '2024-09-15'),

-- Scores for Class 7A1 - Computer Science (Tin học) - Teacher Nguyễn Thị Thủy (ID: 1) - Semester 1
('1_18_2024_1_7a1_tinhoc', 18, 1, 6, '7A1', 'Tin học', '1', 2024, '7,8,7,8', 7, 8, 8.0, 'Khá', 'Chu Thị Uyên', 'Nguyễn Thị Thủy', '2024-09-15', '2024-09-15'),
('1_19_2024_1_7a1_tinhoc', 19, 1, 6, '7A1', 'Tin học', '1', 2024, '8,8,9,8', 8, 8, 8.0, 'Tốt', 'Hà Văn Việt', 'Nguyễn Thị Thủy', '2024-09-15', '2024-09-15'),
('1_20_2024_1_7a1_tinhoc', 20, 1, 6, '7A1', 'Tin học', '1', 2024, '9,8,9,9', 9, 9, 9.0, 'Rất tốt', 'Lài Thị Xuân', 'Nguyễn Thị Thủy', '2024-09-15', '2024-09-15'),

-- Scores for Class 8A1 - Computer Science (Tin học) - Teacher Nguyễn Thị Thủy (ID: 1) - Semester 2
('1_1_2024_2_8a1_tinhoc', 1, 1, 1, '8A1', 'Tin học', '2', 2024, '8,9,8,9', 8, 9, 8.5, 'Tiếp tục phát huy trong học kỳ 2', 'Nguyễn Văn An', 'Nguyễn Thị Thủy', '2024-03-15', '2024-03-15'),
('1_2_2024_2_8a1_tinhoc', 2, 1, 1, '8A1', 'Tin học', '2', 2024, '9,9,10,9', 9, 10, 9.3, 'Xuất sắc, có tiến bộ vượt bậc', 'Trần Thị Bình', 'Nguyễn Thị Thủy', '2024-03-15', '2024-03-15'),
('1_3_2024_2_8a1_tinhoc', 3, 1, 1, '8A1', 'Tin học', '2', 2024, '7,8,7,8', 7, 8, 7.5, 'Có cải thiện so với học kỳ 1', 'Lê Văn Cường', 'Nguyễn Thị Thủy', '2024-03-15', '2024-03-15'),
('1_4_2024_2_8a1_tinhoc', 4, 1, 1, '8A1', 'Tin học', '2', 2024, '8,9,8,8', 8, 9, 8.3, 'Duy trì kết quả tốt', 'Phạm Thị Dung', 'Nguyễn Thị Thủy', '2024-03-15', '2024-03-15'),
('1_5_2024_2_8a1_tinhoc', 5, 1, 1, '8A1', 'Tin học', '2', 2024, '7,8,7,8', 7, 8, 7.5, 'Có sự tiến bộ đáng kể', 'Hoàng Văn Em', 'Nguyễn Thị Thủy', '2024-03-15', '2024-03-15'),

-- Scores for Class 8A2 - Computer Science (Tin học) - Teacher Nguyễn Thị Thủy (ID: 1) - Semester 2
('1_6_2024_2_8a2_tinhoc', 6, 1, 2, '8A2', 'Tin học', '2', 2024, '9,10,9,10', 10, 10, 9.7, 'Duy trì phong độ xuất sắc', 'Vũ Thị Giang', 'Nguyễn Thị Thủy', '2024-03-15', '2024-03-15'),
('1_7_2024_2_8a2_tinhoc', 7, 1, 2, '8A2', 'Tin học', '2', 2024, '8,8,9,8', 8, 9, 8.3, 'Ổn định và tốt', 'Đỗ Văn Hùng', 'Nguyễn Thị Thủy', '2024-03-15', '2024-03-15'),
('1_8_2024_2_8a2_tinhoc', 8, 1, 2, '8A2', 'Tin học', '2', 2024, '8,8,8,9', 8, 9, 8.5, 'Có tiến bộ rõ rệt', 'Bùi Thị Linh', 'Nguyễn Thị Thủy', '2024-03-15', '2024-03-15'),
('1_9_2024_2_8a2_tinhoc', 9, 1, 2, '8A2', 'Tin học', '2', 2024, '9,9,8,9', 9, 9, 9.0, 'Duy trì kết quả tốt', 'Ngô Văn Khang', 'Nguyễn Thị Thủy', '2024-03-15', '2024-03-15'),
('1_10_2024_2_8a2_tinhoc', 10, 1, 2, '8A2', 'Tin học', '2', 2024, '10,10,9,10', 10, 10, 10.0, 'Hoàn hảo trong cả năm học', 'Cao Thị Mai', 'Nguyễn Thị Thủy', '2024-03-15', '2024-03-15'),

-- Scores for Class 8B1 - Mathematics (Toán học) - Teacher Trần Văn Minh (ID: 2) - Semester 2
('2_11_2024_2_8b1_toanhoc', 11, 2, 3, '8B1', 'Toán học', '2', 2024, '9,9,8,9', 9, 9, 9.0, 'Rất giỏi toán, tiếp tục phát huy', 'Đinh Văn Nam', 'Trần Văn Minh', '2024-03-15', '2024-03-15'),
('2_12_2024_2_8b1_toanhoc', 12, 2, 3, '8B1', 'Toán học', '2', 2024, '7,8,8,8', 8, 8, 7.8, 'Có tiến bộ trong học kỳ 2', 'Tạ Thị Oanh', 'Trần Văn Minh', '2024-03-15', '2024-03-15'),
('2_13_2024_2_8b1_toanhoc', 13, 2, 3, '8B1', 'Toán học', '2', 2024, '9,10,9,10', 10, 10, 9.5, 'Xuất sắc, học sinh năng khiếu', 'Lý Văn Phong', 'Trần Văn Minh', '2024-03-15', '2024-03-15'),
('2_14_2024_2_8b1_toanhoc', 14, 2, 3, '8B1', 'Toán học', '2', 2024, '7,7,8,8', 7, 8, 7.5, 'Có cải thiện so với HK1', 'Dương Thị Quỳnh', 'Trần Văn Minh', '2024-03-15', '2024-03-15'),

-- Scores for Class 9A1 - Computer Science (Tin học) - Teacher Nguyễn Thị Thủy (ID: 1) - Semester 2
('1_15_2024_2_9a1_tinhoc', 15, 1, 4, '9A1', 'Tin học', '2', 2024, '10,10,10,9', 10, 10, 10.0, 'Duy trì đẳng cấp xuất sắc', 'Võ Văn Sơn', 'Nguyễn Thị Thủy', '2024-03-15', '2024-03-15'),
('1_16_2024_2_9a1_tinhoc', 16, 1, 4, '9A1', 'Tin học', '2', 2024, '9,9,8,9', 9, 9, 9.0, 'Rất tốt, ổn định', 'Phan Thị Trang', 'Nguyễn Thị Thủy', '2024-03-15', '2024-03-15'),
('1_17_2024_2_9a1_tinhoc', 17, 1, 4, '9A1', 'Tin học', '2', 2024, '9,10,9,9', 9, 10, 9.3, 'Duy trì phong độ cao', 'Mai Văn Tuấn', 'Nguyễn Thị Thủy', '2024-03-15', '2024-03-15'),

-- Scores for Class 7A1 - Computer Science (Tin học) - Teacher Nguyễn Thị Thủy (ID: 1) - Semester 2
('1_18_2024_2_7a1_tinhoc', 18, 1, 6, '7A1', 'Tin học', '2', 2024, '8,8,8,9', 8, 9, 8.3, 'Có tiến bộ tốt trong HK2', 'Chu Thị Uyên', 'Nguyễn Thị Thủy', '2024-03-15', '2024-03-15'),
('1_19_2024_2_7a1_tinhoc', 19, 1, 6, '7A1', 'Tin học', '2', 2024, '8,9,9,8', 8, 9, 8.5, 'Duy trì kết quả tốt', 'Hà Văn Việt', 'Nguyễn Thị Thủy', '2024-03-15', '2024-03-15'),
('1_20_2024_2_7a1_tinhoc', 20, 1, 6, '7A1', 'Tin học', '2', 2024, '9,9,10,9', 9, 10, 9.3, 'Rất tốt, có tiến bộ', 'Lài Thị Xuân', 'Nguyễn Thị Thủy', '2024-03-15', '2024-03-15');

-- Insert Semester Schedules (Vietnamese descriptions)
INSERT INTO semester_schedules (schedule_name, semester, year, class_name, start_date_time, end_date_time, is_active, is_locked, description, created_by, created_at, updated_at) VALUES
('Học kỳ 1 - Nhập điểm', '1', 2024, '8A1', '2024-09-01 00:00:00', '2024-12-31 23:59:59', true, false, 'Thời gian nhập điểm học kỳ 1 cho lớp 8A1', 'Admin', '2024-08-25', '2024-08-25'),
('Học kỳ 1 - Nhập điểm', '1', 2024, '8A2', '2024-09-01 00:00:00', '2024-12-31 23:59:59', true, false, 'Thời gian nhập điểm học kỳ 1 cho lớp 8A2', 'Admin', '2024-08-25', '2024-08-25'),
('Học kỳ 1 - Nhập điểm', '1', 2024, '8B1', '2024-09-01 00:00:00', '2024-12-31 23:59:59', true, false, 'Thời gian nhập điểm học kỳ 1 cho lớp 8B1', 'Admin', '2024-08-25', '2024-08-25'),
('Học kỳ 1 - Nhập điểm', '1', 2024, '9A1', '2024-09-01 00:00:00', '2024-12-31 23:59:59', true, false, 'Thời gian nhập điểm học kỳ 1 cho lớp 9A1', 'Admin', '2024-08-25', '2024-08-25'),
('Học kỳ 1 - Nhập điểm', '1', 2024, '7A1', '2024-09-01 00:00:00', '2024-12-31 23:59:59', true, false, 'Thời gian nhập điểm học kỳ 1 cho lớp 7A1', 'Admin', '2024-08-25', '2024-08-25'),
('Học kỳ 2 - Nhập điểm', '2', 2025, '8A1', '2025-01-15 00:00:00', '2025-05-31 23:59:59', false, false, 'Thời gian nhập điểm học kỳ 2 cho lớp 8A1', 'Admin', '2024-12-01', '2024-12-01'),
('Học kỳ 2 - Nhập điểm', '2', 2025, '8A2', '2025-01-15 00:00:00', '2025-05-31 23:59:59', false, false, 'Thời gian nhập điểm học kỳ 2 cho lớp 8A2', 'Admin', '2024-12-01', '2024-12-01');

-- ========== VERIFICATION QUERIES ==========

-- Display record counts for verification
SELECT 'Teachers Count' as TableName, COUNT(*) as RecordCount FROM teachers
UNION ALL
SELECT 'Students Count', COUNT(*) FROM students
UNION ALL
SELECT 'Classes Count', COUNT(*) FROM classes
UNION ALL
SELECT 'Teacher Assignments Count', COUNT(*) FROM teacher_classes
UNION ALL
SELECT 'Student Assignments Count', COUNT(*) FROM student_class_assignments
UNION ALL
SELECT 'Scores Count', COUNT(*) FROM scores
UNION ALL
SELECT 'Semester Schedules Count', COUNT(*) FROM semester_schedules;

-- ========== SAMPLE VERIFICATION QUERIES ==========

-- Show teacher-class assignments with subjects
SELECT 
    t.name as teacher_name,
    c.class_name,
    tc.subject,
    tc.academic_year,
    tc.semester,
    tc.assignment_role,
    tc.is_primary_teacher
FROM teachers t 
JOIN teacher_classes tc ON t.id = tc.teacher_id 
JOIN classes c ON tc.class_id = c.id
WHERE tc.is_active = true
ORDER BY t.name, c.class_name;

-- Show student enrollments by class
SELECT 
    c.class_name,
    c.subject as class_subject,
    COUNT(sca.student_id) as student_count
FROM classes c
LEFT JOIN student_class_assignments sca ON c.id = sca.class_id AND sca.is_active = true
WHERE c.is_active = true
GROUP BY c.id, c.class_name, c.subject
ORDER BY c.class_name;

-- Show scores summary by class and subject
SELECT 
    s.class_name,
    s.subject,
    s.teacher_name,
    COUNT(*) as student_count,
    ROUND(AVG(s.tbm), 2) as average_score,
    MIN(s.tbm) as min_score,
    MAX(s.tbm) as max_score
FROM scores s
GROUP BY s.class_name, s.subject, s.teacher_name
ORDER BY s.class_name, s.subject;

-- Show active semester schedules
SELECT 
    schedule_name,
    class_name,
    semester,
    year,
    DATE_FORMAT(start_date_time, '%Y-%m-%d %H:%i') as start_time,
    DATE_FORMAT(end_date_time, '%Y-%m-%d %H:%i') as end_time,
    CASE 
        WHEN is_active = 1 THEN 'Active' 
        ELSE 'Inactive' 
    END as status,
    CASE 
        WHEN is_locked = 1 THEN 'Locked' 
        ELSE 'Open' 
    END as entry_status
FROM semester_schedules
ORDER BY year DESC, semester DESC, class_name;

-- ========== ADD COMMENTS TO TABLES ==========

ALTER TABLE teachers 
COMMENT = 'Teachers table with authentication and profile information';

ALTER TABLE students 
COMMENT = 'Students table with basic profile information';

ALTER TABLE classes 
COMMENT = 'School classes with academic year, semester, and subject information';

ALTER TABLE teacher_classes 
COMMENT = 'Teacher-class assignments with subject and role information';

ALTER TABLE student_class_assignments 
COMMENT = 'Student-class assignments with academic year and semester tracking';

ALTER TABLE scores 
COMMENT = 'Student scores table with subject support and comprehensive grading data';

ALTER TABLE semester_schedules 
COMMENT = 'Semester schedules for controlling score entry periods';

-- ========== FINAL COMMIT ==========
COMMIT;