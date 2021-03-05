# Application Setup

### Requirements

- [PostgreSQL v13](https://www.postgresql.org/download/)
- [JDK v11](https://adoptopenjdk.net/releases.html)
- [Gradle v6.4.1](https://gradle.org/releases/)
- [Git](https://git-scm.com/downloads)

### Clone repository

`$ git clone "https://github.com/Bloxico/ase-be-services"`

### Navigate to root

`cd ase-be-services/`

### Get the latest version

`$ git checkout develop`

### Update credentials

- [Test properties](../src/test/resources/application-test.properties)
    - spring.datasource.url
    - spring.datasource.username
    - spring.datasource.password

- [Local properties](../src/main/resources/application-local.properties)
    - spring.datasource.url
    - spring.datasource.username
    - spring.datasource.password

### Build

`gradle clean build -x test`

### Start

`gradle clean bootRun`

### Interact

- [Application root path](http://localhost:8089/api)
- [Swagger](http://localhost:8089/api/swagger-ui.html)
- [Postman collection](../src/main/resources/postman/Art%20Stock%20Exchange.postman_collection.json)

---

[<: Home](../README.md) | [Postman Setup :>](postman_setup.md)