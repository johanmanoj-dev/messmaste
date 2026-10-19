-- ============================================================
-- Messmate Database Schema & Seed Data (MySQL)
-- Course: Object Oriented Programming (24SJPBCST304)
-- ============================================================

CREATE DATABASE IF NOT EXISTS messmate_db;
USE messmate_db;

-- 1. Users Table (Stores Students and Admins)
-- Demonstrates Module 1 & 2 concepts: Encapsulation & Role Hierarchy
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,       -- Room Number for students (e.g., '101'), or 'admin'
    password VARCHAR(50) NOT NULL,              -- Simple password
    full_name VARCHAR(100) NOT NULL,            -- Student / Admin Name
    role VARCHAR(20) NOT NULL                   -- 'STUDENT' or 'ADMIN'
);

-- 2. Bookings Table (Stores Daily Meal Selections & Attendance)
-- Demonstrates Module 4: Relational Database design & JDBC Operations
CREATE TABLE IF NOT EXISTS bookings (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    meal_date DATE NOT NULL,                    -- The date the meal is booked for
    booked_at DATE NOT NULL,                    -- The date when the booking was made
    breakfast BOOLEAN DEFAULT FALSE,
    lunch BOOLEAN DEFAULT FALSE,
    snacks BOOLEAN DEFAULT FALSE,
    dinner BOOLEAN DEFAULT FALSE,
    breakfast_attended BOOLEAN DEFAULT FALSE,   -- Attendance marked by Admin
    lunch_attended BOOLEAN DEFAULT FALSE,
    snacks_attended BOOLEAN DEFAULT FALSE,
    dinner_attended BOOLEAN DEFAULT FALSE,
    total_cost DOUBLE NOT NULL,                 -- Computed based on fixed meal prices
    FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY uk_student_date (student_id, meal_date)
);

-- Seed Data: Predefined Users
-- Students with room numbers as username:
INSERT IGNORE INTO users (id, username, password, full_name, role) VALUES
(1, '101', 'pass101', 'Rahul Sharma', 'STUDENT'),
(2, '102', 'pass102', 'Sneha Patel', 'STUDENT'),
(3, '103', 'pass103', 'Amit Kumar', 'STUDENT'),
(4, '104', 'pass104', 'Ananya Nair', 'STUDENT'),
(5, '105', 'pass105', 'Rohan Das', 'STUDENT'),
(6, 'admin', 'admin123', 'Mess Administrator', 'ADMIN'),
(7, '999', 'admin123', 'Mess Warden', 'ADMIN');

