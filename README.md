# Smart Campus Helpdesk

A web-based campus helpdesk system developed as a Web Technologies and Services (WTS) course-end project.

The system provides a centralized platform for students and staff to submit campus-related issues and for administrators/helpdesk staff to manage, assign, track, and resolve those issues.

---

## 1. Project Overview

In a college campus, students and staff may face different issues related to IT support, facilities, hostel services, library services, transportation, and other campus facilities.

Traditional methods such as direct communication, phone calls, or manual reporting can make it difficult to track the status and history of complaints.

**Smart Campus Helpdesk** provides a centralized web-based system where users can:

- Submit support tickets
- Select an appropriate issue category
- Specify the issue description and location
- Set the priority of an issue
- Track ticket status
- View ticket details
- Communicate through ticket comments

Administrators/helpdesk staff can:

- View submitted tickets
- Assign tickets
- Change ticket status
- Add comments
- Record resolutions
- Maintain ticket history

The project demonstrates the integration of HTML, CSS, JavaScript, JSP, Java Servlets, JDBC, MySQL, Apache Tomcat, and the MVC architecture.

---

## 2. Objectives

The main objectives of the project are:

1. To provide a centralized platform for reporting campus issues.
2. To reduce the dependency on manual complaint handling.
3. To organize issues using predefined categories.
4. To provide priority-based ticket management.
5. To allow helpdesk staff to assign and track tickets.
6. To maintain the status and history of tickets.
7. To provide a simple and user-friendly web interface.
8. To demonstrate server-side web development using Java Servlets and JSP.
9. To demonstrate database connectivity using JDBC and MySQL.
10. To implement the MVC architecture in a web application.

---

## 3. Main Features

### Student / Staff Features

- User registration
- User login
- Dashboard
- Create support ticket
- Select ticket category
- Specify issue location
- Set ticket priority
- View submitted tickets
- View ticket details
- View ticket status
- Add comments
- View ticket history

### Admin / Helpdesk Features

- Admin/helpdesk dashboard
- View all tickets
- Search and filter tickets
- Assign tickets to staff
- Update ticket status
- Add comments
- Record resolution details
- View ticket history
- Manage support requests

---

## 4. Ticket Workflow

Tickets follow a defined lifecycle:

```text
OPEN
  |
  v
ASSIGNED
  |
  v
IN_PROGRESS
  |
  v
RESOLVED
  |
  v
CLOSED
Status Description
Status	Description
OPEN	Ticket has been submitted and is waiting for assignment
ASSIGNED	Ticket has been assigned to a staff/helpdesk member
IN_PROGRESS	Staff member is currently working on the issue
RESOLVED	The reported issue has been resolved
CLOSED	Ticket has been completed and closed
5. Ticket Priorities

The system supports the following priorities:

Priority	Description
LOW	Issue has low urgency
MEDIUM	Normal campus support issue
HIGH	Important issue requiring faster attention
URGENT	Critical issue requiring immediate attention
6. Ticket Categories

The system currently provides the following categories:

IT Support
Facilities
Hostel
Library
Transport

These categories help organize and manage different types of campus support requests.

7. Technology Stack
Technology	Purpose
HTML5	Web page structure
CSS3	User interface styling
JavaScript	Client-side interactions and validation
JSP	Dynamic web pages
Java Servlets	Server-side request processing
JDBC	Database connectivity
MySQL	Data storage
Apache Tomcat 10.1	Web application server
Maven	Project build and dependency management
Git	Version control
GitHub	Source code repository
8. Architecture

The project follows the Model-View-Controller (MVC) architecture.

                    ┌──────────────────────┐
                    │       Browser        │
                    │ HTML / CSS / JS / JSP│
                    └──────────┬───────────┘
                               │
                               v
                    ┌──────────────────────┐
                    │     Controllers      │
                    │    Java Servlets     │
                    └──────────┬───────────┘
                               │
                               v
                    ┌──────────────────────┐
                    │         DAO          │
                    │  JDBC Data Access    │
                    └──────────┬───────────┘
                               │
                               v
                    ┌──────────────────────┐
                    │        MySQL         │
                    │      Database        │
                    └──────────────────────┘

                    Model Layer
                         ▲
                         │
              Java Model Classes / Enums
MVC Components
Model

The model layer contains Java classes representing application data.

Examples include:

User
Ticket
Category
TicketComment
TicketHistory
UserRole
TicketStatus
TicketPriority
View

The view layer consists primarily of JSP pages, HTML, CSS, and JavaScript.

Examples include:

Login page
Registration page
Dashboards
Ticket creation page
Ticket details page
Administrative pages
Controller

Java Servlets handle HTTP requests and coordinate application operations.

Examples include:

LoginServlet
RegisterServlet
Ticket-related Servlets
Assignment and status management Servlets
DAO

Data Access Objects provide the database interaction layer using JDBC.

Examples include:

UserDAO
TicketDAO
CategoryDAO
TicketCommentDAO
TicketHistoryDAO
9. Database Design

The application uses MySQL as the relational database.

Main Tables
users
categories
tickets
ticket_comments
ticket_history
Users

Stores registered users and their roles.

Important fields include:

user_id
name
email
password_hash
role
department
created_at
Categories

Stores available helpdesk categories.

Important fields include:

category_id
category_name
description
active
Tickets

Stores support requests submitted by users.

Important fields include:

ticket_id
user_id
category_id
subject
description
location
priority
status
assigned_to
resolution
created_at
updated_at
Ticket Comments

Stores communication and comments associated with tickets.

Ticket History

Maintains changes and actions performed on tickets.

10. Project Structure

The project follows a Maven web application structure.

Smart Campus Helpdesk
│
├── database
│   └── schema.sql
│
├── src
│   └── main
│       ├── java
│       │   └── com
│       │       └── smartcampus
│       │           └── helpdesk
│       │               ├── controller
│       │               ├── dao
│       │               ├── model
│       │               └── util
│       │
│       ├── resources
│       │   └── db.properties.example
│       │
│       └── webapp
│           ├── WEB-INF
│           ├── css
│           ├── js
│           ├── login.jsp
│           ├── register.jsp
│           └── ...
│
├── pom.xml
├── README.md
└── .gitignore
11. Requirements

Before running the project, install the following:

Java JDK 21
Apache Maven
MySQL 8.0+
Apache Tomcat 10.1
Git
A modern web browser

Verify Java:

java -version

Verify Maven:

mvn -version

Verify Git:

git --version
12. Database Setup
Step 1: Start MySQL

On Windows:

net start MySQL80
Step 2: Open MySQL
mysql -u root -p
Step 3: Create the database

The database schema is available in:

database/schema.sql

Run:

mysql -u root -p < database\schema.sql

Alternatively, inside MySQL:

SOURCE database/schema.sql;

The database used by the application is:

smart_campus_helpdesk
Step 4: Verify the database
USE smart_campus_helpdesk;

SHOW TABLES;

Expected tables:

categories
ticket_comments
ticket_history
tickets
users
13. Database Configuration

The project provides a configuration template:

src/main/resources/db.properties.example

Create a local configuration file:

src/main/resources/db.properties

Configure it according to your local MySQL installation:

db.url=jdbc:mysql://localhost:3306/smart_campus_helpdesk
db.username=your_mysql_username
db.password=your_mysql_password
Important

db.properties contains local database credentials and should not be committed to GitHub.

The project .gitignore excludes this file.

Only the following template should be committed:

db.properties.example
14. Build the Project

Open a terminal in the project directory:

cd /d "D:\JAYANTH CSE\3rd yr SEM_5\WTS\Course End Project\Smart Campus Helpdesk"

Run:

mvn clean package

If successful, Maven will generate:

target/smart-campus-helpdesk.war
15. Deploy to Apache Tomcat

Set the Tomcat installation path:

set CATALINA_HOME=C:\Program Files\Apache Software Foundation\Tomcat 10.1

Copy the generated WAR file:

copy /Y "target\smart-campus-helpdesk.war" "%CATALINA_HOME%\webapps\smart-campus-helpdesk.war"

Start Tomcat:

"%CATALINA_HOME%\bin\catalina.bat" run

Wait for the server to start.

16. Access the Application

Open:

http://localhost:8080/smart-campus-helpdesk/

The registration page can be accessed through:

http://localhost:8080/smart-campus-helpdesk/register

The login page can be accessed through:

http://localhost:8080/smart-campus-helpdesk/login.jsp
17. Authentication

The current project configuration contains a simplified authentication flow intended for the college project demonstration.

For demonstration purposes, the login and registration flow can use simplified credential handling rather than implementing a production-grade identity management system.

The application still maintains HTTP sessions and protected application pages.

For a production deployment, authentication should be replaced with proper credential verification, stronger account management, password policies, and additional security controls.

18. Password Security

When password hashing is used, the project uses:

PBKDF2WithHmacSHA256

The password utility generates a random salt and derives a password hash before storage.

Passwords should not be stored as plain text in the database.

19. Security Considerations

The project includes several basic security practices:

Password hashing
Prepared statements through JDBC
Session-based authentication
Role-based application access
Input validation
Database credentials kept outside version control
.gitignore protection for local configuration
Parameterized SQL queries to reduce SQL injection risk

The system is intended as an academic project and should not be considered production-ready security software.

20. Example Workflow

A typical helpdesk workflow is:

Student / Staff
      |
      v
Login / Registration
      |
      v
Dashboard
      |
      v
Create Ticket
      |
      v
Select Category
      |
      v
Enter Issue Details
      |
      v
Set Priority
      |
      v
Submit Ticket
      |
      v
       OPEN
        |
        v
     ASSIGNED
        |
        v
    IN_PROGRESS
        |
        v
     RESOLVED
        |
        v
      CLOSED
21. Example Ticket

Example:

Category: IT Support

Subject:
Unable to connect to campus Wi-Fi

Description:
The system is unable to connect to the campus Wi-Fi network.

Location:
Computer Science Block

Priority:
HIGH

The helpdesk can then assign the ticket to a staff member and update its status as the issue is handled.

22. Testing

The project can be tested using the following scenarios:

User Management
Register a user
Login
Logout
Access dashboard
Ticket Management
Create a ticket
Select category
Set priority
View ticket
Update ticket status
Helpdesk Management
View tickets
Search tickets
Filter tickets
Assign tickets
Update ticket status
Add comments
Record resolution
Database Testing

Verify records using:

SELECT * FROM users;
SELECT * FROM categories;
SELECT * FROM tickets;
SELECT * FROM ticket_comments;
SELECT * FROM ticket_history;
23. Troubleshooting
MySQL connection error

Check:

db.url=jdbc:mysql://localhost:3306/smart_campus_helpdesk
db.username=your_mysql_username
db.password=your_mysql_password

Also verify that MySQL is running.

Port 8080 already in use

Check whether another application is using port 8080 or configure Tomcat to use another port.

Maven build failure

Check:

java -version
mvn -version

The project is configured for Java 21.

Application not loading

Check that:

Tomcat is running.
The WAR exists in the Tomcat webapps directory.
The WAR deployment completed successfully.
The URL contains the correct application context.
24. Version Control

The project uses Git for version control.

Initialize the repository:

git init

Add files:

git add .

Create a commit:

git commit -m "Initial commit - Smart Campus Helpdesk"

Connect the repository to GitHub:

git remote add origin https://github.com/YOUR_USERNAME/smart-campus-helpdesk.git

Rename the branch:

git branch -M main

Push the project:

git push -u origin main
25. Future Enhancements

Possible future improvements include:

Email notifications for ticket updates
File/image attachments for tickets
Advanced role and permission management
Real-time notifications
Analytics and reporting dashboard
Ticket SLA monitoring
Mobile-friendly improvements
Integration with institutional authentication
Improved password and account security
Automated ticket categorization
Knowledge-base integration
26. Academic Context

Project: Smart Campus Helpdesk

Course: Web Technologies and Services (WTS)

Project Type: Course-End Project

The project demonstrates practical application of:

HTML5
CSS3
JavaScript
JSP
Java Servlets
JDBC
MySQL
Apache Tomcat
MVC architecture
Git and GitHub
27. Authors

Developed as a college course-end project by:

Smart Campus Helpdesk Project Team

28. License

This project is developed for academic and educational purposes.

© 2026 Smart Campus Helpdesk Project Team
