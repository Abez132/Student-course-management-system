# Student Management System

A desktop application built with Java Swing for managing students, courses, enrollments, and grades. It supports two roles — **Student** and **Administrator** — each with their own dashboard and feature set.

---

## Features

### Student
- Register a new account and log in with Student ID + name + password
- Browse all available courses with images and descriptions
- Enroll in or drop courses
- View personal grades and academic performance summary
- Access course materials (file viewer with file transfer support)

### Administrator
- Manage students — add, edit, delete
- Manage courses — add, edit, delete with image assignment
- Assign and update grades per student per course
- View all grades across all students
- Upload and transfer course material files to students

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 11 |
| UI | Java Swing |
| Database | MySQL 8 |
| Build Tool | Apache Maven |
| DB Connector | MySQL Connector/J 8.0.33 |
| File Transfer | Custom TCP server + Java-WebSocket 1.5.3 |
| JSON | Jackson Databind 2.15.2 |
| File Utils | Apache Commons IO 2.11.0 |
| Logging | SLF4J Simple 1.7.36 |

---

## Prerequisites

- Java JDK 17+
- Apache Maven 3.6+
- XAMPP (or any MySQL 8 server)

---

## Database Setup

1. Start MySQL from the XAMPP Control Panel.
2. Open a terminal and run the schema script:

```bash
"C:\xampp\mysql\bin\mysql.exe" -u root -p123456 < src/main/resources/schema.sql
```

This creates the `student_management` database with the following tables:
- `students`
- `courses`
- `enrollments`

The default credentials in `src/main/resources/database.properties` are:

```properties
db.url=jdbc:mysql://localhost:3306/student_management
db.username=root
db.password=123456
```

Change these if your MySQL setup uses different credentials.

---

## Running the Application

```bash
mvn exec:java -Dexec.mainClass=com.App
```

Or run it directly from your IDE (e.g., VS Code with the Oracle Java extension) by opening `App.java` and clicking **Run**.

---

## Project Structure

```
src/
└── main/
    ├── java/com/
    │   ├── App.java                  # Entry point, starts file transfer server
    │   ├── LogIn.java                # Login screen
    │   ├── SignUp.java               # Student registration screen
    │   ├── Home.java                 # Student dashboard
    │   ├── Adminstrator.java         # Admin login screen
    │   ├── LoadingScreen.java        # Reusable loading overlay
    │   ├── dao/
    │   │   ├── StudentDAO.java       # Student DB operations
    │   │   └── CourseDAO.java        # Course & enrollment DB operations
    │   ├── model/
    │   │   ├── Student.java          # Student entity
    │   │   └── Course.java           # Course entity
    │   ├── network/
    │   │   ├── FileTransferServer.java  # TCP server for file uploads (port 8888)
    │   │   └── FileTransferClient.java  # Client-side file transfer
    │   ├── ui/
    │   │   ├── admin/                # Admin dashboard panels
    │   │   ├── student/              # Student file viewer panel
    │   │   └── common/components/    # Shared UI components (StyledButton, RoundBorder)
    │   └── utils/
    │       ├── DatabaseUtil.java     # DB connection manager
    │       ├── Constants.java        # App-wide constants
    │       ├── UIUtils.java          # UI helper methods
    │       └── ValidationUtils.java  # Input validation helpers
    └── resources/
        ├── database.properties       # DB connection config
        ├── schema.sql                # DB schema and table definitions
        ├── security.policy           # Java security policy
        ├── icons/                    # UI icons (PNG)
        └── *.jpg                     # Course images
uploads/                              # Uploaded course material files
```

---

## Default Admin Access

The administrator panel is accessed from the Login screen via the **ADMIN** button. Admin credentials are managed separately from student accounts.

---

## Notes

- The file transfer server starts automatically on **port 8888** when the app launches. Make sure this port is free.
- Course images are loaded from the classpath — image filenames are stored in the `courses` table and must match files in `src/main/resources/`.
- Uploaded course materials are stored in the `uploads/` directory at the project root.
