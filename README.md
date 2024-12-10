# **PerScholas Capstone Hospital - Doctor Appointment Booking System**

### **Table of Contents**
* Overview
* Features
* Technologies Used
* Setup Instructions
* Project Structure
* Usage
* Future Enhancements

### **Overview**

This project is a Doctor Appointment Booking System developed as a capstone project for PerScholas. 
It is designed to facilitate seamless interactions between patients, doctors, and administrators, 
providing functionalities like appointment booking, user role-based dashboards, and secure authentication.

### **Features**

#### **User Roles:**

* Admin: Manage users, appointments, and system settings.
* Doctor: View and manage appointments, update appointment statuses.
* Patient: Book, view, and manage appointments.

#### **Authentication and Authorization:**

* Role-based access using Spring Security.
* Custom login and logout functionality.

##### **Appointment Management:**
* Booking, rescheduling, and canceling appointments.
* Appointment status updates (e.g., confirmed, completed).

##### **Responsive Design:**

* User-friendly interface optimized for various devices.

##### **Messaging System:**

* View messages related to appointments and system updates (future implementation).

### **Technologies Used**

##### Backend:
* Java (Spring Boot)
* Spring Security
* Hibernate (JPA)

##### **Frontend:**
* Thymeleaf
* HTML/CSS
* Bootstrap 5

##### Database:

* MySQL

##### Others:

* Maven

### Setup Instructions

##### Prerequisites

1. Java Development Kit (JDK) 17 or higher
2. Maven for dependency management
3. MySQL database
4. Git for version control


### **Future Enhancements**

* Add notification features (email and SMS) for appointment updates.
* Implement a messaging system for communication between patients and doctors.
* Enable search and filter functionalities for appointments.
* Add a reporting system for analytics and insights.