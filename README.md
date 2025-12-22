🎮 GameStore – Spring Boot Backend API

A RESTful backend API for the GameStore application built using Spring Boot.
The backend provides secure authentication, role-based authorization, and game management APIs, and serves as the core business layer for the GameStore frontend.

🚀 Features

🔐 JWT-based authentication

🧑‍💼 Role-Based Access Control (RBAC)

USER

ADMIN

👤 User registration & login

🕹️ Game management (Admin only)

🗂️ Genre management

❤️ Wishlist APIs

🛒 Cart APIs

🛡️ Secure endpoints with Spring Security

📄 RESTful API design

🗃️ Database integration using JPA/Hibernate

🛠️ Tech Stack

Backend: Spring Boot

Security: Spring Security, JWT

ORM: Spring Data JPA (Hibernate)

Database: PostgreSQL

Build Tool: Maven

API Testing: Postman

Java Version: Java 21

🧑‍💼 Role-Based Access Control (RBAC)
Role	Permissions
USER	Browse games, manage wishlist & cart
ADMIN	Manage games, genres, and users

Roles are stored in the database and embedded inside the JWT token.

🔐 Authentication & Authorization Flow

User registers or logs in

Server generates a JWT token containing:

User ID

Username

Role(s)

Token is sent to the client

Client sends token in Authorization header:

Authorization: Bearer <token>


Spring Security validates token for every request

Access is granted based on user role

🛡️ Security Configuration

Stateless authentication

JWT request filter

Password encryption using BCrypt

Method-level security using @PreAuthorize

Example:

@PreAuthorize("hasRole('ADMIN')")
@PostMapping("/games")
public ResponseEntity<Game> addGame(@RequestBody Game game) {
    return ResponseEntity.ok(gameService.save(game));
}

📁 Project Structure
src/main/java/com/gamestore
│── controller
│── service
│── repository
│── model
│── dto
│── security
│── exception
└── GameStoreApplication.java

🌐 API Endpoints (Sample)
🔐 Authentication
Method	Endpoint	Description
POST	/api/auth/register	Register user
POST	/api/auth/login	Login user
🕹️ Games
Method	Endpoint	Access
GET	/api/games	USER, ADMIN
POST	/api/games	ADMIN
PUT	/api/games/{id}	ADMIN
DELETE	/api/games/{id}	ADMIN
❤️ Wishlist
Method	Endpoint	Access
GET	/api/wishlist	USER
POST	/api/wishlist/{gameId}	USER
🛒 Cart
Method	Endpoint	Access
GET	/api/cart	USER
POST	/api/cart/{gameId}	USER
⚙️ Installation & Setup
1️⃣ Clone the repository
git clone https://github.com/your-username/gamestore-backend.git

2️⃣ Navigate to project directory
cd gamestore-backend


🔗 Frontend Repository

👉 Frontend (React): https://game-store-lilac-five.vercel.app/
