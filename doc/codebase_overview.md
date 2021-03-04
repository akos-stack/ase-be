# Codebase Overview

### stack

- Amazon AWS (EC2, S3)
- PostgreSQL
- Java 11
- Gradle
- Spring(Boot)
- JPA + Hibernate
- Spring Data JPA
- Lombok
- Mapstruct
- JUnit
- Hamcrest Matchers
- Rest-Assured
- Quartz
- Liquidbase
- Swagger
- Postman

### src/main

#### security

- OAuth2
- JwtAuthorizationFilter
- Social login
    - Google
    - Facebook
    - Instagram
- @PreAuthorize

#### structure (bottom-up)

- entity
- repository
- dto + proj
- service
    - injects n repositories / utils
    - no deps on other services
    - one method one action
- facade
    - injects n services / utils
    - combines service actions into atomic flow
- api / controller
    - injects 1 facade
    - delegates to facade
    - creates response (if not returned by facade)
    - swagger docs
    - permission check

#### communication

| layer      | accepts     | calls               | returns                   |
|------------|-------------|---------------------|---------------------------|
| controller | request     | 1 facade            | response                  |
| facade     | request     | n service    / util | response / void           |
| service    | dto / value | n repository / util | dto / proj / value / void |

### src/test

#### layers to test

- required
    - controller
    - facade
    - service
- desirable
    - config
    - util

#### cases to test

- edge cases
- fail cases
- success cases
- all response flows documented in swagger

#### how to test

- avoid hardcoded values
- generate unique data for tests (genEmail, genPassword, genUUID)
- avoid checking collection / table / cache / directory size in assertions
- in assertions, test only data that is inserted / changed inside current test case

---

[<: Application Setup](application_setup.md) |