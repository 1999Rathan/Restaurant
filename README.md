Restaurant Reservation System - Backend
This is the core REST API for the Restaurant Reservation System. Built with Spring Boot, it handles secure authentication via Keycloak, manages persistent data in MySQL, and enforces business logic for table bookings and staff management.

🚀 Tech Stack
Java 17

Spring Boot 3.x

Spring Security (OAuth2 Resource Server)

Spring Data JPA (Hibernate)

MySQL

Keycloak (Identity & Access Management)

🔑 Security Configuration
The backend acts as an OAuth2 Resource Server. All requests must include a valid JWT Bearer Token from Keycloak.

Roles & Permissions
ROLE_STAFF: Access to /api/staff/** (Manage tables, slots, and view occupancy).

ROLE_CUSTOMER: Access to /api/customer/** (Search, book, and view personal history).
