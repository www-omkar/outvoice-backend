# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

OutVoice is an invoicing backend built with Spring Boot 3.5.14 / Java 21. It handles user auth, organization management, and PDF invoice generation. The app is in early development — only the initial scaffold exists so far.

## Commands

```bash
# Build (skip tests)
./mvnw clean package -DskipTests

# Run (requires PostgreSQL running)
./mvnw spring-boot:run

# Run all tests
./mvnw test

# Run a single test class
./mvnw test -Dtest=ClassName

# Run a single test method
./mvnw test -Dtest=ClassName#methodName
```

On Windows use `mvnw.cmd` instead of `./mvnw`.

## Stack & Key Dependencies

| Concern | Library |
|---|---|
| Web / REST | Spring Web (Servlet) |
| Auth | Spring Security + JJWT 0.12.6 |
| Persistence | Spring Data JPA + PostgreSQL |
| Migrations | Flyway (PostgreSQL dialect) |
| PDF generation | OpenPDF 2.0.3 |
| Email | Spring Mail |
| Boilerplate reduction | Lombok |

## Architecture

Base package: `com.omkarsathe.outvoice`

Intended layering (feature-based slices inside the base package):

```
outvoice/
  auth/           # JWT filter, SecurityConfig, signup/login endpoints
  user/           # User entity, repository, service
  organization/   # Org entity + tax details (PAN/GST/TAN)
  invoice/        # Invoice entity, PDF generation via OpenPDF
  common/         # Shared utilities, exceptions, response wrappers
```

Flyway migration scripts go in `src/main/resources/db/migration/` with the standard `V{n}__{description}.sql` naming.

## Configuration

`src/main/resources/application.yaml` — currently minimal. Environment-specific values (DB URL, JWT secret, mail credentials) should be provided via environment variables or a local `application-local.yaml` (gitignored).

Required / notable env vars:

| Variable | Default | Purpose |
|---|---|---|
| `DB_URL` | `jdbc:postgresql://localhost:5432/outvoice` | PostgreSQL JDBC URL |
| `DB_USERNAME` | `postgres` | DB user |
| `DB_PASSWORD` | *(none)* | DB password |
| `JWT_SECRET` | *(required)* | Base64-encoded 256-bit HS256 signing key |
| `JWT_EXPIRY_MS` | `86400000` (24 h) | Token TTL in milliseconds |
| `PORT` | `8080` | HTTP server port |
| `CORS_ALLOWED_ORIGINS` | `http://localhost:4200` | Comma-separated list of allowed CORS origins |
| `ALLOW_DUPLICATE_SIGNUP` | `false` | Dev/test: return existing token instead of 409 |
| `RATE_LIMIT_AUTH_CAPACITY` | `10` | Token bucket size per IP for auth endpoints |
| `RATE_LIMIT_AUTH_REFILL_SECONDS` | `60` | Refill window in seconds |

## Auth Design

- Stateless JWT-based auth; no sessions.
- Signup accepts email **or** mobile number (not both required), plus organization details.
- Tokens signed with HS256 via JJWT; filter chain should extend `OncePerRequestFilter`.
