# SPRING TUNES API

**Spring Tunes** es un clon simplificado de Spotify, desarrollado como proyecto de portafolio backend con **Spring Boot**. Permite a los usuarios explorar, escuchar y organizar música, mientras integra un sistema de aprobación para artistas y contenido.

### 🛠 Tecnologías principales:

- **Backend**: Java 21, Spring Boot 3.5, API Docs (Swagger)
- **Frontend**: Cliente web con Angular 20
- **Base de datos**: PostgreSQL
- **Docker**: Contenedores Docker para despliegue de los servicios Spring Boot y PostgreSQL
- **AWS**: 
  - Cognito: Autenticación de usuarios.
  - S3: Almacenamiento de archivos.
  - Lambda: Funciones de backend como correos y generación de enlaces pre firmados para S3.
  - API Gateway: Enrutamiento seguro para comunicarse con las lambdas y otros servicios.

### 🔑 Funcionalidades:

- Registro y autenticación segura mediante AWS Cognito.
- Solicitud y aprobación de perfiles de artista.
- Subida y revisión de canciones antes de su publicación.
- Reproducción de audio vía streaming.
- Creación de playlists, favoritos y sistema de roles (`USER`, `ARTIST`, `ADMIN`).

## 📝 Checklist
Revisar las issues de GitHub para ver las tareas pendientes.

### 🚧 Arquitectura:

![spring tunes.jpg](spring_tunes.jpg)

## 📝 Variables de entorno
| Variable        | Descripción                                                               | Valor por defecto |
| --------------- | ------------------------------------------------------------------------- | ---------------- |
| `DB_HOST`       | Dirección del host de la base de datos PostgreSQL.                        | `localhost`      |
| `DB_PORT`       | Puerto en el que PostgreSQL está escuchando.                              | `5432`           |
| `DB_NAME`       | Nombre de la base de datos a la que se conectará la aplicación.           | *(requerido)*    |
| `DB_USER`       | Usuario con permisos para acceder a la base de datos.                     | *(requerido)*    |
| `DB_PASSWD`     | Contraseña correspondiente al usuario de la base de datos.                | *(requerido)*    |

## 📦 Base de datos
El proyecto no crea el esquema automáticamente (spring.jpa.hibernate.ddl-auto=validate). Para ello debe crear la base de datos y el usuario con los permisos adecuados.

```sql
CREATE DATABASE mydb;
CREATE USER myuser WITH PASSWORD 'mypassword’;
GRANT ALL PRIVILEGES ON DATABASE mydb TO myuser;
```

Evita problemas de permisos con el esquema public

```sql
ALTER DATABASE mydb OWNER TO myuser;
```

Ejecute el script `database.sql` para crear las tablas y el esquema de la base de datos.  Posteriormente debe insertar un usuario cuyo ID sea el UUID de un usuario de AWS Cognito.

### Docker para base de datos
Alternativamente, puedes usar Docker para desplegar la base de datos PostgreSQL. Ejecute el siguiente comando para crear un contenedor:

```bash
docker run --name springtunes_database -e POSTGRES_PASSWORD=mypassword -e POSTGRES_USER=myuser -e POSTGRES_DB=mydb -p 5432:5432 -d postgres:16-alpine
```

Luego copiar el archivo `database.sql` al contenedor:

```bash
docker cp database.sql springtunes_database:/
```

Ejecutar el script `database.sql` dentro del contenedor:

```bash
docker exec springtunes_database psql -U myuser -d mydb -f /database.sql
``` 

## 📄 Swagger
El proyecto incluye Swagger para la documentación de la API. Puedes acceder a la interfaz de Swagger en la siguiente URL:

```
http://localhost:8080/swagger-ui.html
```