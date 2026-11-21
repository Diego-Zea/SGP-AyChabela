# SGP Ay Chabela - Sistema de Gestión de Pedidos

Aplicación web para gestionar la atención del restaurante **Ay Chabela**: control de acceso por roles, carta de platos, administración de usuarios, estado de mesas, registro de pedidos, panel de cocina e inventario de insumos con recetas por plato.

Proyecto integrador del curso **Herramientas de Desarrollo** – Universidad Tecnológica del Perú.

## Enlaces

| Recurso | Enlace |
|---|---|
| Repositorio | https://github.com/Diego-Zea/SGP-AyChabela |
| Tablero de trabajo | https://github.com/users/Diego-Zea/projects/1 |
| Imagen publicada | https://github.com/users/Diego-Zea/packages/container/package/sgp-aychabela |
| Aplicación desplegada | sgp-aychabela-production.up.railway.app |

## Integrantes

| Integrante | Rol en el equipo |
|---|---|
| Diego Zea | Responsable de repositorio |
| Ruben Eufracio | Responsable funcional |
| Daysi Galvez | Responsable de calidad y documentación |

## Problema

El restaurante Ay Chabela registra los pedidos de sus clientes de forma manual, lo que genera errores al tomar las órdenes, demoras en la comunicación entre el salón y la cocina, y dificultad para controlar la carta de platos y el personal que atiende. Se requiere un sistema web que permita gestionar usuarios con distintos roles, administrar los platos disponibles, registrar pedidos, dar seguimiento a su preparación en cocina y controlar el stock de insumos.

## Tecnologías

- Java 8 (Servlets y JSP)
- Apache Tomcat 9
- MySQL 8 con procedimientos almacenados
- NetBeans 17 (proyecto Ant)
- Docker para empaquetado de la aplicación
- GitHub Actions para integración y entrega continuas
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
2. Ejecutar en MySQL los seis scripts de la carpeta `database/` **en orden**, del `01_usuarios_login.sql` al `06_cocina_insumos.sql`. El primero crea la base de datos `aychabela`.
3. Abrir el proyecto en NetBeans, seleccionar Tomcat 9 como servidor y ejecutar con **Run**.
4. Ingresar con uno de los usuarios de prueba.

No hace falta editar ningún archivo de código: la conexión usa `localhost`, `root` y `root` por defecto. Si tu MySQL local usa otras credenciales, defínelas como variables de entorno (ver más abajo).

### Usuarios de prueba

| Usuario | Contraseña | Acceso |
|---|---|---|
| `admin` | `admin123` | Todos los módulos |
| `mesero` | `mesero123` | Platos, Pedidos |
| `cocina` | `cocina123` | Platos, Pedidos, Cocina, Insumos |

## Ejecutar con Docker

La aplicación se empaqueta en una imagen que incluye Tomcat y no requiere instalar NetBeans ni el servidor:

```bash
docker build -t aychabela .

docker run -p 8080:8080 \
  -e DB_HOST=mi-servidor \
  -e DB_NAME=aychabela \
  -e DB_USER=usuario \
  -e DB_PASSWORD=*** \
  aychabela
```

También puede descargarse la imagen ya publicada:

```bash
docker pull ghcr.io/diego-zea/sgp-aychabela:latest
```

### Variables de entorno

Los datos de conexión se leen del entorno para poder desplegar en distintos servidores sin modificar el código ni guardar contraseñas en el repositorio. Si una variable no está definida, se usa el valor local.

| Variable | Por defecto | Para qué |
|---|---|---|
| `DB_HOST` | `localhost` | Servidor de MySQL |
| `DB_PORT` | `3306` | Puerto |
| `DB_NAME` | `aychabela` | Base de datos |
| `DB_USER` | `root` | Usuario |
| `DB_PASSWORD` | `root` | Contraseña |

## Integración y entrega continuas

El repositorio tiene dos flujos automatizados en GitHub Actions:

**Integración continua** (`.github/workflows/ci.yml`) — se ejecuta en cada push a `main` y en cada Pull Request. Compila los archivos Java del proyecto, ejecuta las pruebas unitarias del control de acceso por roles y verifica los scripts de base de datos. Un Pull Request no se revisa hasta que este flujo está en verde.

**Entrega continua** (`.github/workflows/cd.yml`) — se ejecuta al integrar cambios en `main`. Construye la imagen de la aplicación a partir del `Dockerfile` y la publica en el registro de contenedores de GitHub con dos etiquetas: `latest` y el identificador del commit, de modo que cada versión publicada es rastreable hasta el código que la originó.

### Pruebas

Las pruebas unitarias están en `test/pruebas/` y cubren `Acceso.tieneRol`, el método que decide qué módulos ve cada rol. Las ejecuta el flujo de integración continua en cada cambio.

## Estructura

```
.github/workflows/   Flujos de integración y entrega continuas
database/            Scripts SQL numerados (01 a 06)
src/java/capaEntidad Clases de dominio
src/java/capaDatos   Acceso a datos (DAO)
src/java/capaNegocio Reglas de negocio
src/java/servlets    Controladores
test/pruebas/        Pruebas unitarias
web/                 Vistas JSP y estilos
Dockerfile           Empaquetado de la aplicación
```

## Estado del proyecto

**Avance 3 (APF3) – completado**

| Módulo | Estado |
|---|---|
| Inicio y cierre de sesión | Terminado |
| Gestión de platos | Terminado |
| Gestión de usuarios | Terminado |
| Registro y seguimiento de pedidos | Terminado |
| Panel de cocina | Terminado |
| Inventario de insumos y recetas | Terminado |
| Gestión de mesas | Capas de datos y negocio terminadas; interfaz pendiente |

Además de los módulos, este avance incorporó el registro de incidencias, el flujo de integración continua con pruebas automáticas, la publicación automatizada de la imagen y el despliegue en una plataforma en la nube.

**Avances anteriores:** APF1 (sesión, platos y usuarios) y APF2 (mesas, pedidos y control de acceso centralizado), ambos completados.

**Pendiente para el proyecto final:** interfaz de gestión de mesas y módulo de reportes.
