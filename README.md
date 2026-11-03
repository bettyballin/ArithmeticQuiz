# ArithmeticQuiz

This is a Spring Boot application that performs mathematical operations using dynamic business rules.

The goal is to allow users to define rules that change how calculations work, without having to modify the Java code.

## Technologies

- Java
- Spring Boot
- Gradle
- PostgreSQL
- Spring Data JPA
- REST API

## Mathematical Operations

The application supports:

- Addition
- Subtraction
- Multiplication
- Division

## Business Rules

Each rule consists of a trigger and an action.

**Trigger:** Defines when a rule should be executed.
**Action:** Defines what should happen when the trigger matches.

Examples:
- If the first operand of an addition is 5, add 1 to the result.
- If the second operand of a multiplication is greater than 10, multiply the first operand by 2.
- If the result is negative, subtract 5 from it.

Rules are stored in PostgreSQL as JSONB and can be managed through the REST API.

## REST Endpoints

### Mathematical Operations

POST:
`/api/math/ADD`
`/api/math/SUBTRACT`
`/api/math/MULTIPLY`
`/api/math/DIVIDE`

### Business Rules

| Method | Endpoint | Description |
| POST | `/api/rules` | Create a rule |
| GET | `/api/rules` | Get all rules |
| GET | `/api/rules/{id}` | Get a rule by ID |
| PUT | `/api/rules/{id}` | Update a rule |
| DELETE | `/api/rules/{id}` | Delete a rule |

## How to Run

1. Clone the repository:

```bash
git clone https://github.com/bettyballin/ArithmeticQuiz.git
```

2. Open the project:

```bash
cd ArithmeticQuiz
```

3. Configure PostgreSQL in `src/main/resources/application.properties`.

4. Start the application:

```bash
./gradlew bootRun
```

The application runs on:

`http://localhost:8080`

## Examples

Calculate 5 + 10:

```bash
curl -X POST http://localhost:8080/api/math/ADD \
-H "Content-Type: application/json" \
-d '{"a":5,"b":10}'
```

Add a new rule: If the first operand of an addition is 5, add 1 to the result

```bash
curl -X POST http://localhost:8080/api/rules \
-H "Content-Type: application/json" \
-d '{
  "name": "Addition rule",
  "active": true,
  "trigger": {
    "value": 5,
    "target": "OPERAND1",
    "operator": "EQUALS",
    "operation": "ADD"
  },
  "action": {
    "value": 1,
    "target": "RESULT",
    "operator": "ADD"
  }
}'
```