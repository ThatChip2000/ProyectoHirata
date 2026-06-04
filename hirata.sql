-- MySQL dump 10.13  Distrib 8.0.19, for Win64 (x86_64)
--
-- Host: localhost    Database: hirata_flotas
-- ------------------------------------------------------
-- Server version	5.5.5-10.4.32-MariaDB

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `camion`
--

DROP TABLE IF EXISTS `camion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `camion` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `placa` varchar(20) NOT NULL,
  `modelo` varchar(80) NOT NULL,
  `kilometraje` int(10) unsigned NOT NULL DEFAULT 0,
  `conductor_usuario_id` int(11) DEFAULT NULL,
  `actualizado_en` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`id`),
  UNIQUE KEY `placa` (`placa`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `camion`
--

LOCK TABLES `camion` WRITE;
/*!40000 ALTER TABLE `camion` DISABLE KEYS */;
INSERT INTO `camion` VALUES (1,'123k-23asd-23','Hirata',17335,3,'2026-04-11 13:24:31'),(2,'123test','testt',60000,3,'2026-04-11 13:08:13'),(3,'HRT-101','Hino 500 FC9J',3201,2,'2026-04-11 13:38:21'),(4,'HRT-102','Isuzu FTR 850',4800,NULL,'2026-04-11 13:11:52'),(5,'HRT-103','Mercedes-Benz Atego 1726',5100,NULL,'2026-04-11 13:11:52'),(6,'HRT-104','Volvo VM 270',7600,NULL,'2026-04-11 13:11:52'),(7,'HRT-105','Scania P250',2900,NULL,'2026-04-11 13:11:52'),(8,'HRT-106','Freightliner M2 106',9300,NULL,'2026-04-11 13:11:52'),(9,'HRT-107','Kenworth T370',1500,NULL,'2026-04-11 13:11:52'),(10,'HRT-108','International MV607',5400,NULL,'2026-04-11 13:11:52'),(11,'HRT-109','Mitsubishi Fuso Fighter',8701,2,'2026-04-11 13:22:16'),(12,'HRT-110','Chevrolet FVR',4100,NULL,'2026-04-11 13:11:52'),(13,'test-test','testtttest',123,2,'2026-04-11 13:24:01'),(14,'123asd123','sadasda',5002,2,'2026-04-12 20:28:57');
/*!40000 ALTER TABLE `camion` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `checklist_items`
--

DROP TABLE IF EXISTS `checklist_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `checklist_items` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `tipo_equipo` enum('computadora','impresora','proyector') NOT NULL,
  `numero_seccion` int(11) NOT NULL,
  `titulo_seccion` varchar(150) NOT NULL,
  `numero_item` int(11) NOT NULL,
  `descripcion` varchar(300) NOT NULL,
  `es_critico` tinyint(1) DEFAULT 0,
  `orden` int(11) NOT NULL,
  `creado_en` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_checklist` (`tipo_equipo`,`numero_seccion`,`numero_item`),
  KEY `idx_checklist_items_tipo` (`tipo_equipo`),
  KEY `idx_checklist_items_orden` (`orden`)
) ENGINE=InnoDB AUTO_INCREMENT=40 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `checklist_items`
--

LOCK TABLES `checklist_items` WRITE;
/*!40000 ALTER TABLE `checklist_items` DISABLE KEYS */;
INSERT INTO `checklist_items` VALUES (1,'impresora',1,'Revisión de niveles y rellenado',1,'Verificar visualmente el nivel de tinta en tanques frontal',1,1,'2026-05-13 18:53:12'),(2,'impresora',1,'Revisión de niveles y rellenado',2,'Rellenar tinta si está por debajo de la línea inferior',1,2,'2026-05-13 18:53:12'),(3,'impresora',2,'Test de inyectores y limpieza',1,'Imprimir patrón de prueba desde menú Mantenimiento',0,3,'2026-05-13 18:53:12'),(4,'impresora',2,'Test de inyectores y limpieza',2,'Ejecutar limpieza de cabezal (máximo 3 intentos)',0,4,'2026-05-13 18:53:12'),(5,'impresora',2,'Test de inyectores y limpieza',3,'Si no mejora, dejar reposar el equipo por 12 horas',0,5,'2026-05-13 18:53:12'),(6,'impresora',3,'Higiene del escáner y ADF',1,'Limpiar cristal principal del escáner',0,6,'2026-05-13 18:53:12'),(7,'impresora',3,'Higiene del escáner y ADF',2,'Limpiar tira pequeña de cristal a la izquierda (ADF)',0,7,'2026-05-13 18:53:12'),(8,'impresora',3,'Higiene del escáner y ADF',3,'Usar paño de microfibra, NO aplicar líquido directo al cristal',0,8,'2026-05-13 18:53:12'),(9,'impresora',4,'Limpieza de rodillos',1,'Si papel se atasca, limpiar rodillos de goma con paño húmedo',0,10,'2026-05-13 18:53:12'),(10,'impresora',4,'Limpieza de rodillos',2,'IMPORTANTE: NO utilizar alcohol en rodillos (reseca el caucho)',1,9,'2026-05-13 18:53:12'),(11,'impresora',5,'Verificación de consumibles',1,'Revisar estado de la Caja de Mantenimiento en pantalla',0,11,'2026-05-13 18:53:12'),(12,'impresora',5,'Verificación de consumibles',2,'Solicitar repuesto si está cerca del final de su vida útil',0,12,'2026-05-13 18:53:12'),(13,'computadora',1,'Preparación y seguridad eléctrica',1,'Apagar el equipo desde el sistema',1,1,'2026-05-13 18:53:12'),(14,'computadora',1,'Preparación y seguridad eléctrica',2,'Desconectar el cable de poder',1,2,'2026-05-13 18:53:12'),(15,'computadora',1,'Preparación y seguridad eléctrica',3,'Presionar botón de encendido 5 segundos con cable desenchufado',1,3,'2026-05-13 18:53:12'),(16,'computadora',1,'Preparación y seguridad eléctrica',4,'Usar pulsera antiestática ANTES de tocar componentes internos',1,4,'2026-05-13 18:53:12'),(17,'computadora',2,'Limpieza interna',1,'Retirar la tapa lateral del chasis',0,6,'2026-05-13 18:53:12'),(18,'computadora',2,'Limpieza interna',2,'Usar aire comprimido o soplador para remover polvo',0,7,'2026-05-13 18:53:12'),(19,'computadora',2,'Limpieza interna',3,'IMPORTANTE: Bloquear aspas de ventiladores ANTES de aplicar aire',1,5,'2026-05-13 18:53:12'),(20,'computadora',3,'Revisión de contactos',1,'Retirar memorias RAM y limpiar contactos dorados con goma de borrar',0,8,'2026-05-13 18:53:12'),(21,'computadora',3,'Revisión de contactos',2,'Eliminar residuos con paño seco',0,9,'2026-05-13 18:53:12'),(22,'computadora',3,'Revisión de contactos',3,'Limpiar teclado y ratón con alcohol isopropílico 70%',0,10,'2026-05-13 18:53:12'),(23,'computadora',3,'Revisión de contactos',4,'NO rociar líquido directo, humedecer el paño primero',0,11,'2026-05-13 18:53:12'),(24,'computadora',3,'Revisión de contactos',5,'Verificar funcionamiento posterior del equipo',0,12,'2026-05-13 18:53:12'),(25,'proyector',1,'Desconexión y enfriamiento',1,'Esperar a que los ventiladores se detengan completamente',1,1,'2026-05-13 18:53:12'),(26,'proyector',1,'Desconexión y enfriamiento',2,'Esperar a que la luz de estado deje de parpadear',1,2,'2026-05-13 18:53:12'),(27,'proyector',1,'Desconexión y enfriamiento',3,'Si fue usado recientemente, dejar enfriar 15 minutos mínimo',1,3,'2026-05-13 18:53:12'),(28,'proyector',2,'Mantenimiento del filtro',1,'Localizar la cubierta en el lateral y retirar filtro con cuidado',0,5,'2026-05-13 18:53:12'),(29,'proyector',2,'Mantenimiento del filtro',2,'Golpear marco suavemente contra superficie plana para polvo grueso',0,6,'2026-05-13 18:53:12'),(30,'proyector',2,'Mantenimiento del filtro',3,'Usar aspiradora de baja potencia para restos de polvo fino',0,7,'2026-05-13 18:53:12'),(31,'proyector',2,'Mantenimiento del filtro',4,'PROHIBIDO: aire comprimido, soplar con boca, secadores de pelo',1,4,'2026-05-13 18:53:12'),(32,'proyector',2,'Mantenimiento del filtro',5,'Si polvo persiste, solicitar cambio de la pieza',0,8,'2026-05-13 18:53:12'),(33,'proyector',2,'Mantenimiento del filtro',6,'Verificar que el filtro no tenga roturas antes de reinstalarlo',0,9,'2026-05-13 18:53:12'),(34,'proyector',3,'Limpieza del lente',1,'Usar pera de aire para remover partículas que puedan rayar cristal',0,10,'2026-05-13 18:53:12'),(35,'proyector',3,'Limpieza del lente',2,'Usar paño de microfibra EXCLUSIVO para óptica',0,11,'2026-05-13 18:53:12'),(36,'proyector',3,'Limpieza del lente',3,'Realizar movimientos circulares desde centro hacia afuera',0,12,'2026-05-13 18:53:12'),(37,'proyector',3,'Limpieza del lente',4,'NO ejercer presión excesiva en el lente',0,13,'2026-05-13 18:53:12'),(38,'proyector',4,'Diagnóstico de estado',1,'Acceder a menú Configuración > Información',0,14,'2026-05-13 18:53:12'),(39,'proyector',4,'Diagnóstico de estado',2,'Verificar que NO existan mensajes de advertencia en historial de eventos',0,15,'2026-05-13 18:53:12');
/*!40000 ALTER TABLE `checklist_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `equipos`
--

DROP TABLE IF EXISTS `equipos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `equipos` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `tipo` enum('computadora','impresora','proyector') NOT NULL,
  `codigo` varchar(50) NOT NULL,
  `modelo` varchar(100) NOT NULL,
  `ubicacion` varchar(150) NOT NULL,
  `estado` enum('activo','inactivo','reparacion') NOT NULL DEFAULT 'activo',
  `fecha_adquisicion` date DEFAULT NULL,
  `responsable_usuario_id` int(11) DEFAULT NULL,
  `creado_en` timestamp NOT NULL DEFAULT current_timestamp(),
  `actualizado_en` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`id`),
  UNIQUE KEY `codigo` (`codigo`),
  KEY `idx_equipos_tipo` (`tipo`),
  KEY `idx_equipos_estado` (`estado`),
  KEY `idx_equipos_responsable` (`responsable_usuario_id`),
  CONSTRAINT `equipos_ibfk_1` FOREIGN KEY (`responsable_usuario_id`) REFERENCES `usuarios` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `equipos`
--

LOCK TABLES `equipos` WRITE;
/*!40000 ALTER TABLE `equipos` DISABLE KEYS */;
INSERT INTO `equipos` VALUES (1,'computadora','PC-001','Dell OptiPlex 7090','Oficina Administrativa','activo','2024-01-15',1,'2026-05-13 18:53:12','2026-05-13 18:53:12'),(2,'computadora','PC-002','HP ProDesk 600','Oficina Ventas','activo','2024-02-10',14,'2026-05-13 18:53:12','2026-05-13 18:53:12'),(3,'computadora','PC-003','Lenovo ThinkCentre','Oficina Contabilidad','activo','2024-03-05',NULL,'2026-05-13 18:53:12','2026-05-13 18:53:12'),(4,'impresora','IMP-001','Epson EcoTank M3180','Recepción','activo','2023-11-20',NULL,'2026-05-13 18:53:12','2026-05-13 18:53:12'),(5,'impresora','IMP-002','HP LaserJet Pro M404n','Oficina Administrativa','activo','2024-01-08',1,'2026-05-13 18:53:12','2026-05-13 18:53:12'),(6,'proyector','PROY-001','Epson PowerLite L690U','Sala de Juntas A','activo','2023-09-12',NULL,'2026-05-13 18:53:12','2026-05-13 18:53:12'),(7,'proyector','PROY-002','Epson PowerLite L690U','Sala de Juntas B','activo','2023-10-01',NULL,'2026-05-13 18:53:12','2026-05-13 18:53:12'),(8,'computadora','PC-004','wwwwe','wsdad','activo',NULL,NULL,'2026-05-13 20:25:16','2026-05-13 20:25:16');
/*!40000 ALTER TABLE `equipos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `inventario_piezas`
--

DROP TABLE IF EXISTS `inventario_piezas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `inventario_piezas` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(150) NOT NULL,
  `tipo_pieza` enum('hardware_almacenamiento','hardware_memoria','hardware_refrigeracion','hardware_fuente','hardware_otro','consumible_tinta','consumible_papel','consumible_toner','consumible_otro','repuesto_impresora','repuesto_proyector','repuesto_general') NOT NULL,
  `descripcion` text DEFAULT NULL,
  `cantidad_actual` int(11) NOT NULL DEFAULT 0,
  `cantidad_minima` int(11) NOT NULL DEFAULT 1,
  `estado` enum('disponible','en_reparacion','fuera_de_servicio','agotado') NOT NULL DEFAULT 'disponible',
  `ubicacion_almacen` varchar(100) DEFAULT NULL,
  `proveedor` varchar(100) DEFAULT NULL,
  `precio_unitario` decimal(10,2) DEFAULT NULL,
  `creado_en` timestamp NOT NULL DEFAULT current_timestamp(),
  `actualizado_en` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`id`),
  KEY `idx_inventario_tipo` (`tipo_pieza`),
  KEY `idx_inventario_estado` (`estado`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `inventario_piezas`
--

LOCK TABLES `inventario_piezas` WRITE;
/*!40000 ALTER TABLE `inventario_piezas` DISABLE KEYS */;
INSERT INTO `inventario_piezas` VALUES (18,'Memoria RAM DDR4 8GB','hardware_memoria','Módulo RAM DDR4 2400MHz 8GB compatible con PCs de oficina',4,2,'disponible','Estante A - Cajón 1','TechSupply Ltda',35990.00,'2026-05-13 20:05:04','2026-05-13 20:05:04'),(19,'Disco SSD 240GB','hardware_almacenamiento','Disco sólido SATA III 240GB para reemplazo en computadoras',2,1,'disponible','Estante A - Cajón 2','TechSupply Ltda',49990.00,'2026-05-13 20:05:04','2026-05-13 20:05:04'),(20,'Tinta negra Epson EcoTank','consumible_tinta','Botella tinta negra 70ml para IMP-001 Epson EcoTank M3180',6,2,'disponible','Estante B - Cajón 1','OfficeMax',8990.00,'2026-05-13 20:05:04','2026-05-13 20:05:04'),(21,'Caja de Mantenimiento Epson','repuesto_impresora','Caja de mantenimiento Epson T04D1 para EcoTank M3180',2,1,'disponible','Estante B - Cajón 2','OfficeMax',24990.00,'2026-05-13 20:05:04','2026-05-13 20:05:04'),(22,'Filtro proyector Epson L690U','repuesto_proyector','Filtro de aire de repuesto para PROY-001 y PROY-002',3,1,'disponible','Estante C - Cajón 1','ProyectaChile',15990.00,'2026-05-13 20:05:04','2026-05-13 20:05:04'),(23,'Ventilador CPU 80mm','hardware_refrigeracion','Ventilador de reemplazo para torre de PC, 80mm, 12V',2,1,'disponible','Estante A - Cajón 3','TechSupply Ltda',9990.00,'2026-05-13 20:05:04','2026-05-13 20:05:04'),(24,'Cable HDMI 2m','repuesto_general','Cable HDMI 2.0 de 2 metros para conexión de proyectores',5,2,'disponible','Estante C - Cajón 2','OfficeMax',4990.00,'2026-05-13 20:05:04','2026-05-13 20:05:04'),(25,'Tóner HP LaserJet M404','consumible_toner','Cartucho de tóner original HP CF259A para IMP-002',1,1,'disponible','Estante B - Cajón 3','OfficeMax',79990.00,'2026-05-13 20:05:04','2026-05-13 20:05:04');
/*!40000 ALTER TABLE `inventario_piezas` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mantenimiento`
--

DROP TABLE IF EXISTS `mantenimiento`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `mantenimiento` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `camion_id` int(11) NOT NULL,
  `fecha` date NOT NULL,
  `tipo` varchar(80) DEFAULT NULL,
  `detalle` text NOT NULL,
  `kilometraje_servicio` int(10) unsigned DEFAULT NULL,
  `registrado_por_usuario_id` int(11) DEFAULT NULL,
  `creado_en` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mantenimiento`
--

LOCK TABLES `mantenimiento` WRITE;
/*!40000 ALTER TABLE `mantenimiento` DISABLE KEYS */;
INSERT INTO `mantenimiento` VALUES (1,1,'2026-10-01',NULL,'le cambie una llanta',12333,NULL,'2026-04-11 13:22:53'),(2,1,'2001-01-01',NULL,'asdasda',17335,NULL,'2026-04-11 13:29:10');
/*!40000 ALTER TABLE `mantenimiento` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mantenimiento_checklist`
--

DROP TABLE IF EXISTS `mantenimiento_checklist`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `mantenimiento_checklist` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `mantenimiento_equipo_id` int(11) NOT NULL,
  `checklist_item_id` int(11) NOT NULL,
  `completado` tinyint(1) NOT NULL DEFAULT 0,
  `observacion` text DEFAULT NULL,
  `completado_en` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_mant_checklist` (`mantenimiento_equipo_id`,`checklist_item_id`),
  KEY `checklist_item_id` (`checklist_item_id`),
  CONSTRAINT `mantenimiento_checklist_ibfk_1` FOREIGN KEY (`mantenimiento_equipo_id`) REFERENCES `mantenimiento_equipo` (`id`) ON DELETE CASCADE,
  CONSTRAINT `mantenimiento_checklist_ibfk_2` FOREIGN KEY (`checklist_item_id`) REFERENCES `checklist_items` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mantenimiento_checklist`
--

LOCK TABLES `mantenimiento_checklist` WRITE;
/*!40000 ALTER TABLE `mantenimiento_checklist` DISABLE KEYS */;
/*!40000 ALTER TABLE `mantenimiento_checklist` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mantenimiento_equipo`
--

DROP TABLE IF EXISTS `mantenimiento_equipo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `mantenimiento_equipo` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `equipo_id` int(11) NOT NULL,
  `fecha_programada` date NOT NULL,
  `fecha_realizada` date DEFAULT NULL,
  `tipo` enum('preventivo','correctivo') NOT NULL,
  `estado` enum('pendiente','en_proceso','completado','cancelado') NOT NULL DEFAULT 'pendiente',
  `realizado_por_usuario_id` int(11) DEFAULT NULL,
  `detalles` text DEFAULT NULL,
  `creado_en` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`),
  KEY `equipo_id` (`equipo_id`),
  KEY `idx_mantenimiento_equipo_estado` (`estado`),
  KEY `idx_mantenimiento_equipo_realizado` (`realizado_por_usuario_id`),
  KEY `idx_mantenimiento_equipo_fecha` (`fecha_programada`),
  CONSTRAINT `mantenimiento_equipo_ibfk_1` FOREIGN KEY (`equipo_id`) REFERENCES `equipos` (`id`),
  CONSTRAINT `mantenimiento_equipo_ibfk_2` FOREIGN KEY (`realizado_por_usuario_id`) REFERENCES `usuarios` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mantenimiento_equipo`
--

LOCK TABLES `mantenimiento_equipo` WRITE;
/*!40000 ALTER TABLE `mantenimiento_equipo` DISABLE KEYS */;
/*!40000 ALTER TABLE `mantenimiento_equipo` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `movimiento_inventario`
--

DROP TABLE IF EXISTS `movimiento_inventario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `movimiento_inventario` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `pieza_id` int(11) NOT NULL,
  `tipo_movimiento` enum('ingreso','salida') NOT NULL,
  `cantidad` int(11) NOT NULL,
  `motivo` varchar(200) DEFAULT NULL,
  `equipo_id` int(11) DEFAULT NULL,
  `mantenimiento_equipo_id` int(11) DEFAULT NULL,
  `usuario_id` int(11) NOT NULL,
  `observacion` text DEFAULT NULL,
  `fecha_movimiento` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`),
  KEY `equipo_id` (`equipo_id`),
  KEY `mantenimiento_equipo_id` (`mantenimiento_equipo_id`),
  KEY `usuario_id` (`usuario_id`),
  KEY `idx_movimiento_pieza` (`pieza_id`),
  KEY `idx_movimiento_fecha` (`fecha_movimiento`),
  KEY `idx_movimiento_tipo` (`tipo_movimiento`),
  CONSTRAINT `movimiento_inventario_ibfk_1` FOREIGN KEY (`pieza_id`) REFERENCES `inventario_piezas` (`id`) ON DELETE CASCADE,
  CONSTRAINT `movimiento_inventario_ibfk_2` FOREIGN KEY (`equipo_id`) REFERENCES `equipos` (`id`) ON DELETE SET NULL,
  CONSTRAINT `movimiento_inventario_ibfk_3` FOREIGN KEY (`mantenimiento_equipo_id`) REFERENCES `mantenimiento_equipo` (`id`) ON DELETE SET NULL,
  CONSTRAINT `movimiento_inventario_ibfk_4` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `movimiento_inventario`
--

LOCK TABLES `movimiento_inventario` WRITE;
/*!40000 ALTER TABLE `movimiento_inventario` DISABLE KEYS */;
/*!40000 ALTER TABLE `movimiento_inventario` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usuarios`
--

DROP TABLE IF EXISTS `usuarios`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuarios` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `usuario` varchar(50) NOT NULL,
  `contrasena` varchar(255) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `rol` enum('camionero','administrador') NOT NULL,
  `activo` tinyint(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (`id`),
  UNIQUE KEY `usuario` (`usuario`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuarios`
--

LOCK TABLES `usuarios` WRITE;
/*!40000 ALTER TABLE `usuarios` DISABLE KEYS */;
INSERT INTO `usuarios` VALUES (1,'admin','admin123','Administrador General','administrador',1),(2,'juan.camion','1234','Juan Perez','camionero',1),(3,'maria.camion','1234','Maria Lopez','camionero',1),(4,'camionero01','Camion#2026','Jose Luis Ramirez','camionero',1),(5,'camionero02','RutaSegura#26','Miguel Angel Torres','camionero',1),(6,'camionero03','Volante#2026','Juan Carlos Mendoza','camionero',1),(7,'camionero04','MotorFuerte#1','Pedro Antonio Salazar','camionero',1),(8,'camionero05','DieselPlus#99','Ruben Dario Gutierrez','camionero',1),(9,'camionero06','Kilometro#500','Oscar Ivan Paredes','camionero',1),(10,'camionero07','CargaSegura#7','Luis Fernando Chavez','camionero',1),(11,'camionero08','ViajeLargo#88','Hector Manuel Navarro','camionero',1),(12,'camionero09','CabinaNorte#3','Cesar Augusto Rojas','camionero',1),(13,'camionero10','FrenoOK#2026','Diego Armando Cardenas','camionero',1),(14,'admin01','AdminSeguro#26','Laura Beatriz Herrera','administrador',1),(15,'admin02','ControlFlota#1','Ricardo Alonso Vega','administrador',1);
/*!40000 ALTER TABLE `usuarios` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'hirata_flotas'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-05-31 18:04:54
