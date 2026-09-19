USE aychabela;

CREATE TABLE IF NOT EXISTS insumos (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    nombre       VARCHAR(80)    NOT NULL,
    unidad       VARCHAR(10)    NOT NULL,             
    stock        DECIMAL(10,3)  NOT NULL DEFAULT 0,
    stock_minimo DECIMAL(10,3)  NOT NULL DEFAULT 0,
    activo       TINYINT(1)     NOT NULL DEFAULT 1,
    UNIQUE KEY uk_insumo_nombre (nombre)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS plato_insumo (
    id        INT AUTO_INCREMENT PRIMARY KEY,
    plato_id  INT           NOT NULL,
    insumo_id INT           NOT NULL,
    cantidad  DECIMAL(10,3) NOT NULL,               
    UNIQUE KEY uk_plato_insumo (plato_id, insumo_id),
    CONSTRAINT fk_receta_plato  FOREIGN KEY (plato_id)  REFERENCES platos(id)  ON DELETE CASCADE,
    CONSTRAINT fk_receta_insumo FOREIGN KEY (insumo_id) REFERENCES insumos(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS movimiento_insumo (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    insumo_id   INT           NOT NULL,
    pedido_id   INT           NULL,                    
    tipo        VARCHAR(10)   NOT NULL,                
    cantidad    DECIMAL(10,3) NOT NULL,
    fecha_hora  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    observacion VARCHAR(150),
    CONSTRAINT fk_mov_insumo FOREIGN KEY (insumo_id) REFERENCES insumos(id),
    CONSTRAINT fk_mov_pedido FOREIGN KEY (pedido_id) REFERENCES pedidos(id),
    INDEX idx_mov_fecha (fecha_hora),
    INDEX idx_mov_tipo  (tipo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DELIMITER //

DROP PROCEDURE IF EXISTS ListarInsumos //
CREATE PROCEDURE ListarInsumos()
BEGIN
    SELECT id, nombre, unidad, stock, stock_minimo, activo
    FROM insumos
    ORDER BY nombre;
END //

DROP PROCEDURE IF EXISTS ListarInsumosActivos //
CREATE PROCEDURE ListarInsumosActivos()
BEGIN
    SELECT id, nombre, unidad, stock, stock_minimo, activo
    FROM insumos
    WHERE activo = 1
    ORDER BY nombre;
END //

DROP PROCEDURE IF EXISTS ObtenerInsumo //
CREATE PROCEDURE ObtenerInsumo(IN p_id INT)
BEGIN
    SELECT id, nombre, unidad, stock, stock_minimo, activo
    FROM insumos WHERE id = p_id;
END //

DROP PROCEDURE IF EXISTS InsertarInsumo //
CREATE PROCEDURE InsertarInsumo(
    IN p_nombre       VARCHAR(80),
    IN p_unidad       VARCHAR(10),
    IN p_stock        DECIMAL(10,3),
    IN p_stock_minimo DECIMAL(10,3)
)
BEGIN
    INSERT INTO insumos (nombre, unidad, stock, stock_minimo, activo)
    VALUES (p_nombre, p_unidad, p_stock, p_stock_minimo, 1);

    IF p_stock > 0 THEN
        INSERT INTO movimiento_insumo (insumo_id, pedido_id, tipo, cantidad, observacion)
        VALUES (LAST_INSERT_ID(), NULL, 'ENTRADA', p_stock, 'Stock inicial');
    END IF;
END //

DROP PROCEDURE IF EXISTS ModificarInsumo //
CREATE PROCEDURE ModificarInsumo(
    IN p_id           INT,
    IN p_nombre       VARCHAR(80),
    IN p_unidad       VARCHAR(10),
    IN p_stock        DECIMAL(10,3),
    IN p_stock_minimo DECIMAL(10,3),
    IN p_activo       TINYINT
)
BEGIN
    DECLARE v_stock_ant DECIMAL(10,3);
    SELECT stock INTO v_stock_ant FROM insumos WHERE id = p_id;

    UPDATE insumos
    SET nombre       = p_nombre,
        unidad       = p_unidad,
        stock        = p_stock,
        stock_minimo = p_stock_minimo,
        activo       = p_activo
    WHERE id = p_id;

    IF v_stock_ant <> p_stock THEN
        INSERT INTO movimiento_insumo (insumo_id, pedido_id, tipo, cantidad, observacion)
        VALUES (p_id, NULL, 'AJUSTE', ABS(p_stock - v_stock_ant),
                CONCAT('Ajuste manual: ', v_stock_ant, ' -> ', p_stock));
    END IF;
END //

DROP PROCEDURE IF EXISTS EliminarInsumo //
CREATE PROCEDURE EliminarInsumo(IN p_id INT)
BEGIN
    UPDATE insumos SET activo = 0 WHERE id = p_id;
END //

DROP PROCEDURE IF EXISTS RegistrarEntradaInsumo //
CREATE PROCEDURE RegistrarEntradaInsumo(
    IN p_id       INT,
    IN p_cantidad DECIMAL(10,3),
    IN p_obs      VARCHAR(150)
)
BEGIN
    UPDATE insumos SET stock = stock + p_cantidad WHERE id = p_id;

    INSERT INTO movimiento_insumo (insumo_id, pedido_id, tipo, cantidad, observacion)
    VALUES (p_id, NULL, 'ENTRADA', p_cantidad, IFNULL(p_obs, 'Compra'));
END //

DROP PROCEDURE IF EXISTS ListarRecetaPlato //
CREATE PROCEDURE ListarRecetaPlato(IN p_plato_id INT)
BEGIN
    SELECT pi.id, pi.plato_id, pi.insumo_id, pi.cantidad,
           i.nombre AS insumo_nombre, i.unidad, i.stock
    FROM plato_insumo pi
    JOIN insumos i ON i.id = pi.insumo_id
    WHERE pi.plato_id = p_plato_id
    ORDER BY i.nombre;
END //

DROP PROCEDURE IF EXISTS AgregarInsumoAPlato //
CREATE PROCEDURE AgregarInsumoAPlato(
    IN p_plato_id  INT,
    IN p_insumo_id INT,
    IN p_cantidad  DECIMAL(10,3)
)
BEGIN
    INSERT INTO plato_insumo (plato_id, insumo_id, cantidad)
    VALUES (p_plato_id, p_insumo_id, p_cantidad)
    ON DUPLICATE KEY UPDATE cantidad = p_cantidad;
END //

DROP PROCEDURE IF EXISTS EliminarInsumoDePlato //
CREATE PROCEDURE EliminarInsumoDePlato(IN p_id INT)
BEGIN
    DELETE FROM plato_insumo WHERE id = p_id;
END //

DROP PROCEDURE IF EXISTS DescontarInsumosPedido //
CREATE PROCEDURE DescontarInsumosPedido(IN p_pedido_id INT)
BEGIN
    DECLARE v_yaDescontado INT DEFAULT 0;

    SELECT COUNT(*) INTO v_yaDescontado
    FROM movimiento_insumo
    WHERE pedido_id = p_pedido_id AND tipo = 'SALIDA';

    IF v_yaDescontado = 0 THEN

        INSERT INTO movimiento_insumo (insumo_id, pedido_id, tipo, cantidad, observacion)
        SELECT pi.insumo_id,
               p_pedido_id,
               'SALIDA',
               SUM(d.cantidad * pi.cantidad),
               CONCAT('Consumo automatico del pedido #', p_pedido_id)
        FROM detalle_pedido d
        JOIN plato_insumo pi ON pi.plato_id = d.plato_id
        WHERE d.pedido_id = p_pedido_id
        GROUP BY pi.insumo_id;

        UPDATE insumos i
        JOIN (
            SELECT pi.insumo_id, SUM(d.cantidad * pi.cantidad) AS consumo
            FROM detalle_pedido d
            JOIN plato_insumo pi ON pi.plato_id = d.plato_id
            WHERE d.pedido_id = p_pedido_id
            GROUP BY pi.insumo_id
        ) x ON x.insumo_id = i.id
        SET i.stock = i.stock - x.consumo;

    END IF;
END //

DROP PROCEDURE IF EXISTS CambiarEstadoPedido //
CREATE PROCEDURE CambiarEstadoPedido(
    IN p_id     INT,
    IN p_estado VARCHAR(20)
)
BEGIN
    UPDATE pedidos SET estado = p_estado WHERE id = p_id;

    IF p_estado = 'EN_PREPARACION' THEN
        CALL DescontarInsumosPedido(p_id);
    END IF;

    IF p_estado IN ('ENTREGADO', 'CERRADO', 'CANCELADO') THEN
        UPDATE mesas m
        SET m.estado = 'LIBRE'
        WHERE m.id = (SELECT mesa_id FROM pedidos WHERE id = p_id)
          AND NOT EXISTS (
              SELECT 1 FROM pedidos p2
              WHERE p2.mesa_id = m.id
                AND p2.estado IN ('PENDIENTE', 'EN_PREPARACION', 'LISTO')
          );
    END IF;
END //

DROP PROCEDURE IF EXISTS ListarPedidosCocina //
CREATE PROCEDURE ListarPedidosCocina()
BEGIN
    SELECT p.id,
           p.fecha_hora,
           p.estado,
           p.total,
           p.notas,
           m.numero AS mesa_numero,
           u.nombre AS usuario_nombre,
           TIMESTAMPDIFF(MINUTE, p.fecha_hora, NOW()) AS minutos
    FROM pedidos p
    JOIN mesas    m ON m.id = p.mesa_id
    JOIN usuarios u ON u.id = p.usuario_id
    WHERE p.estado IN ('PENDIENTE', 'EN_PREPARACION', 'LISTO')
    ORDER BY FIELD(p.estado, 'PENDIENTE', 'EN_PREPARACION', 'LISTO'), p.fecha_hora;
END //

DROP PROCEDURE IF EXISTS ListarDetallePedidosCocina //
CREATE PROCEDURE ListarDetallePedidosCocina()
BEGIN
    SELECT d.pedido_id,
           d.cantidad,
           pl.nombre AS plato_nombre
    FROM detalle_pedido d
    JOIN platos  pl ON pl.id = d.plato_id
    JOIN pedidos p  ON p.id  = d.pedido_id
    WHERE p.estado IN ('PENDIENTE', 'EN_PREPARACION', 'LISTO')
    ORDER BY d.pedido_id, d.id;
END //


DROP PROCEDURE IF EXISTS ReporteResumenVentas //
CREATE PROCEDURE ReporteResumenVentas(IN p_desde DATE, IN p_hasta DATE)
BEGIN
    SELECT COUNT(*)                AS total_pedidos,
           IFNULL(SUM(total), 0)   AS total_ventas,
           IFNULL(AVG(total), 0)   AS ticket_promedio
    FROM pedidos
    WHERE DATE(fecha_hora) BETWEEN p_desde AND p_hasta
      AND estado <> 'CANCELADO';
END //

DROP PROCEDURE IF EXISTS ReporteVentasPorDia //
CREATE PROCEDURE ReporteVentasPorDia(IN p_desde DATE, IN p_hasta DATE)
BEGIN
    SELECT DATE(fecha_hora)      AS fecha,
           COUNT(*)              AS pedidos,
           IFNULL(SUM(total), 0) AS total
    FROM pedidos
    WHERE DATE(fecha_hora) BETWEEN p_desde AND p_hasta
      AND estado <> 'CANCELADO'
    GROUP BY DATE(fecha_hora)
    ORDER BY fecha;
END //

DROP PROCEDURE IF EXISTS ReportePlatosMasVendidos //
CREATE PROCEDURE ReportePlatosMasVendidos(IN p_desde DATE, IN p_hasta DATE)
BEGIN
    SELECT pl.nombre                     AS plato,
           IFNULL(c.nombre, 'Sin categoria') AS categoria,
           SUM(d.cantidad)               AS cantidad,
           SUM(d.subtotal)               AS total
    FROM detalle_pedido d
    JOIN pedidos     p  ON p.id  = d.pedido_id
    JOIN platos      pl ON pl.id = d.plato_id
    LEFT JOIN categorias c ON c.id = pl.categoria_id
    WHERE DATE(p.fecha_hora) BETWEEN p_desde AND p_hasta
      AND p.estado <> 'CANCELADO'
    GROUP BY pl.id, pl.nombre, c.nombre
    ORDER BY cantidad DESC;
END //

DROP PROCEDURE IF EXISTS ReporteInsumosConsumidos //
CREATE PROCEDURE ReporteInsumosConsumidos(IN p_desde DATE, IN p_hasta DATE)
BEGIN
    SELECT i.nombre        AS insumo,
           i.unidad        AS unidad,
           SUM(mv.cantidad) AS consumido,
           i.stock         AS stock_actual,
           i.stock_minimo  AS stock_minimo
    FROM movimiento_insumo mv
    JOIN insumos i ON i.id = mv.insumo_id
    WHERE mv.tipo = 'SALIDA'
      AND DATE(mv.fecha_hora) BETWEEN p_desde AND p_hasta
    GROUP BY i.id, i.nombre, i.unidad, i.stock, i.stock_minimo
    ORDER BY consumido DESC;
END //

DROP PROCEDURE IF EXISTS ReportePedidosDetallado //
CREATE PROCEDURE ReportePedidosDetallado(IN p_desde DATE, IN p_hasta DATE)
BEGIN
    SELECT p.id,
           p.fecha_hora,
           m.numero AS mesa_numero,
           u.nombre AS usuario_nombre,
           p.estado,
           p.total
    FROM pedidos p
    JOIN mesas    m ON m.id = p.mesa_id
    JOIN usuarios u ON u.id = p.usuario_id
    WHERE DATE(p.fecha_hora) BETWEEN p_desde AND p_hasta
    ORDER BY p.fecha_hora;
END //

DELIMITER ;

-- DATOS DE PRUEBA
INSERT IGNORE INTO insumos (nombre, unidad, stock, stock_minimo) VALUES
    ('Arroz',            'kg',  50.000, 10.000),
    ('Pollo',            'kg',  30.000,  8.000),
    ('Papa blanca',      'kg',  40.000, 10.000),
    ('Cebolla',          'kg',  15.000,  4.000),
    ('Tomate',           'kg',  12.000,  3.000),
    ('Aceite vegetal',   'l',   20.000,  5.000),
    ('Huevo',            'und', 120.000, 30.000),
    ('Leche evaporada',  'ml', 8000.000, 2000.000),
    ('Sal',              'kg',   5.000,  1.000),
    ('Maracuya',         'kg',   6.000,  2.000),
    ('Azucar',           'kg',  10.000,  3.000);


INSERT IGNORE INTO plato_insumo (plato_id, insumo_id, cantidad)
SELECT p.id, i.id, 0.250
FROM platos p, insumos i
WHERE i.nombre = 'Arroz' AND p.nombre LIKE '%Arroz%';

INSERT IGNORE INTO plato_insumo (plato_id, insumo_id, cantidad)
SELECT p.id, i.id, 0.180
FROM platos p, insumos i
WHERE i.nombre = 'Pollo' AND p.nombre LIKE '%Arroz con Pollo%';

INSERT IGNORE INTO plato_insumo (plato_id, insumo_id, cantidad)
SELECT p.id, i.id, 0.300
FROM platos p, insumos i
WHERE i.nombre = 'Maracuya' AND p.nombre LIKE '%aracuy%';


