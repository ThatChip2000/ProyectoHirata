CREATE DATABASE IF NOT EXISTS hirata_flotas;
USE hirata_flotas;

DROP TABLE IF EXISTS mantenimiento;
DROP TABLE IF EXISTS camion;
DROP TABLE IF EXISTS usuarios;

CREATE TABLE usuarios (
  id INT NOT NULL AUTO_INCREMENT,
  usuario VARCHAR(50) NOT NULL UNIQUE,
  contrasena VARCHAR(255) NOT NULL,
  nombre VARCHAR(100) NOT NULL,
  rol ENUM('camionero', 'administrador') NOT NULL,
  activo TINYINT(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (id)
);

CREATE TABLE camion (
  id INT NOT NULL AUTO_INCREMENT,
  placa VARCHAR(20) NOT NULL UNIQUE,
  modelo VARCHAR(80) NOT NULL,
  kilometraje INT UNSIGNED NOT NULL DEFAULT 0,
  conductor_usuario_id INT DEFAULT NULL,
  actualizado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
);

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

INSERT INTO mantenimiento (id, camion_id, fecha, tipo, detalle, kilometraje_servicio, registrado_por_usuario_id, creado_en) VALUES
(1, 1, '2026-10-01', NULL, 'le cambie una llanta', 12333, NULL, '2026-04-11 09:22:53'),
(2, 1, '2001-01-01', NULL, 'asdasda', 17335, NULL, '2026-04-11 09:29:10');
