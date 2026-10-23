

-- APF2 FASE 1 - Gestion de Mesas
-- Ejecutar sobre la base de datos aychabela.
-- Contiene la estructura y los datos iniciales del modulo.

USE aychabela;

-- Tabla de mesas
CREATE TABLE IF NOT EXISTS mesas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    numero INT NOT NULL UNIQUE,
    capacidad INT NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'LIBRE',
    ubicacion VARCHAR(50) NOT NULL
);

-- Ocho mesas iniciales
INSERT IGNORE INTO mesas (numero, capacidad, ubicacion) VALUES
(1, 4, 'Salon principal'),
(2, 2, 'Salon principal'),
(3, 4, 'Salon principal'),
(4, 4, 'Salon principal'),
(5, 2, 'Terraza'),
(6, 6, 'Terraza'),
(7, 4, 'Terraza'),
(8, 4, 'Salon principal');
DELIMITER //

-- Listar todas las mesas
DROP PROCEDURE IF EXISTS ListarMesas //
CREATE PROCEDURE ListarMesas()
BEGIN
    SELECT id, numero, capacidad, estado, ubicacion
    FROM mesas
    ORDER BY numero;
END //

-- Cambiar el estado de una mesa
DROP PROCEDURE IF EXISTS CambiarEstadoMesa //
CREATE PROCEDURE CambiarEstadoMesa(
    IN p_id INT,
    IN p_estado VARCHAR(20)
)
BEGIN
    UPDATE mesas
    SET estado = p_estado
    WHERE id = p_id;
END //

DELIMITER ;

