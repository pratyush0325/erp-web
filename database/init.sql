-- ============================================================
-- ERP Demo Database — run once on a fresh MySQL instance
-- All demo accounts use password: demo123
-- ============================================================

-- ── auth_db ─────────────────────────────────────────────────

CREATE DATABASE IF NOT EXISTS auth_db;
USE auth_db;

DROP TABLE IF EXISTS users_auth;

CREATE TABLE users_auth (
    user_id        INT          PRIMARY KEY AUTO_INCREMENT,
    username       VARCHAR(50)  NOT NULL UNIQUE,
    role           VARCHAR(20)  NOT NULL,
    password_hash  VARCHAR(255) NOT NULL,
    status         VARCHAR(20)  DEFAULT 'active',
    failed_attempts INT         NOT NULL DEFAULT 0,
    lockout_until  TIMESTAMP    NULL,
    last_login     TIMESTAMP    NULL
);

-- password for all accounts: demo123
INSERT INTO users_auth (username, role, password_hash) VALUES
    ('admin',       'admin',      '$2a$10$0mctLTEEPY0rz.ZJ3AFib.UCBrXpmsJ.Oq5MchOVPefAD8w33doCu'),
    ('prof_smith',  'instructor', '$2a$10$clxtFUCJ4VPcG8y5t33PausKOYWzu4nbLqU7a9zmT7dqQX9YFlLkq'),
    ('prof_jones',  'instructor', '$2a$10$clxtFUCJ4VPcG8y5t33PausKOYWzu4nbLqU7a9zmT7dqQX9YFlLkq'),
    ('alice',       'student',    '$2a$10$wYK.D1XnO1zotjccLgr/.eFAmBmAdZJPMpqZ1ZHFZ862kZnpGm2FW'),
    ('bob',         'student',    '$2a$10$wYK.D1XnO1zotjccLgr/.eFAmBmAdZJPMpqZ1ZHFZ862kZnpGm2FW'),
    ('carol',       'student',    '$2a$10$wYK.D1XnO1zotjccLgr/.eFAmBmAdZJPMpqZ1ZHFZ862kZnpGm2FW'),
    ('dave',        'student',    '$2a$10$wYK.D1XnO1zotjccLgr/.eFAmBmAdZJPMpqZ1ZHFZ862kZnpGm2FW'),
    ('emma',        'student',    '$2a$10$wYK.D1XnO1zotjccLgr/.eFAmBmAdZJPMpqZ1ZHFZ862kZnpGm2FW');

-- ── erp_db ──────────────────────────────────────────────────

CREATE DATABASE IF NOT EXISTS erp_db;
USE erp_db;

DROP TABLE IF EXISTS grades;
DROP TABLE IF EXISTS enrollments;
DROP TABLE IF EXISTS sections;
DROP TABLE IF EXISTS courses;
DROP TABLE IF EXISTS instructors;
DROP TABLE IF EXISTS students;
DROP TABLE IF EXISTS settings;

CREATE TABLE students (
    user_id INT         PRIMARY KEY,
    roll_no VARCHAR(50) NOT NULL UNIQUE,
    program VARCHAR(50) NOT NULL,
    year    INT         NOT NULL,
    FOREIGN KEY (user_id) REFERENCES auth_db.users_auth(user_id)
);

CREATE TABLE instructors (
    user_id    INT         PRIMARY KEY,
    department VARCHAR(50) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES auth_db.users_auth(user_id)
);

CREATE TABLE courses (
    code    VARCHAR(50) PRIMARY KEY,
    title   VARCHAR(100) NOT NULL,
    credits INT          NOT NULL
);

CREATE TABLE sections (
    section_id    INT         PRIMARY KEY AUTO_INCREMENT,
    course_id     VARCHAR(50) NOT NULL,
    instructor_id INT         NOT NULL,
    day_time      VARCHAR(50),
    room          VARCHAR(50),
    capacity      INT,
    semester      VARCHAR(50),
    year          INT,
    FOREIGN KEY (course_id)     REFERENCES courses(code),
    FOREIGN KEY (instructor_id) REFERENCES instructors(user_id)
);

CREATE TABLE enrollments (
    enrollment_id INT         PRIMARY KEY AUTO_INCREMENT,
    student_id    INT         NOT NULL,
    section_id    INT         NOT NULL,
    status        VARCHAR(50) NOT NULL,
    FOREIGN KEY (student_id) REFERENCES students(user_id),
    FOREIGN KEY (section_id) REFERENCES sections(section_id)
);

CREATE TABLE grades (
    grade_id      INT         PRIMARY KEY AUTO_INCREMENT,
    enrollment_id INT         NOT NULL,
    component     VARCHAR(50) NOT NULL,
    score         DOUBLE      NOT NULL,
    final_grade   DOUBLE      DEFAULT 0,
    FOREIGN KEY (enrollment_id) REFERENCES enrollments(enrollment_id)
);

CREATE TABLE settings (
    settings_key   VARCHAR(50) PRIMARY KEY,
    settings_value VARCHAR(50)
);

-- ── seed data ───────────────────────────────────────────────

-- auth user_ids: admin=1, prof_smith=2, prof_jones=3, alice=4, bob=5, carol=6, dave=7, emma=8

INSERT INTO instructors (user_id, department) VALUES
    (2, 'Computer Science'),
    (3, 'Mathematics');

INSERT INTO students (user_id, roll_no, program, year) VALUES
    (4, '2023001', 'Computer Science',       3),
    (5, '2023002', 'Electrical Engineering', 2),
    (6, '2023003', 'Computer Science',       3),
    (7, '2022001', 'Mathematics',            4),
    (8, '2024001', 'Computer Science',       1);

INSERT INTO courses (code, title, credits) VALUES
    ('CS101',  'Introduction to Programming',  3),
    ('CS201',  'Data Structures & Algorithms', 4),
    ('MATH101','Calculus I',                   3),
    ('EE201',  'Circuit Theory',               4);

-- sections: Fall 2025
INSERT INTO sections (course_id, instructor_id, day_time, room, capacity, semester, year) VALUES
    ('CS101',  2, 'Mon/Wed 09:00-10:30', 'A101', 40, 'Fall', 2025),  -- section 1
    ('CS201',  2, 'Tue/Thu 11:00-12:30', 'A102', 35, 'Fall', 2025),  -- section 2
    ('MATH101',3, 'Mon/Wed 13:00-14:30', 'B201', 50, 'Fall', 2025),  -- section 3
    ('EE201',  3, 'Tue/Thu 15:00-16:30', 'B202', 30, 'Fall', 2025),  -- section 4
    ('CS101',  2, 'Fri 10:00-13:00',     'A103', 25, 'Fall', 2025),  -- section 5 (lab)
    ('CS201',  2, 'Mon/Wed 16:00-17:30', 'A104', 30, 'Fall', 2025);  -- section 6

-- enrollments
INSERT INTO enrollments (student_id, section_id, status) VALUES
    (4, 1, 'registered'),   -- alice  → CS101 lec
    (4, 2, 'registered'),   -- alice  → CS201
    (4, 3, 'registered'),   -- alice  → MATH101
    (5, 3, 'registered'),   -- bob    → MATH101
    (5, 4, 'registered'),   -- bob    → EE201
    (6, 1, 'registered'),   -- carol  → CS101 lec
    (6, 2, 'registered'),   -- carol  → CS201
    (7, 3, 'registered'),   -- dave   → MATH101
    (7, 4, 'registered'),   -- dave   → EE201
    (8, 1, 'registered'),   -- emma   → CS101 lec
    (8, 5, 'registered');   -- emma   → CS101 lab

-- grades (enrollment_ids follow insert order above: 1-11)
-- alice in CS101 (enroll 1)
INSERT INTO grades (enrollment_id, component, score) VALUES
    (1, 'Quiz 1',    88),
    (1, 'Midterm',   75),
    (1, 'Final Exam',82);

-- alice in CS201 (enroll 2)
INSERT INTO grades (enrollment_id, component, score) VALUES
    (2, 'Assignment 1', 92),
    (2, 'Assignment 2', 87),
    (2, 'Midterm',      79),
    (2, 'Final Exam',   91);

-- alice in MATH101 (enroll 3)
INSERT INTO grades (enrollment_id, component, score) VALUES
    (3, 'Quiz 1',    95),
    (3, 'Midterm',   90),
    (3, 'Final Exam',93);

-- bob in MATH101 (enroll 4)
INSERT INTO grades (enrollment_id, component, score) VALUES
    (4, 'Quiz 1',    70),
    (4, 'Midterm',   65),
    (4, 'Final Exam',72);

-- bob in EE201 (enroll 5)
INSERT INTO grades (enrollment_id, component, score) VALUES
    (5, 'Lab 1',     85),
    (5, 'Midterm',   78),
    (5, 'Final Exam',80);

-- carol in CS101 (enroll 6)
INSERT INTO grades (enrollment_id, component, score) VALUES
    (6, 'Quiz 1',    91),
    (6, 'Midterm',   88),
    (6, 'Final Exam',94);

-- carol in CS201 (enroll 7)
INSERT INTO grades (enrollment_id, component, score) VALUES
    (7, 'Assignment 1', 96),
    (7, 'Assignment 2', 93),
    (7, 'Midterm',      89),
    (7, 'Final Exam',   95);

-- dave in MATH101 (enroll 8) — no grades yet (pending)
-- dave in EE201 (enroll 9)
INSERT INTO grades (enrollment_id, component, score) VALUES
    (9, 'Lab 1', 60),
    (9, 'Lab 2', 55);

-- emma in CS101 lec (enroll 10)
INSERT INTO grades (enrollment_id, component, score) VALUES
    (10, 'Quiz 1', 77),
    (10, 'Midterm', 71);

-- settings
INSERT INTO settings (settings_key, settings_value) VALUES
    ('maintenance_on',       'false'),
    ('registration_deadline', '2025-09-15');
