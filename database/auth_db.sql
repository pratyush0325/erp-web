CREATE DATABASE IF NOT EXISTS auth_db;

USE auth_db;

# DROP TABLE IF EXISTS users_auth;

CREATE TABLE IF NOT EXISTS users_auth (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    role VARCHAR(20) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    status VARCHAR(20),
    last_login TIMESTAMP
);

INSERT INTO users_auth (username, role, password_hash)
VALUES
#     ('admin2', 'admin', '$2a$10$qvIq6XlFUtw9U6SKLYieaexpyTFlOlWnTrZO8h6B63SfCrmyU1.RS'),
#     ('inst1', 'instructor', '$2a$10$VOSzqgGaMM2/w9fSwbxYFeKQFvU6D6aITGWQ4DZfBBgPqa4swrScC'),
#     ('stu1', 'student', '$2a$10$tjxjCcH6b4w04gPuSzKavuOZp0N/bkRUhFuPftlc06bfGjUmM8CMu'),
#     ('stu2', 'student', '$2a$10$qMQPQRO9ddJyjGJ.XInN5.DWD62.hWWP6BZ8vIKbfgjJtJrEO0Tva'),
#     ('stu3', 'student', '$2a$10$qMQPQRO9ddJyjGJ.XInN5.DWD62.hWWP6BZ8vIKbfgjJtJrEO0Tva'),
    ('stu4', 'student', '$2a$10$qMQPQRO9ddJyjGJ.XInN5.DWD62.hWWP6BZ8vIKbfgjJtJrEO0Tva');

