# Last-Mile Delivery Tracker

**Phases 1–9: Delivery Operations and Final Readiness**

A modular monorepo for managing last-mile deliveries from order creation through assignment, tracking, failed-delivery rescheduling, and notifications. The final phase focuses on integration verification, security hardening, documentation, and deployment readiness.

## Tech Stack

### Backend
- Java 17
- Spring Boot 3.x
- Maven
- Spring Web, Data JPA, Security, Validation, Mail
- JWT Authentication
- PostgreSQL
- Lombok, Bean Validation

### Frontend
- React 18
- Vite
- JavaScript
- React Router
- Axios
- Responsive CSS

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
| `DB_USERNAME` | Database username | required |
| `DB_PASSWORD` | Database password | required |
| `JWT_SECRET` | Secret key for JWT signing (min 32 chars) | required |
| `JWT_EXPIRATION` | Token expiry in milliseconds | `86400000` (24 hours) |
| `VITE_API_BASE_URL` | Backend API URL for frontend | `http://localhost:8080` |
| `MAIL_HOST` | SMTP host for email notifications | optional |
| `MAIL_PORT` | SMTP port | `587` |
| `MAIL_USERNAME` | SMTP username | optional |
| `MAIL_PASSWORD` | SMTP password | optional |
| `MAIL_FROM` | Sender address | optional |

For the backend, export environment variables before starting, or set them in your shell/IDE.

## How to Run Backend

```bash
cd backend

# Set environment variables (example for PowerShell)
$env:DB_URL="jdbc:postgresql://localhost:5432/lastmile_tracker"
$env:DB_USERNAME="postgres"
$env:DB_PASSWORD="your-database-password"
$env:JWT_SECRET="replace-with-a-random-secret-of-at-least-32-characters"

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

## Operations Workflows

### Pricing

The backend is the pricing source of truth. It resolves each pincode to a zone, selects `INTRA_ZONE` or `INTER_ZONE`, then applies the matching B2B/B2C rate card.

```text
Volumetric Weight = length × breadth × height / 5000
Chargeable Weight = max(actual weight, volumetric weight)
Final Price = base rate + weight-based charge + applicable COD surcharge
```

Customers request a quote through `POST /api/orders/price` before confirming an order with `POST /api/orders`.

### Assignment and Tracking

Admins can manually assign or auto-assign available delivery agents. Agents update assigned orders through the existing status API. Valid delivery progression is:

```text
PENDING → PICKED_UP → IN_TRANSIT → OUT_FOR_DELIVERY → DELIVERED
                                                └──────→ FAILED
```

Tracking history is append-only and visible through `GET /api/orders/{id}/tracking`.

### Rescheduling

Customers can request a future delivery date for failed orders. Admins approve or reject requests. Approval clears the assignment, returns the order to `PENDING`, and records the transition; assignment can then be performed again through the admin assignment APIs.

### Notifications

Authenticated users can access only their own in-app notifications:

- `GET /api/notifications`
- `GET /api/notifications/unread-count`
- `PUT /api/notifications/{id}/read`
- `PUT /api/notifications/read-all`

Important events also attempt email delivery when SMTP variables are configured. Email failures are logged and do not roll back business operations.

## Testing

```bash
cd backend
mvn clean compile
mvn test

cd ../frontend
npm install
npm run build
```

Behavioral API testing requires a running PostgreSQL instance and seeded zones, areas, rate cards, surcharges, users, and agents. The frontend can be previewed with `npm run preview` after a successful build.

## Deployment Readiness

Recommended deployment structure:

- Backend: package and run the Spring Boot application.
- Database: managed PostgreSQL with migrations or controlled Hibernate schema updates.
- Frontend: build the React/Vite app and serve the generated `dist/` directory from a static host or reverse proxy.

Set production environment variables through the hosting platform, never by committing `.env`. Use a random JWT secret, restricted database credentials, and real SMTP credentials only in the deployment secret store.

## Known Limitations

- No automated unit, integration, or end-to-end tests are currently included.
- PostgreSQL and SMTP must be available and configured for full runtime verification.
- Email delivery is best-effort; in-app notifications remain the durable notification record.
- The project does not include deployment manifests, containerization, maps, GPS, SMS, or push notifications.

## License

Private project — all rights reserved.
