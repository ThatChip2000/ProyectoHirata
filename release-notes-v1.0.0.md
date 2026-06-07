# v1.0.0 - Versión Inicial

**Release Date:** 2026-03-01

## 🎉 Primer Commit: Proyecto EntregaFlota

Esta es la versión inicial del proyecto **EntregaFlota**, una aplicación Java Swing para la gestión integral de flota y mantenimiento de equipos.

## ✨ Características Principales

- ✅ **Registro de Kilometraje** - Ingreso y control de kilometraje de camiones con validaciones automáticas
- ✅ **Notificaciones de Mantenimiento** - Sistema automático de alertas cuando se requiere mantenimiento
- ✅ **Gestión de Equipos** - Control de PCs, impresoras, proyectores y otros equipos
- ✅ **Checklist de Mantenimiento** - Checklist personalizado por tipo de equipo
- ✅ **Consulta de Historial** - Visualización de mantenciones realizadas por código de equipo
- ✅ **Base de Datos MySQL** - Esquema completo y optimizado

## 📋 Contenido

- `RegistroKilometraje.java` - Aplicación principal
- `hirata_flotas_actualizado.sql` - Script completo de base de datos
- `reorden_checklist_items.sql` - Ajuste de orden de checklist
- `EQUIPOS_DISPONIBLES.txt` - Listado de equipos semilla

## 🔧 Requisitos

- Java JDK 17 o superior
- MySQL (XAMPP recomendado)
- MySQL Connector/J en classpath

## 🚀 Instrucciones de Ejecución

1. **Crear base de datos:**
   ```bash
   mysql -u root -p < hirata_flotas_actualizado.sql
   ```

2. **Compilar:**
   ```bash
   javac -cp ".;lib/*" RegistroKilometraje.java
   ```

3. **Ejecutar:**
   ```bash
   java -cp ".;lib/*" RegistroKilometraje
   ```

## 📝 Notas

- Este repositorio excluye credenciales reales para evitar exponer datos sensibles
- Requiere configuración de MySQL con usuario y contraseña
- La interfaz está desarrollada con Swing para máxima compatibilidad

---

**Desarrollado por:** ThatChip2000  
**Asignatura:** Integración de Competencias 1
