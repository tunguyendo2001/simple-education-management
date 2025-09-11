-- Corrected Sample Data for Student Management System
-- Clear existing data (in reverse dependency order)
DELETE FROM scores;
DELETE FROM semester_schedules;
DELETE FROM student_class_assignments;
DELETE FROM teacher_classes;
DELETE FROM classes;
DELETE FROM students;
DELETE FROM teachers;

-- Reset auto-increment counters
ALTER TABLE teachers AUTO_INCREMENT = 1;
ALTER TABLE students AUTO_INCREMENT = 1;
ALTER TABLE classes AUTO_INCREMENT = 1;
ALTER TABLE teacher_classes AUTO_INCREMENT = 1;
ALTER TABLE student_class_assignments AUTO_INCREMENT = 1;
ALTER TABLE scores AUTO_INCREMENT = 1;
ALTER TABLE semester_schedules AUTO_INCREMENT = 1;

-- Insert Teachers, passwords are bcrypt hashes of '123456'
INSERT INTO teachers (name, gender, hometown, birthday, username, password_hash, email, is_active, created_at, updated_at) VALUES
('Nguyễn Thị Thủy', 'WOMEN', 'Hà Nội', '1985-03-15', 'thuynguyen', '$2a$10$VA8kgIIzdChsNzKerGB7/.u.V3EpBsq50fKY.w7OVxHxYR.nOGIyW', 'thuy.nguyen@school.edu.vn', true, '2023-01-15', '2024-01-15'),
('Trần Văn Minh', 'MEN', 'Hồ Chí Minh', '1982-07-22', 'minhtran', '$2a$10$VA8kgIIzdChsNzKerGB7/.u.V3EpBsq50fKY.w7OVxHxYR.nOGIyW', 'minh.tran@school.edu.vn', true, '2023-01-15', '2024-01-15'),
('Phạm Thị Lan', 'WOMEN', 'Đà Nẵng', '1988-11-08', 'lanpham', '$2a$10$VA8kgIIzdChsNzKerGB7/.u.V3EpBsq50fKY.w7OVxHxYR.nOGIyW', 'lan.pham@school.edu.vn', true, '2023-01-15', '2024-01-15'),
('Lê Văn Đức', 'MEN', 'Hải Phòng', '1980-12-03', 'ducle', '$2a$10$VA8kgIIzdChsNzKerGB7/.u.V3EpBsq50fKY.w7OVxHxYR.nOGIyW', 'duc.le@school.edu.vn', true, '2023-01-15', '2024-01-15'),
('Hoàng Thị Mai', 'WOMEN', 'Huế', '1987-05-20', 'maihoang', '$2a$10$VA8kgIIzdChsNzKerGB7/.u.V3EpBsq50fKY.w7OVxHxYR.nOGIyW', 'mai.hoang@school.edu.vn', true, '2023-01-15', '2024-01-15');

-- Insert Students
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

-- Insert Classes (corrected column names)
INSERT INTO classes (name, class_name, grade_level, academic_year, semester, subject, is_active, created_at, updated_at) VALUES
('8A1', '8A1', 8, 2024, 1, 'Tin học', true, '2024-08-15', '2024-08-15'),
('8A2', '8A2', 8, 2024, 1, 'Tin học', true, '2024-08-15', '2024-08-15'),
('8B1', '8B1', 8, 2024, 1, 'Toán học', true, '2024-08-15', '2024-08-15'),
('9A1', '9A1', 9, 2024, 1, 'Tin học', true, '2024-08-15', '2024-08-15'),
('9A2', '9A2', 9, 2024, 1, 'Văn học', true, '2024-08-15', '2024-08-15'),
('7A1', '7A1', 7, 2024, 1, 'Tin học', true, '2024-08-15', '2024-08-15'),
('7B1', '7B1', 7, 2024, 1, 'Tiếng Anh', true, '2024-08-15', '2024-08-15'),
('8C1', '8C1', 8, 2024, 1, 'Vật lý', true, '2024-08-15', '2024-08-15');

-- Insert Teacher-Class Assignments (corrected column names)
-- Note: Using teacher_id instead of full teacher object, and using correct class reference
INSERT INTO teacher_classes (teacher_id, class_id, subject, academic_year, semester, assignment_role, is_primary_teacher, assigned_by, is_active, assigned_at, updated_at) VALUES
(1, 1, 'Tin học', 2024, '1', 'PRIMARY_TEACHER', true, 'Admin', true, '2024-08-20', '2024-08-20'),
(1, 2, 'Tin học', 2024, '1', 'SUBJECT_TEACHER', false, 'Admin', true, '2024-08-20', '2024-08-20'),
(1, 4, 'Tin học', 2024, '1', 'SUBJECT_TEACHER', false, 'Admin', true, '2024-08-20', '2024-08-20'),
(1, 6, 'Tin học', 2024, '1', 'SUBJECT_TEACHER', false, 'Admin', true, '2024-08-20', '2024-08-20'),
(2, 3, 'Toán học', 2024, '1', 'PRIMARY_TEACHER', true, 'Admin', true, '2024-08-20', '2024-08-20'),
(2, 8, 'Vật lý', 2024, '1', 'SUBJECT_TEACHER', false, 'Admin', true, '2024-08-20', '2024-08-20'),
(3, 5, 'Văn học', 2024, '1', 'PRIMARY_TEACHER', true, 'Admin', true, '2024-08-20', '2024-08-20'),
(4, 7, 'Tiếng Anh', 2024, '1', 'PRIMARY_TEACHER', true, 'Admin', true, '2024-08-20', '2024-08-20'),
(5, 1, 'Chủ nhiệm', 2024, 'BOTH', 'PRIMARY_TEACHER', true, 'Admin', true, '2024-08-20', '2024-08-20');

-- Insert Student-Class Assignments (using student_id and class_id based on your entity structure)
-- Note: Using the correct foreign key column name for class reference
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

-- Insert Scores
INSERT INTO scores (student_id, teacher_id, class_id, class_name, semester, year, ddgtx, ddggk, ddgck, tbm, comment, student_name, teacher_name, created_at, updated_at) VALUES
-- Scores for Class 8A1 - Computer Science - Teacher Nguyễn Thị Thủy (ID: 1)
(1, 1, 1, '8A1', 1, 2024, '8,9,7,8', 8, 9, 8, 'Học sinh có tiến bộ rõ rệt', 'Nguyễn Văn An', 'Nguyễn Thị Thủy', '2024-09-15', '2024-09-15'),
(2, 1, 1, '8A1', 1, 2024, '9,8,9,10', 9, 9, 9, 'Rất tốt, tiếp tục phát huy', 'Trần Thị Bình', 'Nguyễn Thị Thủy', '2024-09-15', '2024-09-15'),
(3, 1, 1, '8A1', 1, 2024, '7,6,7,8', 7, 8, 7, 'Cần cố gắng hơn', 'Lê Văn Cường', 'Nguyễn Thị Thủy', '2024-09-15', '2024-09-15'),
(4, 1, 1, '8A1', 1, 2024, '8,8,9,8', 8, 8, 8, 'Kết quả ổn định', 'Phạm Thị Dung', 'Nguyễn Thị Thủy', '2024-09-15', '2024-09-15'),
(5, 1, 1, '8A1', 1, 2024, '6,7,6,7', 6, 7, 7, 'Cần bổ sung kiến thức cơ bản', 'Hoàng Văn Em', 'Nguyễn Thị Thủy', '2024-09-15', '2024-09-15'),

-- Scores for Class 8A2 - Computer Science - Teacher Nguyễn Thị Thủy (ID: 1)
(6, 1, 2, '8A2', 1, 2024, '9,9,8,10', 9, 10, 9, 'Xuất sắc', 'Vũ Thị Giang', 'Nguyễn Thị Thủy', '2024-09-15', '2024-09-15'),
(7, 1, 2, '8A2', 1, 2024, '8,7,8,9', 8, 8, 8, 'Tốt', 'Đỗ Văn Hùng', 'Nguyễn Thị Thủy', '2024-09-15', '2024-09-15'),
(8, 1, 2, '8A2', 1, 2024, '7,8,7,8', 7, 8, 8, 'Khá', 'Bùi Thị Linh', 'Nguyễn Thị Thủy', '2024-09-15', '2024-09-15'),
(9, 1, 2, '8A2', 1, 2024, '8,9,8,9', 8, 9, 9, 'Tốt', 'Ngô Văn Khang', 'Nguyễn Thị Thủy', '2024-09-15', '2024-09-15'),
(10, 1, 2, '8A2', 1, 2024, '9,10,9,9', 9, 10, 10, 'Rất tốt', 'Cao Thị Mai', 'Nguyễn Thị Thủy', '2024-09-15', '2024-09-15'),

-- Scores for Class 8B1 - Mathematics - Teacher Trần Văn Minh (ID: 2)
(11, 2, 3, '8B1', 1, 2024, '8,9,8,9', 8, 9, 8, 'Giỏi toán', 'Đinh Văn Nam', 'Trần Văn Minh', '2024-09-15', '2024-09-15'),
(12, 2, 3, '8B1', 1, 2024, '7,7,8,7', 7, 8, 7, 'Khá', 'Tạ Thị Oanh', 'Trần Văn Minh', '2024-09-15', '2024-09-15'),
(13, 2, 3, '8B1', 1, 2024, '9,8,9,10', 9, 9, 9, 'Rất tốt', 'Lý Văn Phong', 'Trần Văn Minh', '2024-09-15', '2024-09-15'),
(14, 2, 3, '8B1', 1, 2024, '6,7,6,7', 6, 7, 7, 'Trung bình', 'Dương Thị Quỳnh', 'Trần Văn Minh', '2024-09-15', '2024-09-15'),

-- Scores for Class 9A1 - Computer Science - Teacher Nguyễn Thị Thủy (ID: 1)
(15, 1, 4, '9A1', 1, 2024, '10,9,10,9', 10, 10, 10, 'Xuất sắc', 'Võ Văn Sơn', 'Nguyễn Thị Thủy', '2024-09-15', '2024-09-15'),
(16, 1, 4, '9A1', 1, 2024, '8,9,8,8', 8, 9, 8, 'Tốt', 'Phan Thị Trang', 'Nguyễn Thị Thủy', '2024-09-15', '2024-09-15'),
(17, 1, 4, '9A1', 1, 2024, '9,9,9,8', 9, 9, 9, 'Rất tốt', 'Mai Văn Tuấn', 'Nguyễn Thị Thủy', '2024-09-15', '2024-09-15'),

-- Scores for Class 7A1 - Computer Science - Teacher Nguyễn Thị Thủy (ID: 1)
(18, 1, 6, '7A1', 1, 2024, '7,8,7,8', 7, 8, 8, 'Khá', 'Chu Thị Uyên', 'Nguyễn Thị Thủy', '2024-09-15', '2024-09-15'),
(19, 1, 6, '7A1', 1, 2024, '8,8,9,8', 8, 8, 8, 'Tốt', 'Hà Văn Việt', 'Nguyễn Thị Thủy', '2024-09-15', '2024-09-15'),
(20, 1, 6, '7A1', 1, 2024, '9,8,9,9', 9, 9, 9, 'Rất tốt', 'Lài Thị Xuân', 'Nguyễn Thị Thủy', '2024-09-15', '2024-09-15');

-- Insert Semester Schedules
INSERT INTO semester_schedules (schedule_name, semester, year, class_name, start_date_time, end_date_time, is_active, is_locked, description, created_by, created_at, updated_at) VALUES
('Học kỳ 1 - Nhập điểm', 1, 2024, '8A1', '2024-09-01 00:00:00', '2024-12-31 23:59:59', true, false, 'Thời gian nhập điểm học kỳ 1 cho lớp 8A1', 'Admin', '2024-08-25', '2024-08-25'),
('Học kỳ 1 - Nhập điểm', 1, 2024, '8A2', '2024-09-01 00:00:00', '2024-12-31 23:59:59', true, false, 'Thời gian nhập điểm học kỳ 1 cho lớp 8A2', 'Admin', '2024-08-25', '2024-08-25'),
('Học kỳ 1 - Nhập điểm', 1, 2024, '8B1', '2024-09-01 00:00:00', '2024-12-31 23:59:59', true, false, 'Thời gian nhập điểm học kỳ 1 cho lớp 8B1', 'Admin', '2024-08-25', '2024-08-25'),
('Học kỳ 1 - Nhập điểm', 1, 2024, '9A1', '2024-09-01 00:00:00', '2024-12-31 23:59:59', true, false, 'Thời gian nhập điểm học kỳ 1 cho lớp 9A1', 'Admin', '2024-08-25', '2024-08-25'),
('Học kỳ 1 - Nhập điểm', 1, 2024, '7A1', '2024-09-01 00:00:00', '2024-12-31 23:59:59', true, false, 'Thời gian nhập điểm học kỳ 1 cho lớp 7A1', 'Admin', '2024-08-25', '2024-08-25'),
('Học kỳ 2 - Nhập điểm', 2, 2025, '8A1', '2025-01-15 00:00:00', '2025-05-31 23:59:59', false, false, 'Thời gian nhập điểm học kỳ 2 cho lớp 8A1', 'Admin', '2024-12-01', '2024-12-01'),
('Học kỳ 2 - Nhập điểm', 2, 2025, '8A2', '2025-01-15 00:00:00', '2025-05-31 23:59:59', false, false, 'Thời gian nhập điểm học kỳ 2 cho lớp 8A2', 'Admin', '2024-12-01', '2024-12-01');

-- Verification queries
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