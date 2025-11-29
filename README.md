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
- Este endpoint retornara un Token, el cual se debe autorizar en swagger para utilizar los otros endpoint.

NOTA: De acuerdo a las expresiones regulares configuradas en el properties el correo acepta el siguiente formato:
- Debe contener obligatoriamente el @
  Antes del @ permite letras, números, puntos, guión, guion bajo
  Debe contener un punto antes de la extensión "cl , com, etc"
  Extension del dominio minimo 2 letras.

Ejemplo:
- user@mail.com
  juan.perez@gmail.cl
  test_123@empresa.org

El password:
- Debe tener mínimo 6 caracteres
  Debe contener al menos un número
  Debe contener al menos una letra (mayúscula o minúscula)
  Acepta cualquier combinación de caracteres
  Fin de la cadena
Ejemplo:
- abc123
  Hola2024
  Pass1
