# 🔗 URL Shortener Service

A production-ready URL Shortener REST API built with Spring Boot, Redis, and PostgreSQL.

## Features
- Shorten long URLs to 6-character Base62 codes
- JWT-based user authentication
- Redis caching for fast redirects (cache-aside pattern)
- Rate limiting (20 req/min per IP) via Bucket4j
- Link expiry (30 days)
- Global exception handling
- Flyway database migrations
- Swagger UI for API documentation
- Fully Dockerized

## Tech Stack
| Layer | Technology |
|---|---|
| Framework | Spring Boot 3.5 |
| Language | Java 21 |
| Database | PostgreSQL 15 |
| Cache | Redis 7 |
| Auth | Spring Security + JWT |
| Rate Limiting | Bucket4j |
| DB Migration | Flyway |
| Docs | Swagger / OpenAPI |
| Container | Docker + Docker Compose |

## API Endpoints
| Method | Endpoint | Auth | Description |
|---|---|---|---|
| POST | /api/auth/register | No | Register a new user |
| POST | /api/auth/login | No | Login, get JWT token |
| POST | /api/urls/shorten | Yes | Create a short URL |
| GET | /api/urls/my-links | Yes | List all your links |
| DELETE | /api/urls/{code} | Yes | Delete a short URL |
| GET | /{code} | No | Redirect to original URL |

## Quick Start

### Prerequisites
- Java 21
- Docker Desktop

### Run the project
# Clone the repo
git clone https://github.com/priyalxprx/url-shortener.git
cd url-shortener

# Start PostgreSQL and Redis
docker-compose up -d

# Run the app
./mvnw spring-boot:run

### Access
- API: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html

## Architecture
- Cache-Aside pattern: check Redis first → miss → fetch PostgreSQL → re-cache
- JWT stateless authentication — no sessions
- Rate limiting per IP using Token Bucket algorithm
- Flyway auto-runs migrations on startup