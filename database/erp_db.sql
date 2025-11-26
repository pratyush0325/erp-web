CREATE DATABASE IF NOT EXISTS erp_db;

USE erp_db;

DROP TABLE IF EXISTS grades;
DROP TABLE IF EXISTS enrollments;
DROP TABLE IF EXISTS sections;
DROP TABLE IF EXISTS courses;
DROP TABLE IF EXISTS instructors;
DROP TABLE IF EXISTS students;
DROP TABLE IF EXISTS settings;

CREATE TABLE IF NOT EXISTS students (
    user_id INT PRIMARY KEY,
    roll_no VARCHAR(50) NOT NULL UNIQUE,
    program VARCHAR(50) NOT NULL,
    year INT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES auth_db.users_auth(user_id)
);

CREATE TABLE IF NOT EXISTS instructors (
    user_id INT PRIMARY KEY,
    department VARCHAR(50) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES auth_db.   users_auth(user_id)
);

CREATE TABLE IF NOT EXISTS courses (
    code VARCHAR(50) PRIMARY KEY,
    title VARCHAR(50) NOT NULL,
    credits INT NOT NULL
);

CREATE TABLE IF NOT EXISTS sections (
    section_id INT PRIMARY KEY AUTO_INCREMENT,
    course_id VARCHAR(50) NOT NULL,
    instructor_id INT NOT NULL,
    day_time VARCHAR(50),
    room VARCHAR(50),
    capacity INT,
    semester VARCHAR(50),
    year INT,
    FOREIGN KEY (course_id) REFERENCES courses(code),
    FOREIGN KEY (instructor_id) REFERENCES instructors(user_id)
);

CREATE TABLE IF NOT EXISTS enrollments (
    enrollment_id INT PRIMARY KEY AUTO_INCREMENT,
    student_id INT NOT NULL,
    section_id INT NOT NULL,
    status VARCHAR(50) NOT NULL,
    FOREIGN KEY (student_id) REFERENCES students(user_id),
    FOREIGN KEY (section_id) REFERENCES sections(section_id)
);

CREATE TABLE IF NOT EXISTS grades (
    grade_id INT PRIMARY KEY AUTO_INCREMENT,
    enrollment_id INT NOT NULL,
    component VARCHAR(50) NOT NULL,
    score DOUBLE NOT NULL,
    final_grade DOUBLE DEFAULT 0,
    FOREIGN KEY (enrollment_id) REFERENCES enrollments(enrollment_id)
);

CREATE TABLE IF NOT EXISTS settings (
    settings_key VARCHAR(50) PRIMARY KEY,
    settings_value VARCHAR(50)
);

# populate the database
INSERT INTO students (user_id, roll_no, program, year)
VALUES
    (3, '2023001', 'Computer Science', 2025),
    (4, '2023002', 'Electrical Engineering', 2025);

INSERT INTO instructors (user_id, department)
VALUES
    (2, 'Computer Science');

INSERT INTO courses (code, title, credits)
VALUES
    ('CS101', 'Intro to Programming', 3),
    ('EE201', 'Circuit Theory', 4);

INSERT INTO sections (course_id, instructor_id, day_time, room, capacity, semester, year)
VALUES
    ('CS101', 2, 'Mon/Wed 10:00-11:30', 'A101', 50, 'Fall', 2025),
    ('EE201', 2, 'Tue/Thu 13:00-14:30', 'B202', 30, 'Fall', 2025);

INSERT INTO enrollments (student_id, section_id, status)
VALUES
    (3, 1, 'registered'),
    (4, 2, 'registered');

INSERT INTO grades (enrollment_id, component, score)
VALUES
    (1, 'Quiz 1', 95),
    (1, 'Midterm', 88),
    (1, 'Final Exam', 92);

INSERT INTO settings (settings_key, settings_value)
VALUES
    (   'maintenance_on', 'false');