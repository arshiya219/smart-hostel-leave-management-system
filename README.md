# Smart Hostel Leave Management System

A web-based Hostel Leave Management System developed using **Spring Boot**, **Java**, **Thymeleaf**, and **MySQL** to simplify the hostel leave approval process through a structured, role-based workflow.

The system enables students to submit leave requests, while parents, teachers, and hostel authorities participate in an organized approval process, reducing paperwork and improving transparency.

---

## Features

- Student Leave Request Submission
- Parent Login and Dashboard
- Teacher Approval Module
- Head of Department (HOD) Approval
- Hostel Coordinator Management
- Leave Status Tracking
- Role-Based Access Control
- Database Integration using MySQL
- Responsive Web Interface using Thymeleaf

---

## Workflow

```text
Student
    │
    ▼
Parent Verification
    │
    ▼
Teacher Approval
    │
    ▼
HOD Approval
    │
    ▼
Hostel Coordinator
    │
    ▼
Leave Approved / Rejected
```

---

## Technology Stack

| Category | Technologies |
|----------|--------------|
| Language | Java 17 |
| Framework | Spring Boot |
| Database | MySQL |
| ORM | Spring Data JPA / Hibernate |
| Frontend | HTML, CSS, Thymeleaf |
| Build Tool | Maven |
| Version Control | Git & GitHub |

---

## Project Structure

```
smart-hostel-leave-management-system
│
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com.hostel.smart_hostel
│   │   │       ├── controller
│   │   │       ├── entity
│   │   │       └── repository
│   │   │
│   │   └── resources
│   │       ├── templates
│   │       └── application.properties
│   │
│   └── test
│
├── pom.xml
├── mvnw
└── README.md
```

---

## Modules

### Student
- Submit leave requests
- Track leave status

### Parent
- Login to the portal
- Review leave information
- Access parent dashboard

### Teacher
- Review student leave requests
- Approve or reject requests

### Head of Department (HOD)
- Review forwarded leave requests
- Grant final academic approval

### Hostel Coordinator
- Manage hostel-related leave processing

---

## Database

The application uses **MySQL** for storing:

- Student Details
- Parent Details
- Teacher Records
- HOD Records
- Hostel Coordinator Information
- Leave Request Data

---

## Installation

### Clone the Repository

```bash
git clone https://github.com/arshiya219/smart-hostel-leave-management-system.git
```

### Navigate to the Project

```bash
cd smart-hostel-leave-management-system
```

### Configure Database

Update `application.properties` with your MySQL credentials.

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/smarthostel
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### Run the Application

```bash
./mvnw spring-boot:run
```

or

```bash
mvn spring-boot:run
```

---

## Future Enhancements

- QR Code Generation for Leave Verification
- Email Notifications
- OTP-Based Authentication
- Student Attendance Integration
- Admin Dashboard
- Leave Analytics Dashboard
- Mobile Responsive UI
- PDF Leave Report Generation

---

## Learning Outcomes

Through this project, I gained practical experience in:

- Spring Boot Development
- MVC Architecture
- RESTful Application Design
- MySQL Database Design
- Spring Data JPA
- Repository Pattern
- Role-Based System Design
- Maven Project Management
- Git Version Control

---

## Author

**Arshiya Shaik**

B.Tech – Big Data Analytics

GitHub: https://github.com/arshiya219

---

## License

This project is developed for educational and academic purposes.
