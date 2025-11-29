# User API - Spring Boot (Java 17) - HSQLDB - OpenJPA - JWT - Maven 

## Run
mvn clean install
mvn spring-boot:run

## Endpoints
POST /user/register - register
GET  /users        - list
GET  /user/{id}   - get by id
PUT  /user/{id}   - update
DELETE /user/{id} - delete

Probar a traves de Swagger
Swagger UI: http://localhost:8080/swagger-ui.html

- Registrar un usuario con este Body por ejemplo
{
  "name": "Juan Rodriguez",
  "email": "juan@rodriguez.org",
  "password": "Hunter2a",
  "phones": [
    {
      "number": "1234567",
      "cityCode": "1",
      "countryCode": "57"
    }
  ]
}

Este endpoint retornara un Token, el cual se debe autorizar en swagger para utilizar los otros endpoint.
