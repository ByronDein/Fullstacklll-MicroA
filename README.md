# usuarios

Microservicio generado con el arquetipo **foro-microservicio-archetype** (Spring Boot 3 + Oracle).

**Estructura (capas)**

- `controller`: endpoints REST (GET, POST, PUT, DELETE) que devuelven JSON
- `service`: interfaz y su implementacion con la logica
- `repository`: acceso a Oracle con Spring Data JPA
- `model`: entidades (tablas)
- `exception`: manejo de errores (400, 404) en formato JSON

**Como ejecutarlo**

1. Levantar Oracle (por ejemplo con el docker-compose del proyecto principal).
2. Ejecutar: `./mvnw spring-boot:run` (puerto 8081).
3. Probar en Postman: `GET http://localhost:8081/ejemplos`

La entidad `Ejemplo` es solo una muestra: se renombra o se copia para crear las entidades reales del microservicio.

**Oracle Cloud:** definir TNS_ADMIN, DB_SERVICE, DB_USER y DB_PASSWORD y ejecutar con el perfil `cloud`
(`-Dspring-boot.run.profiles=cloud`).
