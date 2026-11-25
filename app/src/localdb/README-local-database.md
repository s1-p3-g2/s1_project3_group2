# Local Rate My Duck database

This folder contains a separate Docker Compose setup for a PostgreSQL 17 database and the schema that initializes it.

## Start it

From PowerShell, change to the folder containing these three files and run:

```powershell
docker compose -f .\docker-compose.database.yml up -d
```

Check the container and startup logs:

```powershell
docker compose -f .\docker-compose.database.yml ps
docker compose -f .\docker-compose.database.yml logs database
```

The database is available on the computer at `localhost:5432` with these local development settings:

- Database: `ratemyduck`
- Username: `ratemyduck`
- Password: `ratemyduck_local`

For a Spring Boot app running directly on the computer, use:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/ratemyduck
spring.datasource.username=ratemyduck
spring.datasource.password=ratemyduck_local
```

Keep the PostgreSQL JDBC driver in the backend's runtime dependencies. These credentials are for local development only.

## How the files work

- `image: postgres:17` tells Docker Compose to download and run the PostgreSQL 17 image if it is not already present.
- The `database` service is the running container created from that image.
- The named volume `ratemyduck_postgres_data` stores PostgreSQL's data directory outside the container. Restarting or recreating the container keeps the database data.
- The SQL file is mounted read-only into PostgreSQL's initialization directory. The official image runs it when it initializes a new, empty data volume. It creates the users, ducks, ratings, and comments tables, their relationships and indexes. Comments are limited to 500 characters.
- The health check waits for PostgreSQL to accept connections.

Stop the container while keeping the data:

```powershell
docker compose -f .\docker-compose.database.yml down
```

Do not add `--volumes` / `-v` to that command unless you intend to delete the database volume and all data in it. Initialization SQL runs only when the data volume is new; changing the SQL later does not automatically alter an existing database. Use database migrations for later schema changes.

If port 5432 is already in use, change the Compose port mapping to `"5433:5432"` and connect from the host to `localhost:5433`.
