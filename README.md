# Hall Management System

Microservices-based Hall Booking platform (Spring Boot + React).  
**Modules:** MainApplication, Notifications, Payments, Booking, Vendor/Admin, Frontend.

## Run (Backend)
- Java 17+, Maven/Gradle
- Configure env vars:
  - TWILIO_ACCOUNT_SID
  - TWILIO_AUTH_TOKEN
  - TWILIO_FROM_NUMBER
- ./mvnw spring-boot:run (or ./gradlew bootRun)

## Run (Frontend)
- Node 18+
- yarn && yarn dev (or 
pm i && npm run dev)

## Security
Secrets are managed via environment variables. No secrets committed.
