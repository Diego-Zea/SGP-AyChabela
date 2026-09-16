-- =============================================================
-- 01_usuarios_login.sql
-- Crea la base de datos, la tabla de usuarios y el login.
-- =============================================================
CREATE DATABASE IF NOT EXISTS aychabela CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci;
USE aychabela;

-- Tabla: usuarios
CREATE TABLE usuarios (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    nombre      VARCHAR(100) NOT NULL,
    usuario     VARCHAR(50)  NOT NULL UNIQUE,
    password    VARCHAR(50)  NOT NULL,
    rol         VARCHAR(20)  NOT NULL DEFAULT 'MESERO',
    activo      TINYINT(1)   NOT NULL DEFAULT 1
);

-- Usuarios de prueba
INSERT INTO usuarios (nombre, usuario, password, rol) VALUES
('Administrador', 'admin',  'admin123',  'ADMIN'),
('Juan Perez',    'mesero', 'mesero123', 'MESERO'),
('Maria Garcia',  'cocina', 'cocina123', 'COCINA');

DELIMITER //

DROP PROCEDURE IF EXISTS LoginUsuario //
CREATE PROCEDURE LoginUsuario(
    IN p_usuario  VARCHAR(50),
    IN p_password VARCHAR(50)
)
BEGIN
    SELECT id, nombre, usuario, rol, activo
    FROM usuarios
    WHERE usuario = p_usuario
      AND password = p_password
      AND activo = 1;
END //

DELIMITER ;
