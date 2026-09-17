-- =============================================================
-- 02_platos.sql
-- Tablas de categorias y platos, datos iniciales y procedimientos.
-- =============================================================
USE aychabela;

-- Tabla: categorias
CREATE TABLE categorias (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    nombre      VARCHAR(50)  NOT NULL UNIQUE,
    descripcion VARCHAR(200)
);

-- Tabla: platos
CREATE TABLE platos (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    nombre       VARCHAR(100)   NOT NULL,
    descripcion  VARCHAR(255),
    precio       DECIMAL(10,2) NOT NULL,
    disponible   TINYINT(1)    NOT NULL DEFAULT 1,
    categoria_id INT NOT NULL,
    CONSTRAINT fk_platos_categoria FOREIGN KEY (categoria_id) REFERENCES categorias(id)
);

-- Categorias
INSERT INTO categorias (nombre, descripcion) VALUES
('Entradas',           'Platos de entrada'),
('Platos Principales', 'Platos de fondo'),
('Bebidas',            'Bebidas frias y calientes'),
('Postres',            'Postres y dulces');

-- Platos
INSERT INTO platos (nombre, descripcion, precio, categoria_id) VALUES
('Ceviche Mixto',          'Ceviche con mariscos variados',          28.00, 1),
('Causa Limena',           'Causa con atun y palta',                 18.00, 1),
('Tequenos',               'Tequenos de queso (6 unidades)',         15.00, 1),
('Lomo Saltado',           'Lomo saltado con papas fritas y arroz',  32.00, 2),
('Aji de Gallina',         'Aji de gallina con arroz y papa',        25.00, 2),
('Anticuchos',             'Anticuchos de corazon con papa y choclo',20.00, 2),
('Pollo a la Brasa (1/4)', 'Cuarto de pollo a la brasa con ensalada',24.00, 2),
('Chicha Morada',          'Chicha morada artesanal (jarra)',        12.00, 3),
('Inca Kola',              'Gaseosa Inca Kola 620ml',                 6.00, 3),
('Agua Mineral',           'Agua mineral 625ml',                      4.00, 3),
('Maracuya Refresco',      'Refresco de maracuya natural',            8.00, 3),
('Arroz con Leche',        'Arroz con leche con canela',              8.00, 4),
('Mazamorra Morada',       'Mazamorra morada con leche',              7.00, 4);

DELIMITER //

DROP PROCEDURE IF EXISTS ListarPlatos //
CREATE PROCEDURE ListarPlatos()
BEGIN
    SELECT p.id, p.nombre, p.descripcion, p.precio, p.disponible,
           p.categoria_id, c.nombre AS categoria_nombre
    FROM platos p
    INNER JOIN categorias c ON p.categoria_id = c.id
    ORDER BY c.id, p.nombre;
END //

DROP PROCEDURE IF EXISTS ListarPlatosDisponibles //
CREATE PROCEDURE ListarPlatosDisponibles()
BEGIN
    SELECT p.id, p.nombre, p.descripcion, p.precio, p.disponible,
           p.categoria_id, c.nombre AS categoria_nombre
    FROM platos p
    INNER JOIN categorias c ON p.categoria_id = c.id
    WHERE p.disponible = 1
    ORDER BY c.id, p.nombre;
END //

DROP PROCEDURE IF EXISTS InsertarPlato //
CREATE PROCEDURE InsertarPlato(
    IN p_nombre       VARCHAR(100),
    IN p_descripcion  VARCHAR(255),
    IN p_precio       DECIMAL(10,2),
    IN p_categoria_id INT,
    IN p_disponible   TINYINT
)
BEGIN
    INSERT INTO platos (nombre, descripcion, precio, categoria_id, disponible)
    VALUES (p_nombre, p_descripcion, p_precio, p_categoria_id, p_disponible);
END //

DROP PROCEDURE IF EXISTS ModificarPlato //
CREATE PROCEDURE ModificarPlato(
    IN p_id           INT,
    IN p_nombre       VARCHAR(100),
    IN p_descripcion  VARCHAR(255),
    IN p_precio       DECIMAL(10,2),
    IN p_categoria_id INT,
    IN p_disponible   TINYINT
)
BEGIN
    UPDATE platos
    SET nombre = p_nombre,
        descripcion = p_descripcion,
        precio = p_precio,
        categoria_id = p_categoria_id,
        disponible = p_disponible
    WHERE id = p_id;
END //

DROP PROCEDURE IF EXISTS EliminarPlato //
CREATE PROCEDURE EliminarPlato(IN p_id INT)
BEGIN
    DELETE FROM platos WHERE id = p_id;
END //

DROP PROCEDURE IF EXISTS ObtenerPlato //
CREATE PROCEDURE ObtenerPlato(IN p_id INT)
BEGIN
    SELECT p.id, p.nombre, p.descripcion, p.precio, p.disponible,
           p.categoria_id, c.nombre AS categoria_nombre
    FROM platos p
    INNER JOIN categorias c ON p.categoria_id = c.id
    WHERE p.id = p_id;
END //

DROP PROCEDURE IF EXISTS ListarCategorias //
CREATE PROCEDURE ListarCategorias()
BEGIN
    SELECT id, nombre, descripcion FROM categorias ORDER BY id;
END //

DELIMITER ;
