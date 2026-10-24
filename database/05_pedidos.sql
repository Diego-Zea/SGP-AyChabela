-- =============================================================
-- 05_pedidos.sql   (APF2)
-- Tablas de pedidos y su detalle, con los procedimientos del modulo.
-- Ejecutar despues de 04_mesas.sql
-- =============================================================
USE aychabela;

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

DELIMITER //

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

DELIMITER ;
