-- ============================================================================
-- SISTEMA DE GESTIÓN DE FLOTA + EQUIPOS - HIRATA
-- Base de datos completa con tablas de camiones y equipos
-- ============================================================================

CREATE DATABASE IF NOT EXISTS hirata_flotas;
USE hirata_flotas;

-- ============================================================================
-- TABLAS EXISTENTES (FLOTA)
-- ============================================================================

DROP TABLE IF EXISTS mantenimiento;
DROP TABLE IF EXISTS camion;
DROP TABLE IF EXISTS mantenimiento_checklist;
DROP TABLE IF EXISTS mantenimiento_equipo;
DROP TABLE IF EXISTS equipos;
DROP TABLE IF EXISTS checklist_items;
DROP TABLE IF EXISTS usuarios;

-- ============================================================================
-- TABLA: USUARIOS (Sin cambios, mantiene compatibilidad)
-- ============================================================================

CREATE TABLE usuarios (
  id INT NOT NULL AUTO_INCREMENT,
  usuario VARCHAR(50) NOT NULL UNIQUE,
  contrasena VARCHAR(255) NOT NULL,
  nombre VARCHAR(100) NOT NULL,
  rol ENUM('camionero', 'administrador') NOT NULL,
  activo TINYINT(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (id)
);

INSERT INTO usuarios (id, usuario, contrasena, nombre, rol, activo) VALUES
(1, 'admin', 'admin123', 'Administrador General', 'administrador', 1),
(2, 'juan.camion', '1234', 'Juan Perez', 'camionero', 1),
(3, 'maria.camion', '1234', 'Maria Lopez', 'camionero', 1),
(4, 'camionero01', 'Camion#2026', 'Jose Luis Ramirez', 'camionero', 1),
(5, 'camionero02', 'RutaSegura#26', 'Miguel Angel Torres', 'camionero', 1),
(6, 'camionero03', 'Volante#2026', 'Juan Carlos Mendoza', 'camionero', 1),
(7, 'camionero04', 'MotorFuerte#1', 'Pedro Antonio Salazar', 'camionero', 1),
(8, 'camionero05', 'DieselPlus#99', 'Ruben Dario Gutierrez', 'camionero', 1),
(9, 'camionero06', 'Kilometro#500', 'Oscar Ivan Paredes', 'camionero', 1),
(10, 'camionero07', 'CargaSegura#7', 'Luis Fernando Chavez', 'camionero', 1),
(11, 'camionero08', 'ViajeLargo#88', 'Hector Manuel Navarro', 'camionero', 1),
(12, 'camionero09', 'CabinaNorte#3', 'Cesar Augusto Rojas', 'camionero', 1),
(13, 'camionero10', 'FrenoOK#2026', 'Diego Armando Cardenas', 'camionero', 1),
(14, 'admin01', 'AdminSeguro#26', 'Laura Beatriz Herrera', 'administrador', 1),
(15, 'admin02', 'ControlFlota#1', 'Ricardo Alonso Vega', 'administrador', 1);

-- ============================================================================
-- TABLA: CAMION (Flota - Sin cambios)
-- ============================================================================

CREATE TABLE camion (
  id INT NOT NULL AUTO_INCREMENT,
  placa VARCHAR(20) NOT NULL UNIQUE,
  modelo VARCHAR(80) NOT NULL,
  kilometraje INT UNSIGNED NOT NULL DEFAULT 0,
  conductor_usuario_id INT DEFAULT NULL,
  actualizado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
);

INSERT INTO camion (id, placa, modelo, kilometraje, conductor_usuario_id, actualizado_en) VALUES
(1, '123k-23asd-23', 'Hirata', 17335, 3, '2026-04-11 09:24:31'),
(2, '123test', 'testt', 60000, 3, '2026-04-11 09:08:13'),
(3, 'HRT-101', 'Hino 500 FC9J', 3201, 2, '2026-04-11 09:38:21'),
(4, 'HRT-102', 'Isuzu FTR 850', 4800, NULL, '2026-04-11 09:11:52'),
(5, 'HRT-103', 'Mercedes-Benz Atego 1726', 5100, NULL, '2026-04-11 09:11:52'),
(6, 'HRT-104', 'Volvo VM 270', 7600, NULL, '2026-04-11 09:11:52'),
(7, 'HRT-105', 'Scania P250', 2900, NULL, '2026-04-11 09:11:52'),
(8, 'HRT-106', 'Freightliner M2 106', 9300, NULL, '2026-04-11 09:11:52'),
(9, 'HRT-107', 'Kenworth T370', 1500, NULL, '2026-04-11 09:11:52'),
(10, 'HRT-108', 'International MV607', 5400, NULL, '2026-04-11 09:11:52'),
(11, 'HRT-109', 'Mitsubishi Fuso Fighter', 8701, 2, '2026-04-11 09:22:16'),
(12, 'HRT-110', 'Chevrolet FVR', 4100, NULL, '2026-04-11 09:11:52'),
(13, 'test-test', 'testtttest', 123, 2, '2026-04-11 09:24:01'),
(14, '123asd123', 'sadasda', 5002, 2, '2026-04-12 16:28:57');

-- ============================================================================
-- TABLA: MANTENIMIENTO (Flota - Sin cambios)
-- ============================================================================

CREATE TABLE mantenimiento (
  id INT NOT NULL AUTO_INCREMENT,
  camion_id INT NOT NULL,
  fecha DATE NOT NULL,
  tipo VARCHAR(80) DEFAULT NULL,
  detalle TEXT NOT NULL,
  kilometraje_servicio INT UNSIGNED DEFAULT NULL,
  registrado_por_usuario_id INT DEFAULT NULL,
  creado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
);

INSERT INTO mantenimiento (id, camion_id, fecha, tipo, detalle, kilometraje_servicio, registrado_por_usuario_id, creado_en) VALUES
(1, 1, '2026-10-01', NULL, 'le cambie una llanta', 12333, NULL, '2026-04-11 09:22:53'),
(2, 1, '2001-01-01', NULL, 'asdasda', 17335, NULL, '2026-04-11 09:29:10');

-- ============================================================================
-- NUEVAS TABLAS PARA EQUIPOS
-- ============================================================================

-- TABLA: CHECKLIST_ITEMS
-- Almacena los items estándar de cada protocolo de mantenimiento
-- Se llenan una sola vez y sirven como plantilla para todos los mantenimientos

CREATE TABLE checklist_items (
  id INT NOT NULL AUTO_INCREMENT,
  tipo_equipo ENUM('computadora', 'impresora', 'proyector') NOT NULL,
  numero_seccion INT NOT NULL,
  titulo_seccion VARCHAR(150) NOT NULL,
  numero_item INT NOT NULL,
  descripcion VARCHAR(300) NOT NULL,
  es_critico TINYINT(1) DEFAULT 0,
  orden INT NOT NULL,
  creado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_checklist (tipo_equipo, numero_seccion, numero_item)
);

-- ============================================================================
-- INSERTS: CHECKLIST IMPRESORA EPSON ECOTANK M3180
-- ============================================================================

INSERT INTO checklist_items (tipo_equipo, numero_seccion, titulo_seccion, numero_item, descripcion, es_critico, orden) VALUES

-- SECCIÓN 1: Revisión de niveles y rellenado
('impresora', 1, 'Revisión de niveles y rellenado', 1, 'Verificar visualmente el nivel de tinta en tanques frontal', 1, 1),
('impresora', 1, 'Revisión de niveles y rellenado', 2, 'Rellenar tinta si está por debajo de la línea inferior', 1, 2),

-- SECCIÓN 2: Test de inyectores y limpieza de cabezales
('impresora', 2, 'Test de inyectores y limpieza', 1, 'Imprimir patrón de prueba desde menú Mantenimiento', 0, 3),
('impresora', 2, 'Test de inyectores y limpieza', 2, 'Ejecutar limpieza de cabezal (máximo 3 intentos)', 0, 4),
('impresora', 2, 'Test de inyectores y limpieza', 3, 'Si no mejora, dejar reposar el equipo por 12 horas', 0, 5),

-- SECCIÓN 3: Higiene del escáner y ADF
('impresora', 3, 'Higiene del escáner y ADF', 1, 'Limpiar cristal principal del escáner', 0, 6),
('impresora', 3, 'Higiene del escáner y ADF', 2, 'Limpiar tira pequeña de cristal a la izquierda (ADF)', 0, 7),
('impresora', 3, 'Higiene del escáner y ADF', 3, 'Usar paño de microfibra, NO aplicar líquido directo al cristal', 0, 8),

-- SECCIÓN 4: Limpieza de rodillos de tracción
('impresora', 4, 'Limpieza de rodillos', 1, 'Si papel se atasca, limpiar rodillos de goma con paño húmedo', 0, 10),
('impresora', 4, 'Limpieza de rodillos', 2, 'IMPORTANTE: NO utilizar alcohol en rodillos (reseca el caucho)', 1, 9),

-- SECCIÓN 5: Verificación de consumibles internos
('impresora', 5, 'Verificación de consumibles', 1, 'Revisar estado de la Caja de Mantenimiento en pantalla', 0, 11),
('impresora', 5, 'Verificación de consumibles', 2, 'Solicitar repuesto si está cerca del final de su vida útil', 0, 12);

-- ============================================================================
-- INSERTS: CHECKLIST PC OFICINA
-- ============================================================================

INSERT INTO checklist_items (tipo_equipo, numero_seccion, titulo_seccion, numero_item, descripcion, es_critico, orden) VALUES

-- SECCIÓN 1: Preparación y seguridad eléctrica
('computadora', 1, 'Preparación y seguridad eléctrica', 1, 'Apagar el equipo desde el sistema', 1, 1),
('computadora', 1, 'Preparación y seguridad eléctrica', 2, 'Desconectar el cable de poder', 1, 2),
('computadora', 1, 'Preparación y seguridad eléctrica', 3, 'Presionar botón de encendido 5 segundos con cable desenchufado', 1, 3),
('computadora', 1, 'Preparación y seguridad eléctrica', 4, 'Usar pulsera antiestática ANTES de tocar componentes internos', 1, 4),

-- SECCIÓN 2: Limpieza interna
('computadora', 2, 'Limpieza interna', 1, 'Retirar la tapa lateral del chasis', 0, 6),
('computadora', 2, 'Limpieza interna', 2, 'Usar aire comprimido o soplador para remover polvo', 0, 7),
('computadora', 2, 'Limpieza interna', 3, 'IMPORTANTE: Bloquear aspas de ventiladores ANTES de aplicar aire', 1, 5),

-- SECCIÓN 3: Revisión de contactos y periféricos
('computadora', 3, 'Revisión de contactos', 1, 'Retirar memorias RAM y limpiar contactos dorados con goma de borrar', 0, 8),
('computadora', 3, 'Revisión de contactos', 2, 'Eliminar residuos con paño seco', 0, 9),
('computadora', 3, 'Revisión de contactos', 3, 'Limpiar teclado y ratón con alcohol isopropílico 70%', 0, 10),
('computadora', 3, 'Revisión de contactos', 4, 'NO rociar líquido directo, humedecer el paño primero', 0, 11),
('computadora', 3, 'Revisión de contactos', 5, 'Verificar funcionamiento posterior del equipo', 0, 12);

-- ============================================================================
-- INSERTS: CHECKLIST PROYECTOR EPSON POWERLITE L690U
-- ============================================================================

INSERT INTO checklist_items (tipo_equipo, numero_seccion, titulo_seccion, numero_item, descripcion, es_critico, orden) VALUES

-- SECCIÓN 1: Desconexión y enfriamiento
('proyector', 1, 'Desconexión y enfriamiento', 1, 'Esperar a que los ventiladores se detengan completamente', 1, 1),
('proyector', 1, 'Desconexión y enfriamiento', 2, 'Esperar a que la luz de estado deje de parpadear', 1, 2),
('proyector', 1, 'Desconexión y enfriamiento', 3, 'Si fue usado recientemente, dejar enfriar 15 minutos mínimo', 1, 3),

-- SECCIÓN 2: Mantenimiento del filtro
('proyector', 2, 'Mantenimiento del filtro', 1, 'Localizar la cubierta en el lateral y retirar filtro con cuidado', 0, 5),
('proyector', 2, 'Mantenimiento del filtro', 2, 'Golpear marco suavemente contra superficie plana para polvo grueso', 0, 6),
('proyector', 2, 'Mantenimiento del filtro', 3, 'Usar aspiradora de baja potencia para restos de polvo fino', 0, 7),
('proyector', 2, 'Mantenimiento del filtro', 4, 'PROHIBIDO: aire comprimido, soplar con boca, secadores de pelo', 1, 4),
('proyector', 2, 'Mantenimiento del filtro', 5, 'Si polvo persiste, solicitar cambio de la pieza', 0, 8),
('proyector', 2, 'Mantenimiento del filtro', 6, 'Verificar que el filtro no tenga roturas antes de reinstalarlo', 0, 9),

-- SECCIÓN 3: Limpieza del lente
('proyector', 3, 'Limpieza del lente', 1, 'Usar pera de aire para remover partículas que puedan rayar cristal', 0, 10),
('proyector', 3, 'Limpieza del lente', 2, 'Usar paño de microfibra EXCLUSIVO para óptica', 0, 11),
('proyector', 3, 'Limpieza del lente', 3, 'Realizar movimientos circulares desde centro hacia afuera', 0, 12),
('proyector', 3, 'Limpieza del lente', 4, 'NO ejercer presión excesiva en el lente', 0, 13),

-- SECCIÓN 4: Diagnóstico de estado
('proyector', 4, 'Diagnóstico de estado', 1, 'Acceder a menú Configuración > Información', 0, 14),
('proyector', 4, 'Diagnóstico de estado', 2, 'Verificar que NO existan mensajes de advertencia en historial de eventos', 0, 15);

-- ============================================================================
-- TABLA: EQUIPOS
-- Almacena la información de cada equipo (PC, Impresora, Proyector)
-- ============================================================================

CREATE TABLE equipos (
  id INT NOT NULL AUTO_INCREMENT,
  tipo ENUM('computadora', 'impresora', 'proyector') NOT NULL,
  codigo VARCHAR(50) NOT NULL UNIQUE,
  modelo VARCHAR(100) NOT NULL,
  ubicacion VARCHAR(150) NOT NULL,
  estado ENUM('activo', 'inactivo', 'reparacion') NOT NULL DEFAULT 'activo',
  fecha_adquisicion DATE,
  responsable_usuario_id INT DEFAULT NULL,
  creado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  actualizado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  FOREIGN KEY (responsable_usuario_id) REFERENCES usuarios(id)
);

-- ============================================================================
-- INSERTS: DATOS DE EJEMPLO PARA EQUIPOS
-- ============================================================================

INSERT INTO equipos (tipo, codigo, modelo, ubicacion, estado, fecha_adquisicion, responsable_usuario_id) VALUES
-- Computadoras
('computadora', 'PC-001', 'Dell OptiPlex 7090', 'Oficina Administrativa', 'activo', '2024-01-15', 1),
('computadora', 'PC-002', 'HP ProDesk 600', 'Oficina Ventas', 'activo', '2024-02-10', 14),
('computadora', 'PC-003', 'Lenovo ThinkCentre', 'Oficina Contabilidad', 'activo', '2024-03-05', NULL),

-- Impresoras
('impresora', 'IMP-001', 'Epson EcoTank M3180', 'Recepción', 'activo', '2023-11-20', NULL),
('impresora', 'IMP-002', 'HP LaserJet Pro M404n', 'Oficina Administrativa', 'activo', '2024-01-08', 1),

-- Proyectores
('proyector', 'PROY-001', 'Epson PowerLite L690U', 'Sala de Juntas A', 'activo', '2023-09-12', NULL),
('proyector', 'PROY-002', 'Epson PowerLite L690U', 'Sala de Juntas B', 'activo', '2023-10-01', NULL);

-- ============================================================================
-- TABLA: MANTENIMIENTO_EQUIPO
-- Almacena cada mantención realizada a los equipos
-- ============================================================================

CREATE TABLE mantenimiento_equipo (
  id INT NOT NULL AUTO_INCREMENT,
  equipo_id INT NOT NULL,
  fecha_programada DATE NOT NULL,
  fecha_realizada DATE DEFAULT NULL,
  tipo ENUM('preventivo', 'correctivo') NOT NULL,
  estado ENUM('pendiente', 'en_proceso', 'completado', 'cancelado') NOT NULL DEFAULT 'pendiente',
  realizado_por_usuario_id INT DEFAULT NULL,
  detalles TEXT,
  creado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  FOREIGN KEY (equipo_id) REFERENCES equipos(id),
  FOREIGN KEY (realizado_por_usuario_id) REFERENCES usuarios(id)
);

-- ============================================================================
-- TABLA: MANTENIMIENTO_CHECKLIST
-- Almacena el estado de cada item del checklist para una mantención específica
-- ============================================================================

CREATE TABLE mantenimiento_checklist (
  id INT NOT NULL AUTO_INCREMENT,
  mantenimiento_equipo_id INT NOT NULL,
  checklist_item_id INT NOT NULL,
  completado TINYINT(1) NOT NULL DEFAULT 0,
  observacion TEXT,
  completado_en TIMESTAMP NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_mant_checklist (mantenimiento_equipo_id, checklist_item_id),
  FOREIGN KEY (mantenimiento_equipo_id) REFERENCES mantenimiento_equipo(id) ON DELETE CASCADE,
  FOREIGN KEY (checklist_item_id) REFERENCES checklist_items(id)
);

-- ============================================================================
-- INDICES ADICIONALES PARA OPTIMIZACIÓN
-- ============================================================================

CREATE INDEX idx_equipos_tipo ON equipos(tipo);
CREATE INDEX idx_equipos_estado ON equipos(estado);
CREATE INDEX idx_equipos_responsable ON equipos(responsable_usuario_id);

CREATE INDEX idx_mantenimiento_equipo_estado ON mantenimiento_equipo(estado);
CREATE INDEX idx_mantenimiento_equipo_realizado ON mantenimiento_equipo(realizado_por_usuario_id);
CREATE INDEX idx_mantenimiento_equipo_fecha ON mantenimiento_equipo(fecha_programada);

CREATE INDEX idx_checklist_items_tipo ON checklist_items(tipo_equipo);
CREATE INDEX idx_checklist_items_orden ON checklist_items(orden);

-- ============================================================================
-- FIN DEL SCRIPT
-- Base de datos lista para usar con la aplicación Java
-- ============================================================================
