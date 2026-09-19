# SGP Ay Chabela - Sistema de Gestión de Pedidos

Aplicación web para gestionar la atención del restaurante **Ay Chabela**: control de acceso por roles, carta de platos y administración de usuarios. En los siguientes avances se incorporarán mesas, pedidos, cocina, insumos y reportes.

Proyecto integrador del curso **Herramientas de Desarrollo** – Universidad Tecnológica del Perú.

## Integrantes

| Integrante | Rol en el equipo |
|---|---|
| Diego Zea | Responsable de repositorio |
| Ruben Eufracio | Responsable funcional |
| Daysi Galvez | Responsable de calidad y documentación |

## Problema

El restaurante Ay Chabela registra los pedidos de sus clientes de forma manual, lo que genera errores al tomar las órdenes, demoras en la comunicación entre el salón y la cocina, y dificultad para controlar la carta de platos y el personal que atiende. Se requiere un sistema web que permita gestionar usuarios con distintos roles, administrar los platos disponibles y, progresivamente, registrar pedidos y dar seguimiento a su preparación.

## Tecnologías

- Java 8 (Servlets y JSP)
- Apache Tomcat 9
- MySQL 8 con procedimientos almacenados
- NetBeans 17 (proyecto Ant)
- Arquitectura por capas: entidad, datos, negocio y presentación

## Requisitos

- JDK 8 o superior
- Apache Tomcat 9 registrado en NetBeans
- MySQL Server 8

## Instalación y ejecución

1. Clonar el repositorio:
   ```bash
   git clone https://github.com/Diego-Zea/SGP-AyChabela.git
   ```
2. Ejecutar en MySQL los scripts de la carpeta `database/` **en orden numérico** (`01_`, `02_`, ...).
3. Revisar usuario y contraseña de MySQL en `src/java/capaDatos/ConexionBD.java`.
4. Abrir el proyecto en NetBeans, seleccionar Tomcat 9 como servidor y ejecutar (Run).
5. Ingresar con un usuario de prueba: `admin / admin123`.

## Estructura

```
database/            Scripts SQL numerados
src/java/capaEntidad Clases de dominio
src/java/capaDatos   Acceso a datos (DAO)
src/java/capaNegocio Reglas de negocio
src/java/servlets    Controladores
web/                 Vistas JSP y estilos
```

## Estado del proyecto

**Avance 1 (APF1) – en desarrollo**

| Módulo | Estado |
|---|---|
| Inicio y cierre de sesión | En desarrollo |

## Convenciones

- Ramas: `main`, `feature/nombre-funcionalidad`, `fix/nombre-error`
- Commits: verbo en infinitivo que describa el cambio (ej. *Agregar formulario de platos*)
