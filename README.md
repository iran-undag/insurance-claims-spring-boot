# Insurance Claims Spring Boot

A sample insurance claims REST API implemented with Spring Boot, Java 17,
Spring Web MVC, Spring Data JPA, and PostgreSQL 16.

The application exposes these endpoints:

- `POST /api/claims`
- `GET /api/claims/{id}`
- `GET /api/claims?page=0&size=20`

Pagination is zero-based, and the maximum page size is 100. The application
runs on HTTP port `8082`.

## Comparison with the previous samples

| Concern | Traditional Spring, Java config | Traditional Spring, XML config | Spring Boot |
|---|---|---|---|
| Java version | Java 8 | Java 8 | Java 17 |
| Configuration | Java configuration classes | Spring XML files | Auto-configuration plus properties |
| Persistence API | `javax.persistence` | `javax.persistence` | `jakarta.persistence` |
| Validation API | `javax.validation` | `javax.validation` | `jakarta.validation` |
| Repository implementation | Direct `EntityManager` access | Spring Data JPA | Spring Data JPA |
| Deployment | WAR deployed to external Tomcat | WAR deployed to external Tomcat | Executable JAR with embedded Tomcat |
| Application startup | Servlet container initializes Spring | Servlet container loads XML | `SpringApplication.run(...)` |
| Dependency versions | Managed individually | Managed individually | Managed by the Spring Boot parent |

Spring Boot auto-configures the embedded Tomcat server, Spring MVC,
Jackson, the datasource, Hibernate, transaction management, and Spring Data
repository support based on the dependencies and application properties.

The application still uses normal Spring annotations such as `@Service`,
`@Repository`, `@RestController`, and `@Transactional`. Spring Boot reduces
infrastructure configuration; it does not replace Spring Framework.

## Spring Boot version decision

This sample uses Spring Boot `3.2.1`.

Spring Boot 3.2.1 was released on December 21, 2023. Its reference
documentation states that it requires Java 17 and is compatible through
Java 21.

References:

- [Spring Boot 3.2.1 release announcement](https://spring.io/blog/2023/12/21/spring-boot-3-2-1-available-now/)
- [Spring Boot 3.2.1 reference documentation](https://docs.spring.io/spring-boot/docs/3.2.1/reference/pdf/spring-boot-reference.pdf)

Spring Boot 3.2.x reached the end of open-source support on November 21,
2024. Version 3.2.1 is retained in this sample because the project
specifically demonstrates a Spring Boot version released in 2023. A new
production application should use a currently supported Spring Boot version.

See the
[Spring Boot 3.2 end-of-support announcement](https://spring.io/blog/2024/11/21/spring-boot-3-2-12-available-now/).

## Jakarta migration

Spring Boot 3 uses Spring Framework 6 and Jakarta EE APIs. Code that used
Java 8-era `javax.*` packages in the traditional Spring samples now uses
Jakarta namespaces, including:

- `jakarta.persistence`
- `jakarta.validation`
- `jakarta.servlet`, when servlet APIs are needed directly

These package changes are not interchangeable imports. Applications moving
from Spring Boot 2 or older Spring Framework versions must update their
dependencies and source imports together.

## Project structure

The application uses a package-by-layer structure under
`com.companyx.insuranceclaims`:

```text
controller/   Spring MVC REST controllers
dto/          Request, response, pagination, and error DTOs
entity/       JPA entities and persistence enums
exception/    Application exceptions, error codes, and exception handling
repository/   Spring Data JPA repositories
service/      Transactional business operations
```

## Baseline schema ownership

This application intentionally uses Docker initialization rather than Flyway
to create its database schema.

In the baseline:

- `database/00-init.sql` creates both PostgreSQL databases.
- The same initialization script creates the `claims` table in both databases.
- `spring.jpa.hibernate.ddl-auto=validate` makes Hibernate validate the schema.
- Hibernate does not create, update, or migrate tables.
- There are no Flyway dependencies, migrations, beans, or properties.

The databases are:

| Purpose | Database |
|---|---|
| Application | `insurance_claims_boot` |
| Automated tests | `insurance_claims_boot_test` |

The test suite uses real PostgreSQL. H2 is not used.

Docker initialization scripts run only when PostgreSQL initializes an empty
data directory. They do not run again while the existing named volume is
retained.

## Prerequisites

The verified development environment uses:

- Java 17
- Maven 3.9 or later
- Docker Engine
- Docker Compose
- Eclipse IDE with Maven support

Check the command-line tools:

```bash
java -version
javac -version
mvn -version
docker version
docker compose version
```

Both `java -version` and the Java version reported by `mvn -version` should
show Java 17.

## Select Java 17

On the verified Ubuntu environment, Java 17 is installed here:

```text
/usr/lib/jvm/java-17-openjdk-amd64
```

Select it for the current terminal:

```bash
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
export PATH="$JAVA_HOME/bin:$PATH"
```

Verify Maven is using it:

```bash
mvn -version
```

The output must report Java 17. The `<java.version>17</java.version>` Maven
property controls the compiled source level, but it does not select the JDK
that launches Maven.

## Environment variables

Create a local `.env` file in the project root:

```properties
DB_USERNAME=claims_app
DB_PASSWORD=replace_with_password
```

Replace the example password with a local development password. Do not commit
`.env`; it is excluded by `.gitignore`.

Docker Compose automatically reads the project's `.env` file for Compose
variable substitution. Maven and `java -jar` do not automatically load that
file.

Before running Maven tests or starting the application directly, export the
variables into the current shell:

```bash
set -a
source .env
set +a
```

Check that the variables exist without printing their values:

```bash
test -n "$DB_USERNAME" && echo "DB_USERNAME is set"
test -n "$DB_PASSWORD" && echo "DB_PASSWORD is set"
```

## Import into Eclipse

1. Select **File > Import**.
2. Select **Maven > Existing Maven Projects**.
3. Choose the `insurance-claims-spring-boot` project directory.
4. Ensure `pom.xml` is selected and finish the import.
5. Open **Window > Preferences > Java > Installed JREs**.
6. Add `/usr/lib/jvm/java-17-openjdk-amd64` if it is not already present.
7. Open the project's **Properties > Java Build Path > Libraries** and ensure
   its JRE System Library uses Java 17.
8. Select **Maven > Update Project** from the project's context menu.

The Maven command line remains the authoritative build check. If Eclipse uses
another JDK, its editor or launch configuration may disagree with a successful
command-line build.

## Start PostgreSQL

From the project directory:

```bash
docker compose up -d postgres
```

Check its status:

```bash
docker compose ps
```

The PostgreSQL service should report `healthy`.

The host port is `5432`. The application and test databases share this
PostgreSQL container but remain separate databases.

## Run the automated tests

Make sure Java 17 and the database credentials are available in the current
shell:

```bash
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
export PATH="$JAVA_HOME/bin:$PATH"

set -a
source .env
set +a
```

Run the complete suite:

```bash
mvn test
```

The tests include:

- JUnit 5 unit tests
- Mockito service tests
- Jackson serialization and deserialization tests
- Jakarta Bean Validation tests
- Spring MVC controller tests
- Spring Boot context tests
- Spring Data JPA integration tests against `insurance_claims_boot_test`

The integration tests do not substitute H2 for PostgreSQL.

## Run locally with Maven

With PostgreSQL running and the environment variables exported:

```bash
mvn spring-boot:run
```

Wait for the log to report:

```text
Tomcat started on port 8082
```

The API is then available at:

```text
http://localhost:8082/api/claims
```

Stop the application with `Ctrl+C`.

## Build and run the executable JAR

Build and test the application:

```bash
mvn clean package
```

Run the executable JAR:

```bash
java -jar target/insurance-claims-spring-boot-0.0.1-SNAPSHOT.jar
```

This starts Spring Boot's embedded Tomcat on port `8082`. No separately
installed Tomcat server is required.

Stop the application with `Ctrl+C`.

## Run the complete stack with Docker Compose

Stop any locally running instance of the application before starting the
Compose stack because both use host port `8082`.

The Dockerfile copies the already-built executable JAR into a Java 17 runtime
image. It does not run Maven inside the image, so package the application
first:

```bash
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
export PATH="$JAVA_HOME/bin:$PATH"

set -a
source .env
set +a

mvn clean package
```

Validate the Compose file without displaying its resolved configuration:

```bash
docker compose config --quiet
```

Build the application image and start both services:

```bash
docker compose up --build -d
```

Compose reads `.env`, starts PostgreSQL, waits for its health check to pass,
and then starts the application. Inside the Compose network, the application
connects to the database host named `postgres`, not `localhost`.

Check the services:

```bash
docker compose ps
```

Both `postgres` and `app` should be running, and PostgreSQL should report
`healthy`.

Inspect the application startup log:

```bash
docker compose logs --no-color --tail=40 app
```

The log should show Java 17, Spring Boot 3.2.1, a successful PostgreSQL
connection, Hibernate schema validation, and embedded Tomcat on port `8082`.

## Rebuild after changing source code

Because the Dockerfile copies the packaged JAR, rebuild the JAR before
rebuilding the image:

```bash
mvn clean package
docker compose up --build -d
```

`docker compose up --build -d` rebuilds the image and recreates the
application container when its image or configuration changes. A separate
`docker build` command is not required for the normal workflow.

The `--force-recreate` option is normally unnecessary. Use it only when a
container must be recreated despite Compose detecting no relevant change:

```bash
docker compose up --build --force-recreate -d app
```

Recreating the application container does not remove the PostgreSQL named
volume.

## API examples

The examples assume the application is available at `http://localhost:8082`.

### Create a claim

```bash
curl -i -X POST http://localhost:8082/api/claims \
  -H 'Content-Type: application/json' \
  -d '{
    "claimNumber": "CLM-BOOT-README-001",
    "policyNumber": "POL-BOOT-README-001",
    "claimantName": "Maria Santos",
    "incidentDate": "2023-12-15",
    "claimType": "AUTO",
    "claimedAmount": 1850.75,
    "description": "Rear bumper damage"
  }'
```

The response status is `201 Created`. Its body has this shape:

```json
{
  "id": 1,
  "claimNumber": "CLM-BOOT-README-001",
  "policyNumber": "POL-BOOT-README-001",
  "claimantName": "Maria Santos",
  "incidentDate": "2023-12-15",
  "claimType": "AUTO",
  "claimedAmount": 1850.75,
  "status": "SUBMITTED",
  "description": "Rear bumper damage",
  "createdAt": "2026-09-21T12:49:29.344862"
}
```

The generated `id` and `createdAt` value will differ. `LocalDate` values are
serialized as ISO-8601 strings such as `2023-12-15`, not numeric arrays.

### Retrieve a claim

Replace `1` with the generated ID returned by the create request:

```bash
curl -i http://localhost:8082/api/claims/1
```

A matching claim returns `200 OK` and one claim response object.

### List claims

Use zero-based pagination:

```bash
curl -i 'http://localhost:8082/api/claims?page=0&size=20'
```

The response has this shape:

```json
{
  "content": [
    {
      "id": 1,
      "claimNumber": "CLM-BOOT-README-001",
      "policyNumber": "POL-BOOT-README-001",
      "claimantName": "Maria Santos",
      "incidentDate": "2023-12-15",
      "claimType": "AUTO",
      "claimedAmount": 1850.75,
      "status": "SUBMITTED",
      "description": "Rear bumper damage",
      "createdAt": "2026-09-21T12:49:29.344862"
    }
  ],
  "page": 0,
  "size": 20,
  "totalElements": 1,
  "totalPages": 1
}
```

If query parameters are omitted, `page=0` and `size=20` are used. Valid page
sizes are from 1 through 100.

### API errors

The API uses stable machine-readable error codes:

| HTTP status | Code | Situation |
|---|---|---|
| 404 | `CLAIM_NOT_FOUND` | The requested claim ID does not exist |
| 409 | `DUPLICATE_CLAIM_NUMBER` | The claim number already exists |
| 400 | `VALIDATION_FAILED` | Request-body validation failed |
| 400 | `INVALID_PAGE` | `page` is less than zero |
| 400 | `INVALID_PAGE_SIZE` | `size` is outside 1 through 100 |

Retrieve an unknown claim:

```bash
curl -i http://localhost:8082/api/claims/999999
```

Example response:

```json
{
  "status": 404,
  "code": "CLAIM_NOT_FOUND",
  "message": "Claim not found with id: 999999"
}
```

Test invalid pagination:

```bash
curl -i 'http://localhost:8082/api/claims?page=-1&size=20'
curl -i 'http://localhost:8082/api/claims?page=0&size=101'
```

Test request validation:

```bash
curl -i -X POST http://localhost:8082/api/claims \
  -H 'Content-Type: application/json' \
  -d '{}'
```

Validation failures include a `fieldErrors` object. The order of its fields is
not part of the API contract.

Database uniqueness violations are translated to HTTP 409 with
`DUPLICATE_CLAIM_NUMBER`. SQL text, PostgreSQL constraint names, and other
database implementation details are not returned to the client.

## Inspect PostgreSQL directly

Load `.env` into the current shell if necessary:

```bash
set -a
source .env
set +a
```

List the application and test databases:

```bash
docker compose exec postgres psql -U "$DB_USERNAME" -d postgres \
  -c "SELECT datname FROM pg_database WHERE datname IN ('insurance_claims_boot', 'insurance_claims_boot_test') ORDER BY datname;"
```

Inspect the application table definition:

```bash
docker compose exec postgres psql -U "$DB_USERNAME" \
  -d insurance_claims_boot -c '\d claims'
```

Inspect the test table definition:

```bash
docker compose exec postgres psql -U "$DB_USERNAME" \
  -d insurance_claims_boot_test -c '\d claims'
```

Query persisted application data directly:

```bash
docker compose exec postgres psql -U "$DB_USERNAME" \
  -d insurance_claims_boot \
  -c "SELECT id, claim_number, policy_number, incident_date, claim_type, claimed_amount, status, created_at FROM claims ORDER BY id;"
```

Rows created through `POST /api/claims` must appear in this result. This proves
that the HTTP application persisted data in PostgreSQL rather than holding it
only in application memory.

## PostgreSQL named volume

PostgreSQL stores its database files in the Compose named volume
`claims_boot_pg_data`. Docker gives it the project-qualified name
`insurance-claims-spring-boot_claims_boot_pg_data`.

Inspect its Docker-managed location and metadata:

```bash
docker volume inspect insurance-claims-spring-boot_claims_boot_pg_data
```

The volume survives container recreation and a normal `docker compose down`.

## Shut down

Stop and remove the application and PostgreSQL containers while preserving
the database volume:

```bash
docker compose down
```

To stop the services without removing their containers:

```bash
docker compose stop
```

Restart stopped services with:

```bash
docker compose start
```

## Permanently reset this project's database volume

> **Warning:** `docker compose down -v` permanently deletes this project's
> local PostgreSQL volume and all application and test data stored in it.
> This cannot be undone. Do not use it for a normal rebuild or restart.

When an intentionally empty database is required, first confirm that the
current directory is this project and that no data must be retained. Then run:

```bash
docker compose down -v
docker compose up --build -d
```

Because the PostgreSQL data directory is empty after the reset,
`database/00-init.sql` runs again and recreates both databases and both
baseline `claims` tables.

This command targets volumes declared by this Compose project. It should not
be run from another project directory.

## Verified baseline

The Docker-initialized baseline was manually verified:

- Maven ran with Java 17 and produced an executable Spring Boot JAR whose
  manifest reported `Build-Jdk-Spec: 17` and Spring Boot 3.2.1.
- The complete automated suite passed with 42 tests.
- Repository integration tests connected to the PostgreSQL test database.
- Hibernate validated the existing schema with `ddl-auto=validate`.
- The local executable application created, retrieved, and listed claims.
- The Dockerized Java 17 application created, retrieved, and paginated claims.
- Duplicate claim numbers returned HTTP 409.
- Unknown IDs returned HTTP 404.
- Invalid request bodies returned HTTP 400 with field errors.
- A direct SQL query displayed the row previously created through the HTTP API.
- Existing rows remained available after the application was moved from local
  execution to Docker Compose, proving that the named PostgreSQL volume was
  retained.

Docker initialization remains the schema owner for this application. Flyway
will instead be demonstrated in a separate Spring Boot sample application.

## Post-baseline schema evolution without Flyway

After the baseline was verified, two incremental SQL scripts extended the
schema while preserving existing data:

- `database/01-add-claim-status-history.sql`
  - Creates `claim_status_history` in the application and test databases.
  - Adds a foreign key to `claims(id)` with `ON DELETE CASCADE`.
  - Adds an index on `(claim_id, changed_at, id)`.
  - Backfills one initial history row for every existing claim.
- `database/02-add-claims-version.sql`
  - Adds the non-null `claims.version` column with an initial value of zero.
  - Supports JPA optimistic locking through `@Version`.

The original `database/00-init.sql` remains unchanged so the original baseline
is visible in the project history.

For a new empty PostgreSQL volume, the official PostgreSQL image executes the
mounted scripts in filename order:

```text
00-init.sql
01-add-claim-status-history.sql
02-add-claims-version.sql
```

Docker initialization scripts do not run again for an existing volume.
Therefore, an existing environment must apply each new script manually, once,
in release order.

Load the local environment variables:

```bash
set -a
source .env
set +a
```

Apply the history-table change:

```bash
docker compose exec -T postgres psql \
  -v ON_ERROR_STOP=1 \
  -U "$DB_USERNAME" \
  -d postgres \
  < database/01-add-claim-status-history.sql
```

Apply the optimistic-locking column:

```bash
docker compose exec -T postgres psql \
  -v ON_ERROR_STOP=1 \
  -U "$DB_USERNAME" \
  -d postgres \
  < database/02-add-claims-version.sql
```

These scripts are not idempotent and must not be rerun after they succeed.
Inspect the schema before applying them to an existing environment.

Verify the application schema:

```bash
docker compose exec postgres psql \
  -U "$DB_USERNAME" \
  -d insurance_claims_boot \
  -c '\d claims'

docker compose exec postgres psql \
  -U "$DB_USERNAME" \
  -d insurance_claims_boot \
  -c '\d claim_status_history'
```

Repeat the inspection against `insurance_claims_boot_test` when validating the
test schema.

No volume reset is required for these incremental changes. Preserving the
named volume preserves existing claims. Do not use `docker compose down -v`
to apply a schema change because it permanently deletes the project's local
database data.

This sample intentionally does not use Flyway. In a production deployment,
incremental scripts should normally be executed by a controlled database
migration process that records which versions have been applied. Flyway will
be demonstrated in a separate Spring Boot application.

## Post-baseline status endpoints

The post-baseline extension adds these endpoints without changing the original
claim response shapes:

- `PATCH /api/claims/{id}/status`
- `GET /api/claims/{id}/status-history`

### Update a claim status

The supported workflow is:

```text
SUBMITTED -> UNDER_REVIEW -> APPROVED
                         \-> REJECTED
```

`APPROVED` and `REJECTED` are terminal states. Repeating the current status or
skipping directly from `SUBMITTED` to a terminal state is rejected.

Move a submitted claim into review:

```bash
curl -i -X PATCH http://localhost:8082/api/claims/2/status \
  -H 'Content-Type: application/json' \
  -d '{"status":"UNDER_REVIEW"}'
```

A successful request returns `200 OK` using the existing `ClaimResponse`
shape. The internal optimistic-locking version is not exposed in the API.

Move the claim from review to approval:

```bash
curl -i -X PATCH http://localhost:8082/api/claims/2/status \
  -H 'Content-Type: application/json' \
  -d '{"status":"APPROVED"}'
```

### Retrieve claim status history

```bash
curl -i http://localhost:8082/api/claims/2/status-history
```

The response is ordered chronologically and joins each history record to its
claim information:

```json
[
  {
    "id": 2,
    "claimId": 2,
    "claimNumber": "CLM-BOOT-DOCKER-001",
    "status": "SUBMITTED",
    "changedAt": "2026-09-21T07:08:52.70415"
  },
  {
    "id": 3,
    "claimId": 2,
    "claimNumber": "CLM-BOOT-DOCKER-001",
    "status": "UNDER_REVIEW",
    "changedAt": "2026-09-23T08:28:40.635871"
  }
]
```

`changedAt` values are serialized as ISO-8601 strings.

### Additional API errors

The status extension adds these stable error codes:

| HTTP status | Code | Situation |
|---|---|---|
| 409 | `INVALID_STATUS_TRANSITION` | The requested transition violates the claim workflow |
| 409 | `CONCURRENT_CLAIM_UPDATE` | Another transaction updated the same claim first |

An invalid transition returns a response such as:

```json
{
  "status": 409,
  "code": "INVALID_STATUS_TRANSITION",
  "message": "Cannot transition claim status from APPROVED to REJECTED"
}
```

A missing status is handled by the existing validation contract:

```bash
curl -i -X PATCH http://localhost:8082/api/claims/2/status \
  -H 'Content-Type: application/json' \
  -d '{}'
```

```json
{
  "status": 400,
  "code": "VALIDATION_FAILED",
  "message": "Validation failed",
  "fieldErrors": {
    "status": "must not be null"
  }
}
```

An unknown enum value also returns `VALIDATION_FAILED` without exposing
Jackson deserialization details:

```bash
curl -i -X PATCH http://localhost:8082/api/claims/2/status \
  -H 'Content-Type: application/json' \
  -d '{"status":"NOT_A_STATUS"}'
```

### Transactions and optimistic locking

Updating a status and inserting its history row occur in one service
transaction. If the transition is invalid, neither the claim nor its history
is changed.

The `Claim.version` field uses JPA `@Version`. Hibernate includes the current
version in an update and increments it after a successful update. A request
using a stale version is rejected rather than silently overwriting a newer
change. Spring translates that persistence failure to
`ObjectOptimisticLockingFailureException`, which the API maps to HTTP 409 with
`CONCURRENT_CLAIM_UPDATE`.

Inspect the current claim and history state directly:

```bash
docker compose exec postgres psql \
  -U "$DB_USERNAME" \
  -d insurance_claims_boot \
  -c "SELECT id, claim_number, status, version
      FROM claims
      ORDER BY id;

      SELECT id, claim_id, status, changed_at
      FROM claim_status_history
      ORDER BY claim_id, changed_at, id;"
```

## Verified post-baseline extension

The status-history and status-update extension was verified as follows:

- The complete automated suite passed with 63 tests.
- Repository tests used the real PostgreSQL test database.
- A dedicated integration test loaded two copies of one claim in separate
  transactions and proved that PostgreSQL rejected the stale update.
- Docker Compose rebuilt and ran the executable application on Java 17.
- A claim moved from `SUBMITTED` to `UNDER_REVIEW` and then `APPROVED`.
- Its optimistic-locking version increased from zero to two.
- Each successful transition appended one history row.
- `GET /api/claims/{id}/status-history` returned all three history rows in
  chronological order.
- An invalid transition returned HTTP 409 and left the claim version and
  history count unchanged.
- Missing and unknown status values returned HTTP 400 with
  `VALIDATION_FAILED`.
- Direct SQL queries confirmed the final claim and history rows.
