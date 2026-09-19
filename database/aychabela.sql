CREATE DATABASE aychabela CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci;
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

-- Tabla: mesas
CREATE TABLE mesas (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    numero      INT NOT NULL UNIQUE,
    capacidad   INT NOT NULL,
    estado      VARCHAR(20) NOT NULL DEFAULT 'LIBRE',
    ubicacion   VARCHAR(50)
);

-- Tabla: pedidos
CREATE TABLE pedidos (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    fecha_hora  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    mesa_id     INT NOT NULL,
    usuario_id  INT NOT NULL,
    estado      VARCHAR(20)   NOT NULL DEFAULT 'PENDIENTE',
    total       DECIMAL(10,2) NOT NULL DEFAULT 0,
    notas       VARCHAR(255),
    CONSTRAINT fk_pedidos_mesa    FOREIGN KEY (mesa_id)    REFERENCES mesas(id),
    CONSTRAINT fk_pedidos_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

-- Tabla: detalle_pedido
CREATE TABLE detalle_pedido (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    pedido_id       INT NOT NULL,
    plato_id        INT NOT NULL,
    cantidad        INT NOT NULL,
    precio_unitario DECIMAL(10,2) NOT NULL,
    subtotal        DECIMAL(10,2) NOT NULL,
    CONSTRAINT fk_detalle_pedido FOREIGN KEY (pedido_id) REFERENCES pedidos(id) ON DELETE CASCADE,
    CONSTRAINT fk_detalle_plato  FOREIGN KEY (plato_id)  REFERENCES platos(id)
);

-- DATOS INICIALES
-- Usuarios
INSERT INTO usuarios (nombre, usuario, password, rol) VALUES
('Administrador', 'admin',  'admin123',  'ADMIN'),
('Juan Perez',    'mesero', 'mesero123', 'MESERO'),
('Maria Garcia',  'cocina', 'cocina123', 'COCINA');

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

-- Mesas
INSERT INTO mesas (numero, capacidad, ubicacion) VALUES
(1, 4, 'Salon principal'),
(2, 2, 'Salon principal'),
(3, 4, 'Salon principal'),
(4, 4, 'Salon principal'),
(5, 2, 'Terraza'),
(6, 6, 'Terraza'),
(7, 4, 'Terraza'),
(8, 4, 'Salon principal');


DELIMITER //

-- ---------- LOGIN ------------------------------------------
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

-- ---------- PLATOS -----------------------------------------
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

-- ---------- MESAS ------------------------------------------
DROP PROCEDURE IF EXISTS ListarMesas //
CREATE PROCEDURE ListarMesas()
BEGIN
    SELECT id, numero, capacidad, estado, ubicacion
    FROM mesas
    ORDER BY numero;
END //

DROP PROCEDURE IF EXISTS CambiarEstadoMesa //
CREATE PROCEDURE CambiarEstadoMesa(
    IN p_id     INT,
    IN p_estado VARCHAR(20)
)
BEGIN
    UPDATE mesas SET estado = p_estado WHERE id = p_id;
END //

-- ---------- PEDIDOS -------------------------------
DROP PROCEDURE IF EXISTS RegistrarPedido //
CREATE PROCEDURE RegistrarPedido(
    IN  p_mesa_id    INT,
    IN  p_usuario_id INT,
    IN  p_notas      VARCHAR(255),
    OUT p_pedido_id  INT
)
BEGIN
    INSERT INTO pedidos (mesa_id, usuario_id, estado, total, notas)
    VALUES (p_mesa_id, p_usuario_id, 'PENDIENTE', 0, p_notas);
    SET p_pedido_id = LAST_INSERT_ID();
    UPDATE mesas SET estado = 'OCUPADA' WHERE id = p_mesa_id;
END //

DROP PROCEDURE IF EXISTS AgregarDetallePedido //
CREATE PROCEDURE AgregarDetallePedido(
    IN p_pedido_id INT,
    IN p_plato_id  INT,
    IN p_cantidad  INT
)
BEGIN
    DECLARE v_precio DECIMAL(10,2);
    SELECT precio INTO v_precio FROM platos WHERE id = p_plato_id;
    INSERT INTO detalle_pedido (pedido_id, plato_id, cantidad, precio_unitario, subtotal)
    VALUES (p_pedido_id, p_plato_id, p_cantidad, v_precio, v_precio * p_cantidad);
    UPDATE pedidos
    SET total = (SELECT IFNULL(SUM(subtotal),0) FROM detalle_pedido WHERE pedido_id = p_pedido_id)
    WHERE id = p_pedido_id;
END //

DROP PROCEDURE IF EXISTS ListarPedidos //
CREATE PROCEDURE ListarPedidos()
BEGIN
    SELECT p.id, p.fecha_hora, p.estado, p.total, p.notas,
           m.numero    AS mesa_numero,
           u.nombre    AS usuario_nombre
    FROM pedidos p
    INNER JOIN mesas m    ON p.mesa_id    = m.id
    INNER JOIN usuarios u ON p.usuario_id = u.id
    ORDER BY p.fecha_hora DESC;
END //

DROP PROCEDURE IF EXISTS ObtenerPedido //
CREATE PROCEDURE ObtenerPedido(IN p_id INT)
BEGIN
    SELECT p.id, p.fecha_hora, p.estado, p.total, p.notas,
           p.mesa_id, m.numero AS mesa_numero,
           p.usuario_id, u.nombre AS usuario_nombre
    FROM pedidos p
    INNER JOIN mesas m    ON p.mesa_id    = m.id
    INNER JOIN usuarios u ON p.usuario_id = u.id
    WHERE p.id = p_id;
END //

DROP PROCEDURE IF EXISTS ListarDetallePedido //
CREATE PROCEDURE ListarDetallePedido(IN p_pedido_id INT)
BEGIN
    SELECT d.id, d.cantidad, d.precio_unitario, d.subtotal,
           p.id AS plato_id, p.nombre AS plato_nombre
    FROM detalle_pedido d
    INNER JOIN platos p ON d.plato_id = p.id
    WHERE d.pedido_id = p_pedido_id;
END //

DROP PROCEDURE IF EXISTS CambiarEstadoPedido //
CREATE PROCEDURE CambiarEstadoPedido(
    IN p_id     INT,
    IN p_estado VARCHAR(20)
)
BEGIN
    UPDATE pedidos SET estado = p_estado WHERE id = p_id;
END //

-- ---------- USUARIOS ---------------------------------------
DROP PROCEDURE IF EXISTS ListarUsuarios //
CREATE PROCEDURE ListarUsuarios()
BEGIN
    SELECT id, nombre, usuario, password, rol, activo
    FROM usuarios
    ORDER BY id;
END //

DROP PROCEDURE IF EXISTS InsertarUsuario //
CREATE PROCEDURE InsertarUsuario(
    IN p_nombre   VARCHAR(100),
    IN p_usuario  VARCHAR(50),
    IN p_password VARCHAR(50),
    IN p_rol      VARCHAR(20)
)
BEGIN
    INSERT INTO usuarios (nombre, usuario, password, rol, activo)
    VALUES (p_nombre, p_usuario, p_password, p_rol, 1);
END //

DROP PROCEDURE IF EXISTS ModificarUsuario //
CREATE PROCEDURE ModificarUsuario(
    IN p_id       INT,
    IN p_nombre   VARCHAR(100),
    IN p_usuario  VARCHAR(50),
    IN p_password VARCHAR(50),
    IN p_rol      VARCHAR(20),
    IN p_activo   TINYINT
)
BEGIN
    UPDATE usuarios
    SET nombre = p_nombre,
        usuario = p_usuario,
        password = p_password,
        rol = p_rol,
        activo = p_activo
    WHERE id = p_id;
END //

DROP PROCEDURE IF EXISTS EliminarUsuario //
CREATE PROCEDURE EliminarUsuario(IN p_id INT)
BEGIN
    DELETE FROM usuarios WHERE id = p_id;
END //

DROP PROCEDURE IF EXISTS ObtenerUsuario //
CREATE PROCEDURE ObtenerUsuario(IN p_id INT)
BEGIN
    SELECT id, nombre, usuario, password, rol, activo
    FROM usuarios WHERE id = p_id;
END //

DELIMITER ;

