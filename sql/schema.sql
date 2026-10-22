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
    username VARCHAR(50) NOT NULL UNIQUE,       -- ID / Admission Number for students (e.g., '25CS008'), or 'admin'
    password VARCHAR(50) NOT NULL,              -- Simple password (same as ID number for students)
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

-- Clean up any legacy room-number test accounts if present
DELETE FROM users WHERE username IN ('101', '102', '103', '104', '105', '106', '107', '108');

-- Seed Data: Administrators
INSERT IGNORE INTO users (username, password, full_name, role) VALUES
('admin', 'admin123', 'Mess Administrator', 'ADMIN'),
('999', 'admin123', 'Mess Warden', 'ADMIN');

-- Seed Data: Students (ID Number = Admission Number, Password = ID Number, Full Name = Name)
INSERT IGNORE INTO users (username, password, full_name, role) VALUES
('25CS008', '25CS008', 'ABHINAV SHINOJ', 'STUDENT'),
('25CS012', '25CS012', 'ABHISHEK P NAIR', 'STUDENT'),
('25CS013', '25CS013', 'ABIYA PRAKASH A B', 'STUDENT'),
('25CS017', '25CS017', 'ADWAITH ANIL', 'STUDENT'),
('25CS020', '25CS020', 'ADWAITH D NAIR', 'STUDENT'),
('25CS021', '25CS021', 'AFIA S', 'STUDENT'),
('25CS031', '25CS031', 'ALAN BAIJU', 'STUDENT'),
('25CS033', '25CS033', 'ALBIN ANTONY', 'STUDENT'),
('25CS034', '25CS034', 'ALBIN BINU', 'STUDENT'),
('25CS040', '25CS040', 'ALONA CINTO', 'STUDENT'),
('25CS044', '25CS044', 'AMITHA P A', 'STUDENT'),
('25CS050', '25CS050', 'ANNS MARIA GINS', 'STUDENT'),
('25CS053', '25CS053', 'ARCHANA SUDEEP', 'STUDENT'),
('25CS055', '25CS055', 'ASHIN JOSEPH SOJI', 'STUDENT'),
('25CS058', '25CS058', 'ASWIN BIJU', 'STUDENT'),
('25CS062', '25CS062', 'ATHUL R NAIR', 'STUDENT'),
('25CS063', '25CS063', 'AUGUSTINE JOMON', 'STUDENT'),
('25CS066', '25CS066', 'CHRIS JOSEPH SHINE', 'STUDENT'),
('25CS069', '25CS069', 'CYRIAC JOSE', 'STUDENT'),
('25CS077', '25CS077', 'DIYA GOPAKUMAR', 'STUDENT'),
('25CS080', '25CS080', 'DONA VINCENT', 'STUDENT'),
('25CS082', '25CS082', 'EFINOVA SABU', 'STUDENT'),
('25CS083', '25CS083', 'ELAINE ROSE SUNIL', 'STUDENT'),
('25CS085', '25CS085', 'ELSA ROSE JIMMY', 'STUDENT'),
('25CS087', '25CS087', 'EMMANUEL THOMAS ARUN', 'STUDENT'),
('25CS088', '25CS088', 'EVANGELY MARIAM SHANTIS', 'STUDENT'),
('25CS093', '25CS093', 'GANGA P G', 'STUDENT'),
('25CS094', '25CS094', 'GAUTHAM DAS', 'STUDENT'),
('25CS095', '25CS095', 'GEO MATHEW SAJU', 'STUDENT'),
('25CS096', '25CS096', 'GEORGE THOMAS', 'STUDENT'),
('25CS099', '25CS099', 'GOUTHAM KRISHNA V V', 'STUDENT'),
('25CS101', '25CS101', 'HARIJAYANTH S', 'STUDENT'),
('25CS102', '25CS102', 'HARITHA ROY', 'STUDENT'),
('25CS104', '25CS104', 'IRIN GEORGE', 'STUDENT'),
('25CS106', '25CS106', 'JAIDIN MEJE', 'STUDENT'),
('25CS108', '25CS108', 'JEES REJI', 'STUDENT'),
('25CS110', '25CS110', 'JEREMY JOJI', 'STUDENT'),
('25CS111', '25CS111', 'JEROME ABRAHAM PIOUS', 'STUDENT'),
('25CS112', '25CS112', 'JESTO JOHNSON', 'STUDENT'),
('25CS113', '25CS113', 'JESVIN JOIES', 'STUDENT'),
('25CS114', '25CS114', 'JEWEL MARIYA VARGHESE', 'STUDENT'),
('25CS117', '25CS117', 'JOEL JOSE', 'STUDENT'),
('25CS118', '25CS118', 'JOEL JOSEPH', 'STUDENT'),
('25CS121', '25CS121', 'JOHAN P MANOJ', 'STUDENT'),
('25CS123', '25CS123', 'JOICE BENNY', 'STUDENT'),
('25CS124', '25CS124', 'JOSEPH MICHAEL', 'STUDENT'),
('25CS126', '25CS126', 'JOSU JAISON', 'STUDENT'),
('25CS130', '25CS130', 'KARTHIK S GOPAL', 'STUDENT'),
('25CS133', '25CS133', 'KRISHNAJITH A', 'STUDENT'),
('25CS136', '25CS136', 'LEKSHMI SREEKUMAR', 'STUDENT'),
('25CS138', '25CS138', 'LIYA TOMY', 'STUDENT'),
('25CS140', '25CS140', 'MADHAV SURESH', 'STUDENT'),
('25CS146', '25CS146', 'MICHAEL JAMES', 'STUDENT'),
('25CS147', '25CS147', 'MILANA MARIA MATHEW', 'STUDENT'),
('25CS149', '25CS149', 'MINNA AUGUSTIN', 'STUDENT'),
('25CS150', '25CS150', 'MIRON VINCENT', 'STUDENT'),
('25CS152', '25CS152', 'MRUDUL R NAIR', 'STUDENT'),
('25CS158', '25CS158', 'NIRANJAN MANOJ', 'STUDENT'),
('25CS161', '25CS161', 'NISSIMOL SABU', 'STUDENT'),
('25CS171', '25CS171', 'PRIYADARSHINI M R', 'STUDENT'),
('25CS175', '25CS175', 'RHEA THOMAS', 'STUDENT'),
('25CS178', '25CS178', 'RONY SIBY', 'STUDENT'),
('25CS183', '25CS183', 'SALMAN S', 'STUDENT'),
('25CS192', '25CS192', 'SIVAPRIYA M', 'STUDENT'),
('25CS196', '25CS196', 'SREEHARI S', 'STUDENT'),
('25CS198', '25CS198', 'THEJUS TOM PIOUS', 'STUDENT'),
('25CS200', '25CS200', 'TISSA ROSE MATHEW', 'STUDENT'),
('25CS204', '25CS204', 'VYGA SAJIKUMAR', 'STUDENT');
