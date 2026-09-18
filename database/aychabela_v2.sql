USE aychabela;

ALTER TABLE usuarios ADD COLUMN dni       VARCHAR(15) NOT NULL DEFAULT '' AFTER id;
ALTER TABLE usuarios ADD COLUMN nombres   VARCHAR(60) NOT NULL DEFAULT '' AFTER dni;
ALTER TABLE usuarios ADD COLUMN apellidos VARCHAR(60) NOT NULL DEFAULT '' AFTER nombres;

UPDATE usuarios
SET nombres = TRIM(SUBSTRING_INDEX(nombre, ' ', 1)),
    apellidos = TRIM(SUBSTRING(nombre, LENGTH(SUBSTRING_INDEX(nombre, ' ', 1)) + 1))
WHERE nombres = '' OR nombres IS NULL;

UPDATE usuarios
SET dni = LPAD(id, 8, '0')
WHERE dni = '' OR dni IS NULL;

DELIMITER //

DROP PROCEDURE IF EXISTS LoginUsuario //
CREATE PROCEDURE LoginUsuario(
    IN p_usuario  VARCHAR(50),
    IN p_password VARCHAR(50)
)
BEGIN
    SELECT id, dni, nombres, apellidos, nombre, usuario, rol, activo
    FROM usuarios
    WHERE usuario = p_usuario
      AND password = p_password
      AND activo = 1;
END //

DROP PROCEDURE IF EXISTS ListarUsuarios //
CREATE PROCEDURE ListarUsuarios()
BEGIN
    SELECT id, dni, nombres, apellidos, nombre, usuario, password, rol, activo
    FROM usuarios
    ORDER BY id;
END //

DROP PROCEDURE IF EXISTS ObtenerUsuario //
CREATE PROCEDURE ObtenerUsuario(IN p_id INT)
BEGIN
    SELECT id, dni, nombres, apellidos, nombre, usuario, password, rol, activo
    FROM usuarios WHERE id = p_id;
END //

DROP PROCEDURE IF EXISTS InsertarUsuario //
CREATE PROCEDURE InsertarUsuario(
    IN p_dni       VARCHAR(15),
    IN p_nombres   VARCHAR(60),
    IN p_apellidos VARCHAR(60),
    IN p_usuario   VARCHAR(50),
    IN p_password  VARCHAR(50),
    IN p_rol       VARCHAR(20)
)
BEGIN
    INSERT INTO usuarios (dni, nombres, apellidos, nombre, usuario, password, rol, activo)
    VALUES (p_dni, p_nombres, p_apellidos,
            CONCAT(p_nombres, ' ', p_apellidos),
            p_usuario, p_password, p_rol, 1);
END //

DROP PROCEDURE IF EXISTS ModificarUsuario //
CREATE PROCEDURE ModificarUsuario(
    IN p_id        INT,
    IN p_dni       VARCHAR(15),
    IN p_nombres   VARCHAR(60),
    IN p_apellidos VARCHAR(60),
    IN p_usuario   VARCHAR(50),
    IN p_password  VARCHAR(50),
    IN p_rol       VARCHAR(20),
    IN p_activo    TINYINT
)
BEGIN
    UPDATE usuarios
    SET dni = p_dni,
        nombres = p_nombres,
        apellidos = p_apellidos,
        nombre = CONCAT(p_nombres, ' ', p_apellidos),
        usuario = p_usuario,
        password = p_password,
        rol = p_rol,
        activo = p_activo
    WHERE id = p_id;
END //

DELIMITER ;

