GameStore - Spring Boot Backend API

A RESTful backend API for the GameStore application built using Spring Boot. The backend provides secure authentication, role-based authorization, and game management APIs, serving as the core business layer for the GameStore frontend.

Features

JWT-based Authentication

Role-Based Access Control (RBAC)

USER: Browse games, manage wishlist & cart

ADMIN: Manage games, genres, and users

User registration & login

Game management (Admin only)

Genre management

Wishlist APIs

Cart APIs

Secure endpoints with Spring Security

RESTful API design

Database integration using JPA/Hibernate

Tech Stack

Backend: Spring Boot

Security: Spring Security, JWT

ORM: Spring Data JPA (Hibernate)

Database: PostgreSQL

Build Tool: Maven

API Testing: Postman

Java Version: Java 21

Authentication & Authorization Flow

User registers or logs in.

Server generates a JWT token containing:

User ID

Username

Role(s)

Token is sent to the client.

Client sends token in Authorization header:
Authorization: Bearer <token>

Spring Security validates the token for every request.

Access is granted based on user role.

Security Configuration

Stateless authentication

JWT request filter

Password encryption using BCrypt

Method-level security using @PreAuthorize

Example of method-level security:

@PreAuthorize("hasRole('ADMIN')")
@PostMapping("/games")
public ResponseEntity<Game> addGame(@RequestBody Game game) {
    return ResponseEntity.ok(gameService.save(game));
}

Project Structure
src/main/java/com/gamestore
│── controller
│── service
│── repository
│── model
│── dto
│── security
│── exception
└── GameStoreApplication.java

API Endpoints (Sample)
Authentication

POST /api/auth/register - Register user

POST /api/auth/login - Login user

Games

GET /api/games - Access for USER, ADMIN

POST /api/games - Access for ADMIN

PUT /api/games/{id} - Access for ADMIN

DELETE /api/games/{id} - Access for ADMIN

Wishlist

GET /api/wishlist - Access for USER

POST /api/wishlist/{gameId} - Access for USER

Cart

GET /api/cart - Access for USER

POST /api/cart/{gameId} - Access for USER

Installation & Setup

Clone the repository:

git clone https://github.com/your-username/gamestore-backend.git


Navigate to the project directory:

cd gamestore-backend

Frontend Repository

Frontend (React)
