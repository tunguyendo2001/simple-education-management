-- Migration Script 1: Add authentication fields to teachers table
-- File: V1__add_teacher_auth_fields.sql

ALTER TABLE teachers 
ADD COLUMN username VARCHAR(50) UNIQUE,
ADD COLUMN password_hash VARCHAR(255),
ADD COLUMN email VARCHAR(100),
ADD COLUMN is_active BOOLEAN DEFAULT TRUE,
ADD COLUMN last_login TIMESTAMP NULL;

-- Add indexes for performance
CREATE INDEX idx_teachers_username ON teachers(username);
CREATE INDEX idx_teachers_email ON teachers(email);
CREATE INDEX idx_teachers_active ON teachers(is_active);

-- Insert sample teacher data with authentication
INSERT INTO teachers (name, gender, hometown, birthday, username, password_hash, email, is_active) VALUES 
('Nguyễn Thị Thủy', 'WOMEN', 'Hà Nội', '1980-05-15', 'thuy.nguyen', '$2a$10$example.password.hash.here', 'thuy.nguyen@school.edu.vn', TRUE),
('Trần Văn Nam', 'MEN', 'Hồ Chí Minh', '1978-03-22', 'nam.tran', '$2a$10$example.password.hash.here', 'nam.tran@school.edu.vn', TRUE),
('Lê Thị Hoa', 'WOMEN', 'Đà Nẵng', '1985-09-10', 'hoa.le', '$2a$10$example.password.hash.here', 'hoa.le@school.edu.vn', TRUE);

-- Migration Script 2: Create classes table
-- File: V2__create_classes_table.sql

CREATE TABLE classes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    grade_level INT NOT NULL,
    academic_year INT NOT NULL,
    semester INT NOT NULL,
    subject VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Add indexes
CREATE INDEX idx_classes_name ON classes(name);
CREATE INDEX idx_classes_year_semester ON classes(academic_year, semester);
CREATE INDEX idx_classes_subject ON classes(subject);

-- Insert sample class data
INSERT INTO classes (name, grade_level, academic_year, semester, subject) VALUES 
('10A1', 10, 2024, 1, 'Tin học'),
('10A2', 10, 2024, 1, 'Tin học'),
('10B1', 10, 2024, 1, 'Tin học'),
('11A1', 11, 2024, 1, 'Tin học'),
('11A2', 11, 2024, 1, 'Tin học'),
('12A1', 12, 2024, 1, 'Tin học');

-- Migration Script 3: Create teacher_classes junction table
-- File: V3__create_teacher_classes_table.sql

CREATE TABLE teacher_classes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    teacher_id BIGINT NOT NULL,
    class_id BIGINT NOT NULL,
    assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (teacher_id) REFERENCES teachers(id) ON DELETE CASCADE,
    FOREIGN KEY (class_id) REFERENCES classes(id) ON DELETE CASCADE,
    UNIQUE KEY unique_teacher_class (teacher_id, class_id)
);

-- Add indexes for performance
CREATE INDEX idx_teacher_classes_teacher ON teacher_classes(teacher_id);
CREATE INDEX idx_teacher_classes_class ON teacher_classes(class_id);
CREATE INDEX idx_teacher_classes_active ON teacher_classes(is_active);

-- Insert sample teacher-class assignments
-- Assuming teacher IDs 1, 2, 3 and class IDs 1-6 exist
INSERT INTO teacher_classes (teacher_id, class_id, is_active) VALUES 
(1, 1, TRUE),  -- Nguyễn Thị Thủy teaches 10A1
(1, 2, TRUE),  -- Nguyễn Thị Thủy teaches 10A2
(2, 3, TRUE),  -- Trần Văn Nam teaches 10B1
(2, 4, TRUE),  -- Trần Văn Nam teaches 11A1
(3, 5, TRUE),  -- Lê Thị Hoa teaches 11A2
(3, 6, TRUE);  -- Lê Thị Hoa teaches 12A1

-- Migration Script 4: Create student_classes junction table
-- File: V4__create_student_classes_table.sql

CREATE TABLE student_classes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    class_id BIGINT NOT NULL,
    enrolled_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (class_id) REFERENCES classes(id) ON DELETE CASCADE,
    UNIQUE KEY unique_student_class (student_id, class_id)
);

-- Add indexes for performance
CREATE INDEX idx_student_classes_student ON student_classes(student_id);
CREATE INDEX idx_student_classes_class ON student_classes(class_id);
CREATE INDEX idx_student_classes_active ON student_classes(is_active);

-- Migration Script 5: Update scores table to use class_id
-- File: V5__update_scores_table.sql

-- Step 1: Add class_id column to scores table
ALTER TABLE scores 
ADD COLUMN class_id BIGINT;

-- Step 2: Update existing scores to map className to class_id
-- This assumes you have existing data with className values
UPDATE scores s 
JOIN classes c ON s.className = c.name 
SET s.class_id = c.id;

-- Step 3: Make class_id NOT NULL and add foreign key
ALTER TABLE scores 
MODIFY COLUMN class_id BIGINT NOT NULL,
ADD FOREIGN KEY (class_id) REFERENCES classes(id) ON DELETE CASCADE;

-- Step 4: Drop the old className column (optional, keep for transition period)
-- ALTER TABLE scores DROP COLUMN className;

-- Step 5: Add indexes for better performance
CREATE INDEX idx_scores_class_student ON scores(class_id, student_id);
CREATE INDEX idx_scores_teacher_class ON scores(teacher_id, class_id);
CREATE INDEX idx_scores_year_semester ON scores(year, semester);

-- Migration Script 6: Insert sample data
-- File: V6__insert_sample_data.sql

-- Insert sample students
INSERT INTO students (name, gender, hometown, birthday) VALUES 
('Nguyễn Văn An', 'Nam', 'Hà Nội', '2007-01-15'),
('Trần Thị Bình', 'Nữ', 'Hà Nội', '2007-02-20'),
('Lê Văn Cường', 'Nam', 'Hải Phòng', '2007-03-10'),
('Phạm Thị Dung', 'Nữ', 'Nam Định', '2007-04-05'),
('Hoàng Văn Em', 'Nam', 'Thái Bình', '2007-05-12'),
('Đặng Thị Phương', 'Nữ', 'Hà Nội', '2007-06-08');

-- Assign students to classes (assuming student IDs 1-6 and class IDs 1-6)
INSERT INTO student_classes (student_id, class_id, is_active) VALUES 
(1, 1, TRUE), (2, 1, TRUE), -- Students 1,2 in class 10A1
(3, 2, TRUE), (4, 2, TRUE), -- Students 3,4 in class 10A2
(5, 3, TRUE), (6, 3, TRUE); -- Students 5,6 in class 10B1

-- Insert sample scores with class_id
INSERT INTO scores (student_id, teacher_id, class_id, semester, year, ddgtx_string, ddggk, ddgck, tbm, comment, student_name, teacher_name) VALUES 
-- Semester 1 scores for class 10A1 (teacher_id=1, class_id=1)
(1, 1, 1, 1, 2024, '8,9,7', 8, 9, 8, 'Khá', 'Nguyễn Văn An', 'Nguyễn Thị Thủy'),
(2, 1, 1, 1, 2024, '9,8,9', 9, 8, 9, 'Giỏi', 'Trần Thị Bình', 'Nguyễn Thị Thủy'),

-- Semester 2 scores for class 10A1
(1, 1, 1, 2, 2024, '9,8,8', 9, 8, 8, 'Khá', 'Nguyễn Văn An', 'Nguyễn Thị Thủy'),
(2, 1, 1, 2, 2024, '9,9,10', 10, 9, 9, 'Xuất sắc', 'Trần Thị Bình', 'Nguyễn Thị Thủy'),

-- Semester 1 scores for class 10A2 (teacher_id=1, class_id=2)
(3, 1, 2, 1, 2024, '7,8,6', 7, 8, 7, 'Trung bình', 'Lê Văn Cường', 'Nguyễn Thị Thủy'),
(4, 1, 2, 1, 2024, '8,7,9', 8, 7, 8, 'Khá', 'Phạm Thị Dung', 'Nguyễn Thị Thủy');

-- Verification queries
-- File: V7__verification_queries.sql

-- Check teacher-class assignments
SELECT 
    t.name as teacher_name,
    c.name as class_name,
    tc.is_active,
    tc.assigned_at
FROM teachers t
JOIN teacher_classes tc ON t.id = tc.teacher_id
JOIN classes c ON tc.class_id = c.id
WHERE tc.is_active = TRUE
ORDER BY t.name, c.name;

-- Check student-class enrollments
SELECT 
    s.name as student_name,
    c.name as class_name,
    sc.is_active,
    sc.enrolled_at
FROM students s
JOIN student_classes sc ON s.id = sc.student_id
JOIN classes c ON sc.class_id = c.id
WHERE sc.is_active = TRUE
ORDER BY c.name, s.name;

-- Check scores with class information
SELECT 
    s.student_name,
    t.name as teacher_name,
    c.name as class_name,
    sc.semester,
    sc.year,
    sc.tbm,
    sc.comment
FROM scores sc
JOIN teachers t ON sc.teacher_id = t.id
JOIN classes c ON sc.class_id = c.id
ORDER BY c.name, sc.semester, s.student_name;
