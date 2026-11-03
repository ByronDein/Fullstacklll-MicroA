-- Script de base de datos del microservicio de USUARIOS (Oracle)
-- Ejecutar con el usuario "foro" (el que crea el docker-compose.yml)
-- OJO: si ya levantaste el microservicio, Hibernate crea las tablas solo (ddl-auto=update)
-- y el DataInitializer inserta estos mismos datos, asi que este script es para tenerlo documentado
-- o para crear la base a mano.

-- ==========================
-- TABLAS
-- ==========================
CREATE TABLE roles (
    id          NUMBER GENERATED AS IDENTITY PRIMARY KEY,
    nombre      VARCHAR2(255) NOT NULL,
    descripcion VARCHAR2(255),
    CONSTRAINT uk_roles_nombre UNIQUE (nombre)
);

CREATE TABLE usuarios (
    id             NUMBER GENERATED AS IDENTITY PRIMARY KEY,
    nombre         VARCHAR2(255) NOT NULL,
    correo         VARCHAR2(255) NOT NULL,
    clave          VARCHAR2(255) NOT NULL,
    activo         NUMBER(1) DEFAULT 1 NOT NULL,
    fecha_registro DATE,
    rol_id         NUMBER NOT NULL,
    CONSTRAINT uk_usuarios_correo UNIQUE (correo),
    CONSTRAINT fk_usuarios_rol FOREIGN KEY (rol_id) REFERENCES roles (id)
);

-- ==========================
-- DATOS DE PRUEBA
-- ==========================
INSERT INTO roles (nombre, descripcion) VALUES ('USUARIO', 'Puede ver, publicar y comentar temas en el foro');
INSERT INTO roles (nombre, descripcion) VALUES ('MODERADOR', 'Puede revisar y moderar publicaciones y comentarios');
INSERT INTO roles (nombre, descripcion) VALUES ('ADMIN', 'Administra usuarios y todo el foro');

-- Las claves estan encriptadas con BCrypt (admin123, mod123 y user123)
INSERT INTO usuarios (nombre, correo, clave, activo, fecha_registro, rol_id)
VALUES ('Byron Gonzalez', 'byron@foro.cl', '$2a$10$4of8oBM2rngzchC5rleSS.a1jE0JNR0lzXaDaSwDKCC2ymn5Gmsaq', 1, SYSDATE,
        (SELECT id FROM roles WHERE nombre = 'ADMIN'));

INSERT INTO usuarios (nombre, correo, clave, activo, fecha_registro, rol_id)
VALUES ('Camila Rojas', 'camila@foro.cl', '$2a$10$uPOzD2oa2Qxp3Tk.9pE2veIOD23CJLAlg6qiodx7Y3XkF0H9Wivb.', 1, SYSDATE,
        (SELECT id FROM roles WHERE nombre = 'MODERADOR'));

INSERT INTO usuarios (nombre, correo, clave, activo, fecha_registro, rol_id)
VALUES ('Matias Soto', 'matias@foro.cl', '$2a$10$6xtLQ5yQ57kN7F57Epc06uB3nmrbWqMWRLEjKbXriT.aC.e4OshhK', 1, SYSDATE,
        (SELECT id FROM roles WHERE nombre = 'USUARIO'));

COMMIT;

-- ==========================
-- CONSULTAS PARA REVISAR
-- ==========================
-- SELECT * FROM roles;
-- SELECT u.id, u.nombre, u.correo, r.nombre AS rol FROM usuarios u JOIN roles r ON u.rol_id = r.id;
