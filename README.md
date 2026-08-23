# Last-Mile Delivery Tracker

**Phase 1: Foundation and Authentication**

A modular monorepo for managing last-mile deliveries. This phase establishes the project foundation with JWT-based authentication. Future modules (zones, orders, tracking, notifications, etc.) will be added incrementally.

## Tech Stack

### Backend
- Java 17
- Spring Boot 3.x
- Maven
- Spring Web, Data JPA, Security
- JWT Authentication
- PostgreSQL
- Lombok, Bean Validation

### Frontend
- React 18
- Vite
- JavaScript
- React Router
- Axios
- Basic CSS

## Project Structure

```text
last-mile-delivery-tracker/
├── backend/          # Spring Boot REST API
│   └── src/main/java/com/lastmile/tracker/
│       ├── config/
│       ├── controller/
│       ├── dto/
│       ├── entity/
│       ├── repository/
│       ├── service/
│       ├── security/
│       ├── exception/
│       └── enums/
├── frontend/         # React + Vite SPA
│   └── src/
│       ├── api/
│       ├── components/
│       ├── pages/
│       ├── context/
│       └── App.jsx
├── README.md
├── .env.example
└── .gitignore
```

## Prerequisites

- Java 17+
- Maven 3.8+
- Node.js 18+ and npm
- PostgreSQL 14+

## PostgreSQL Setup

1. Install and start PostgreSQL.
2. Create the database:

```sql
CREATE DATABASE lastmile_tracker;
```

3. Note your PostgreSQL username and password for environment configuration.

## Environment Variables

Copy `.env.example` to `.env` and update values as needed:

```bash
cp .env.example .env
```

| Variable | Description | Default |
|----------|-------------|---------|
| `DB_URL` | JDBC connection URL | `jdbc:postgresql://localhost:5432/lastmile_tracker` |
| `DB_USERNAME` | Database username | `postgres` |
| `DB_PASSWORD` | Database password | `postgres` |
| `JWT_SECRET` | Secret key for JWT signing (min 32 chars) | — |
| `JWT_EXPIRATION` | Token expiry in milliseconds | `86400000` (24 hours) |
| `VITE_API_URL` | Backend API URL for frontend | `http://localhost:8080` |

For the backend, export environment variables before starting, or set them in your shell/IDE.

## How to Run Backend

```bash
cd backend

# Set environment variables (example for PowerShell)
$env:DB_URL="jdbc:postgresql://localhost:5432/lastmile_tracker"
$env:DB_USERNAME="postgres"
$env:DB_PASSWORD="postgres"
$env:JWT_SECRET="your-256-bit-secret-key-change-this-in-production-environment"

mvn spring-boot:run
```

The API runs at `http://localhost:8080`.

## How to Run Frontend

```bash
cd frontend
npm install
npm run dev
```

The frontend runs at `http://localhost:5173`.

## Authentication API Endpoints

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| POST | `/api/auth/register` | No | Register a new CUSTOMER |
| POST | `/api/auth/login` | No | Login and receive JWT |
| GET | `/api/auth/me` | Yes | Get current user profile |

### User Roles

- `CUSTOMER` — Public registration allowed
- `DELIVERY_AGENT` — Created via admin (future module)
- `ADMIN` — Created via admin (future module)

## Example API Requests

### Register

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Jane Customer",
    "email": "jane@example.com",
    "password": "password123"
  }'
```

Response:

```json
{
  "success": true,
  "message": "Registration successful",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "user": {
      "id": 1,
      "name": "Jane Customer",
      "email": "jane@example.com",
      "role": "CUSTOMER"
    }
  }
}
```

### Login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "jane@example.com",
    "password": "password123"
  }'
```

### Get Current User

```bash
curl http://localhost:8080/api/auth/me \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## Phase 2: Location and Pricing Management

This phase adds configuration APIs and a basic admin UI for defining the logistics network and pricing structures.

### Admin APIs (Secured by `ROLE_ADMIN`)

#### Zone & Area Management
- `POST /api/admin/zones` - Create a delivery zone.
- `GET /api/admin/zones` - List all zones.
- `POST /api/admin/areas` - Create an area assigned to a zone.
- `GET /api/admin/areas/pincode/{pincode}` - Find an area by 6-digit pincode.

#### Rate Cards
- `POST /api/admin/rate-cards` - Define pricing rules.
- Contains rates for `INTRA_ZONE` (pickup and drop in the same zone) vs `INTER_ZONE` (different zones).
- Contains distinct configurations for `B2B` vs `B2C` orders.

#### COD Surcharges
- `GET /api/admin/cod-surcharges` - Get current COD surcharges globally.
- `PUT /api/admin/cod-surcharges/{orderType}` - Set cash-on-delivery surcharge for B2B or B2C orders.

## Future Modules

The following are intentionally **not** implemented in Phase 1 and 2:

- Order management
- Delivery assignment
- Agent location tracking
- Order tracking
- Failed delivery / rescheduling
- Notifications

The architecture is designed so these modules can be added without rewriting authentication or network configurations.

## License

Private project — all rights reserved.
