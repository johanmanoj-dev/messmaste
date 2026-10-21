# Messmate — Campus Mess Meal Booking System

**Course**: Object Oriented Programming (24SJPBCST304)  
**Curriculum**: B.Tech Computer Science and Engineering — Semester 3  
**Institution**: St. Joseph's College of Engineering and Technology, Palai  
**Technology Stack**: Java SE (Standard Library, Swing GUI), MySQL (JDBC Type-4 Connector)

---

## 1. Quick Start Guide

### Step 1: Start MySQL Database
Ensure MySQL Server is running (e.g., via **XAMPP Control Panel** by clicking *Start* next to MySQL, or via Windows MySQL Service).
- Default Host: `localhost`
- Default Port: `3306`
- Default User: `root`
- Default Password: `""` (Empty password)

> **Note**: If your MySQL password is not empty, you can either:
> 1. Edit `db.properties` in this directory, or
> 2. Click the **"MySQL Settings"** button on the bottom of the Messmate Login Screen to set your password directly from the GUI!

### Step 2: Compile the Project (Command Prompt)
Open Command Prompt (`cmd`) and navigate to the project directory:
```cmd
cd /d "e:\java prjct\messmate"
```
Compile the project into the `bin` directory using `javac`:
```cmd
javac -cp "lib/*;src" -d bin src/messmate/Main.java
```
*(Because `Main.java` imports the other packages and classes, `javac` automatically compiles all referenced classes across the entire project into the `bin/` directory!)*

### Step 3: Run the Application (Command Prompt)
From the same command prompt, run:
```cmd
java -cp "bin;lib/*" messmate.Main
```

---

## 2. Predefined Logins (Seed Accounts)

| User Role | Username (Room No / Admin ID) | Password | Full Name |
| :--- | :--- | :--- | :--- |
| **Student** | `101` | `pass101` | Rahul Sharma |
| **Student** | `102` | `pass102` | Sneha Patel |
| **Student** | `103` | `pass103` | Amit Kumar |
| **Student** | `104` | `pass104` | Ananya Nair |
| **Student** | `105` | `pass105` | Rohan Das |
| **Administrator** | `admin` | `admin123` | Mess Administrator |
| **Administrator** | `999` | `admin123` | Mess Warden |

---

## 3. Core Features & User Workflows

### 3.1 Student Route
1. **Login**: Enter room number (e.g., `101`) and password (`pass101`).
2. **Dashboard**: Access 4 options:
   - **Book a Meal**:
     - Enter meal date in `YYYY-MM-DD` format.
     - **Validation Check 1**: Must be at least **2 days later than today** (enforced by Java business logic, throws `InvalidBookingDateException`).
     - **Validation Check 2**: Checks MySQL to prevent duplicate bookings for the same date (throws `DuplicateBookingException`).
     - Once verified, select meals using checkboxes:
       - Breakfast
       - Lunch
       - Evening Snacks
       - Dinner
     - (Note: Prices are tracked and viewed exclusively in the Monthly Mess Bill screen)
     - Click **Confirm Booking** $\rightarrow$ Confirmation dialog popup asks *"Do you want to confirm this booking for [date]?"*
     - If confirmed: saves booking to database, updates bill, and redirects to dashboard. If cancelled, stays on screen to edit.
   - **View Bookings**:
     - Displays all bookings in chronological order as cards with status (`Active` or `Completed`).
     - Click **View Details** on any card to see full breakdown and attendance status.
   - **Monthly Mess Bill**:
     - Real-time counter showing current month's accumulated bill on top.
     - Lower section displays past months' bill summary cards.
   - **Logout**: Returns to login screen.

### 3.2 Admin Route
1. **Login**: Enter admin ID (`admin`) and password (`admin123`).
2. **Dashboard**: Access 3 options:
   - **Daily Bookings Summary**:
     - Select a date to view all registered students and their booked meals in a `JTable`.
     - Displays daily kitchen totals at the bottom: Total Breakfast, Total Lunch, Total Snacks, and Total Dinner.
   - **Meal Entry & Attendance**:
     - Select date and choose meal session from dropdown (Breakfast / Lunch / Evening Snacks / Dinner).
     - Displays table of registered students with interactive attendance checkboxes.
     - Check students as they enter the mess hall and click **Save Attendance to Database**.
   - **Logout**: Returns to login screen.

---

## 4. Syllabus Mapping Matrix (Viva Guide for Teammates)

When faculty or external examiners ask how concepts from the course are implemented:

| Syllabus Module | Concept Taught | How it is implemented in Messmate | File / Class |
| :--- | :--- | :--- | :--- |
| **Module 1** | **Primitive Data Types & Operators** | `boolean` for meal choices, `double` for prices, `int` for IDs. Arithmetic (`+`, `*`) for bill calculation, relational (`>= 2`) for date logic. | `MealPricing.java`, `BookMealFrame.java` |
| **Module 1** | **Control Statements** | Selection (`if-else` for validation), Iteration (`while (rs.next())` loop reading SQL records, `for` loop creating UI cards). | `BookingService.java`, `ViewBookingsFrame.java` |
| **Module 1** | **Vector Class (Basics)** | `Vector<String>` for column headers and `Vector<Vector<Object>>` for `DefaultTableModel` rows in Swing `JTable`. | `AdminBookingsFrame.java`, `AdminEntryFrame.java` |
| **Module 1** | **Classes, Objects & Encapsulation** | Private fields, getters and setters, hiding internal representation. | `User.java`, `MealBooking.java` |
| **Module 1** | **Constructors & `this` Keyword** | Parameterized constructors resolving field shadowing (`this.id = id`). | `User.java`, `MealBooking.java` |
| **Module 2** | **Inheritance & `super` Keyword** | Superclass `User` inherited by subclasses `Student` and `Admin`. Chaining constructors with `super(...)`. | `User.java`, `Student.java`, `Admin.java` |
| **Module 2** | **Base GUI Inheritance** | Common window appearance, fonts, colors, and alert dialogs defined in `BaseFrame extends JFrame`. | `BaseFrame.java` |
| **Module 2** | **Method Overriding & Dynamic Dispatch** | Overriding `displayInfo()` in `User`, `Student`, and `Admin`. Calling `user.displayInfo()` resolves at runtime. | `User.java`, `Student.java`, `Admin.java` |
| **Module 2** | **Method Overloading** | Overloaded bill calculation: `calculateBill(studentId)` vs `calculateBill(studentId, month, year)`. | `BookingService.java` |
| **Module 2** | **Static Members & Final Variables** | Singleton database connection (`DBConnection.getConnection()`) and fixed meal prices (`public static final double`). | `DBConnection.java`, `MealPricing.java` |
| **Module 2** | **Inner Classes** | Event handler inner classes implementing `ActionListener` and `ItemListener`. | `BookMealFrame.java`, `LoginFrame.java` |
| **Module 3** | **Packages & Access Protection** | Modular packages: `messmate.model`, `messmate.db`, `messmate.service`, `messmate.ui`, `messmate.exception`. `protected` members in base classes. | Whole project |
| **Module 3** | **Interfaces** | Interface `IBookingService` defining contracts, implemented by `BookingService`. Accessing methods through interface reference. | `IBookingService.java`, `BookingService.java` |
| **Module 3** | **Exception Handling** | Checked exceptions (`SQLException`), custom exceptions (`InvalidBookingDateException`, `DuplicateBookingException`), `try-catch-finally` cleanup. | `messmate.exception.*`, `DBConnection.java` |
| **Module 4** | **Swing Controls & Containers** | `JFrame`, `JPanel`, `JLabel`, `JButton`, `JTextField`, `JPasswordField`, `JCheckBox`, `JComboBox`, `JTable`, `JScrollPane`, `JOptionPane`. | `messmate.ui.*` |
| **Module 4** | **Swing Layout Managers** | `BorderLayout`, `FlowLayout`, `GridLayout`, and `BoxLayout` for clean component placement. | All UI frames |
| **Module 4** | **Delegation Event Model** | Event Sources (`JButton`), Event Objects (`ActionEvent`), Event Listener Interfaces (`ActionListener`), registered using `addActionListener()`. | All UI frames |
| **Module 4** | **JDBC & MySQL CRUD Operations** | `DriverManager`, `Connection`, `PreparedStatement` with `?` parameters (prevents SQL injection), `ResultSet` iteration, CRUD queries (Insert bookings, Select records, Update attendance). | `DBConnection.java`, `BookingService.java`, `UserService.java` |

---

## 5. Frequently Asked Questions in Viva

1. **Why use `PreparedStatement` instead of regular `Statement`?**  
   *Answer*: `PreparedStatement` pre-compiles SQL queries and uses parameterized placeholders (`?`), which provides faster execution and protects against SQL Injection attacks. Regular `Statement` is only used for static DDL commands like `CREATE TABLE`.

2. **How does constructor chaining work between `Student` and `User`?**  
   *Answer*: In `Student.java`, the constructor executes `super(id, username, password, fullName, "STUDENT")`. The JVM calls the `User` superclass constructor first to initialize inherited fields before initializing any student-specific properties.

3. **How does the date validation enforce the 2-day rule?**  
   *Answer*: In `BookingService.java` and `BookMealFrame.java`, `ChronoUnit.DAYS.between(today, targetDate)` computes the difference in days. If the value is less than 2, the application throws a custom checked exception: `throw new InvalidBookingDateException(...)`, caught by the UI to show a dialog.

4. **Where is dynamic method dispatch demonstrated?**  
   *Answer*: In `UserService.java` and `LoginFrame.java`, the method returns a `User` reference. However, the actual object instantiated is either `Student` or `Admin`. When `user.displayInfo()` is called, Java dynamically invokes the overridden method of the actual subclass at runtime.
