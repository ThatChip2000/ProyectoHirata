# 🚀 ProyectoHirata - EntregaFlota

![Version](https://img.shields.io/badge/version-1.3.0-blue.svg)
![Java](https://img.shields.io/badge/Java-17+-orange.svg)
![License](https://img.shields.io/badge/license-MIT-green.svg)

Aplicación profesional **Java Swing** para gestión integral de flota y mantenimiento de equipos, desarrollada como proyecto de la asignatura *Integración de Competencias 1*.

## 📦 Releases y Versiones

Descarga la versión que necesites desde la página de [Releases](https://github.com/ThatChip2000/ProyectoHirata/releases):

- **[v1.3.0](https://github.com/ThatChip2000/ProyectoHirata/releases/tag/v1.3.0)** - Sistema de versionado profesional (Actual)
- **[v1.2.0](https://github.com/ThatChip2000/ProyectoHirata/releases/tag/v1.2.0)** - Integración de historial remoto
- **[v1.0.0](https://github.com/ThatChip2000/ProyectoHirata/releases/tag/v1.0.0)** - Versión inicial

> Ver [CHANGELOG.md](CHANGELOG.md) para un historial completo de cambios

## ✨ Características Principales

- ✅ **Registro de Kilometraje** - Ingreso y control de kilometraje de camiones con validaciones
- ✅ **Notificaciones de Mantenimiento** - Alertas automáticas cuando se requiere mantenimiento
- ✅ **Gestión de Equipos** - Control de PCs, impresoras, proyectores y otros equipos
- ✅ **Checklist de Mantenimiento** - Checklist personalizado por tipo de equipo
- ✅ **Consulta de Historial** - Visualización de mantenciones realizadas por equipo
- ✅ **Base de Datos MySQL** - Esquema completo y optimizado

## 📋 Estructura del Proyecto

```
ProyectoHirata/
├── RegistroKilometraje.java          # Aplicación principal
├── ControlInventarioPiezas.java      # Control de inventario
├── hirata.sql                         # Script base de datos actual
├── hirata_flotas_actualizado.sql     # Script base de datos completo
├── CHANGELOG.md                       # Historial de versiones
├── README.md                          # Este archivo
├── lib/                               # Librerías Java
├── .vscode/                           # Configuración VS Code
└── credenciales.txt                   # Plantilla de credenciales
```

## 🔧 Requisitos Previos

- **Java JDK 17** o superior
- **MySQL** (XAMPP recomendado)
- **MySQL Connector/J** en classpath
- **Git** 2.0+ (para clonar el repositorio)

## 🚀 Instrucciones de Instalación y Ejecución

### 1. Clonar el Repositorio

```bash
git clone https://github.com/ThatChip2000/ProyectoHirata.git
cd ProyectoHirata
```

### 2. Configurar Base de Datos

```bash
mysql -u root -p < hirata_flotas_actualizado.sql
```

### 3. Configurar Credenciales

Edita `credenciales.txt` con tus datos de MySQL:

```
usuario=root
contraseña=tu_contraseña
base_datos=hirata_flotas
```

### 4. Compilar

```bash
javac -cp ".;lib/*" RegistroKilometraje.java
```

### 5. Ejecutar

```bash
java -cp ".;lib/*" RegistroKilometraje
```

## 📚 Documentación Adicional

- [CHANGELOG.md](CHANGELOG.md) - Historial completo de cambios y versiones
- [LEER ANTES DE HACER ALGO.txt](LEER%20ANTES%20DE%20HACER%20ALGO.txt) - Notas importantes del proyecto
- [Releases GitHub](https://github.com/ThatChip2000/ProyectoHirata/releases) - Descargas de versiones

## 🔐 Notas de Seguridad

⚠️ **Este repositorio excluye intencionalmente:**
- ❌ Credenciales reales de MySQL
- ❌ Tokens o API keys
- ❌ Datos sensibles de usuarios

Para usar localmente, configura `credenciales.txt` con tus valores.

## 👨‍💻 Desarrollado por

**ThatChip2000** - [GitHub](https://github.com/ThatChip2000)

## 📄 Licencia

Este proyecto es parte de un proyecto académico. Ver LICENSE para más detalles.

## 🤝 Contribuciones

Las contribuciones son bienvenidas. Para cambios mayores, abre un issue primero para discutir los cambios propuestos.

---

**Estado:** En desarrollo activo | **Última actualización:** 2026-06-07
