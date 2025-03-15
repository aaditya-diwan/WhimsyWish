# WhimsyWish Backend

E-commerce platform backend for gifting your loved ones.

## Prerequisites

- Docker and Docker Compose
- Java 21 (for local development without Docker)
- Maven (for local development without Docker)

## Running the Application with Docker

### Quick Start

1. Clone the repository
2. Run the application using Docker Compose:

```bash
docker-compose up
```

The application will be available at http://localhost:8080

### Clean Start (Removing Existing Database)

To completely reset the application and database:

#### On Linux/Mac:
```bash
./clean-and-redeploy.sh
```

#### On Windows:
```bash
clean-and-redeploy.bat
```

This will:
1. Stop all running containers
2. Remove the PostgreSQL volume (deleting all data)
3. Rebuild and restart the application

## Database Access

The application includes a pgAdmin web interface for database management:

- URL: http://localhost:5050
- Email: admin@whimsywish.com
- Password: admin

To set up access to the PostgreSQL server, create a new server in pgAdmin with:
- Host: postgres
- Port: 5432
- Username: postgres
- Password: postgres
- Database: ecommerce

## Database Migration

The application uses Flyway for database migrations. The migration files are in the `src/main/resources/db/migration` directory:

- **V1__initial_schema.sql**: Creates all database tables including users, products, categories, orders, etc.
- **V2__sample_data.sql**: Populates the database with sample data for testing

These files are executed in order when the application starts. If you need to reset the database schema:

1. Run the clean-and-redeploy script which will delete the existing PostgreSQL volume
2. The application will automatically reapply all migrations when it starts

## API Documentation

Once the application is running, you can access the Swagger UI at:
http://localhost:8080/swagger-ui.html

## Development

### Configuration

- Default configuration is in `application.properties`
- Docker-specific configuration is in `application-docker.properties`
- Database cleanup configuration is in `application-clean.properties`

### Authentication

For local development, the following credentials are available:
- Regular user:
  - Username: test@example.com
  - Password: password
- Admin user:
  - Username: admin@example.com
  - Password: password

## Technologies Used

- Spring Boot 3.4.3
- Spring Security with JWT
- Spring Data JPA
- PostgreSQL
- Flyway for database migrations
- Lombok and MapStruct
- Swagger/OpenAPI documentation
- Docker for containerization

## License

[Your license information here] 