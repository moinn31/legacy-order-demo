# legacy-order-demo

A small **Java 8** Maven web application using **embedded Jetty 9.4** and **`javax.servlet`** (`javax.servlet-api` 3.1.0), designed as a clean baseline for testing migration to **Java 17**.

## Features

- **Single-page Order Calculator UI** (`/`) — enter product price, quantity, and discount percentage to calculate subtotal, discount amount, and final total.
- **Health endpoint** (`GET /health`) — returns service status and JVM version as JSON.
- **Order calculation API** (`POST /api/orders/calculate`) — validates inputs and calculates order totals using `BigDecimal` (`RoundingMode.HALF_UP`).
- **JUnit 4 test suite** — covers totals, discounts, rounding, zero quantity, invalid inputs, and HTTP endpoint behavior.

## Prerequisites

- **JDK 8** (or newer JDK; compiler source/target is set to `1.8`).
- No local Maven installation is required — use the included Maven Wrapper (`./mvnw` on macOS/Linux, `mvnw.cmd` on Windows).

## Build and Test

Using the Maven Wrapper:

```bash
# Linux / macOS
./mvnw clean test
```

```powershell
# Windows
.\mvnw.cmd clean test
```

To build the executable shaded JAR:

```bash
./mvnw clean package
```

## Run the Application

Option 1 — Run directly with Maven:

```bash
./mvnw exec:java
```

Option 2 — Run the packaged JAR:

```bash
java -jar target/legacy-order-demo-1.0.0-SNAPSHOT.jar
```

By default, the server starts on port `8080` (override with `-Dserver.port=9090` or `PORT=9090`):

- Web UI: http://localhost:8080/
- Health check: http://localhost:8080/health
- Calculate API: `POST http://localhost:8080/api/orders/calculate`

Example API request:

```bash
curl -X POST http://localhost:8080/api/orders/calculate \
  -H "Content-Type: application/json" \
  -d '{"price": 24.99, "quantity": 3, "discount": 10}'
```

## Publish to GitHub

1. Create a new empty repository on GitHub (without a README or `.gitignore`).
2. Initialize Git, commit the project, and push:

```bash
git init
git add .
git commit -m "Initial commit: legacy-order-demo Java 8 app"
git branch -M main
git remote add origin https://github.com/<your-username>/legacy-order-demo.git
git push -u origin main
```
