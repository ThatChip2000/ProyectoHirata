# EntregaFlota

Aplicacion Java Swing para gestion de flota y mantenimiento de equipos.

## Incluye

- Registro de kilometraje de camiones
- Registro de mantenimiento de camiones
- Registro de equipos (PC, impresoras, proyectores)
- Mantenimiento de equipos con checklist por tipo
- Consulta de mantenciones por codigo de equipo

## Estructura principal

- RegistroKilometraje.java: aplicacion principal
- hirata_flotas_actualizado.sql: script completo de base de datos
- reorden_checklist_items.sql: ajuste de orden de checklist
- EQUIPOS_DISPONIBLES.txt: listado de equipos semilla
- LEER ANTES DE HACER ALGO.txt: notas del proyecto

## Requisitos

- Java JDK 17 o superior
- MySQL (XAMPP)
- MySQL Connector/J en classpath

## Ejecucion

1. Crear la base de datos ejecutando hirata_flotas_actualizado.sql en MySQL.
2. Compilar:

   javac -cp ".;lib/*" RegistroKilometraje.java

3. Ejecutar:

   java -cp ".;lib/*" RegistroKilometraje

## Notas

- Este repositorio excluye pruebas unitarias por solicitud.
- Este repositorio excluye credenciales reales para evitar exponer datos sensibles.
