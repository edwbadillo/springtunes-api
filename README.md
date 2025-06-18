# SPRING TUNES API

**Spring Tunes** es un clon simplificado de Spotify, desarrollado como proyecto de portafolio backend con **Spring Boot + Angular**. Permite a los usuarios explorar, escuchar y organizar música, mientras integra un sistema de aprobación para artistas y contenido.

### 🛠 Tecnologías principales:

- **Backend**: Java + Spring Boot
- **Frontend**: Angular
- **Base de datos**: PostgreSQL
- **Autenticación y almacenamiento**: Firebase (Auth + Storage)

### 🔑 Funcionalidades clave:

- Autenticación segura mediante Firebase.
- Solicitud y aprobación de perfiles de artista.
- Subida y revisión de canciones antes de su publicación.
- Reproducción de audio vía streaming desde Firebase Storage.
- Creación de playlists, favoritos y sistema de roles (`USER`, `ARTIST`, `ADMIN`).

### 🚧 Arquitectura:

![spring tunes.jpg](spring%20tunes.jpg)

## 📝 Variables de entorno
| Variable        | Descripción                                                               | Valor por defecto |
| --------------- | ------------------------------------------------------------------------- | ---------------- |
| `DB_HOST`       | Dirección del host de la base de datos PostgreSQL.                        | `localhost`      |
| `DB_PORT`       | Puerto en el que PostgreSQL está escuchando.                              | `5432`           |
| `DB_NAME`       | Nombre de la base de datos a la que se conectará la aplicación.           | *(requerido)*    |
| `DB_USER`       | Usuario con permisos para acceder a la base de datos.                     | *(requerido)*    |
| `DB_PASSWD`     | Contraseña correspondiente al usuario de la base de datos.                | *(requerido)*    |
| `MAIL_HOST`     | Host del servidor SMTP para el envío de correos.                          | *(requerido)*    |
| `MAIL_PORT`     | Puerto del servidor SMTP.                                                 | *(requerido)*    |
| `MAIL_USER`     | Usuario (correo) que se usará para autenticar con el servidor SMTP.       | *(requerido)*    |
| `MAIL_PASSWORD` | Contraseña o token del usuario SMTP.                                      | *(requerido)*    |
| `SENDER_EMAIL`  | Dirección de correo que aparecerá como remitente en los correos enviados. | *(requerido)*    |

## 🔗 Integración con Firebase
Crea un archivo `serviceAccountKey.json` con las credenciales del proyecto de Firebase [más información](https://firebase.google.com/docs/admin/setup?hl=es-419#initialize-sdk). Este archivo debe estar ubicado en la carpeta `src/main/resources`.

Debes tener un proyecto de Firebase configurado y configurar las variables de entorno correspondientes. El proyecto utiliza el SDK de Firebase Admin para interactuar con Firebase Authentication.
Debes asociar el ID del usuario Firebase con el ID del usuario registrado en la base de datos.

## 📦 Base de datos
El proyecto no crea el esquema automáticamente (spring.jpa.hibernate.ddl-auto=validate). Asegúrate de tener las tablas creadas con el script SQL incluido antes de ejecutar la aplicación, recuerda asociar el ID del usuario Firebase con el ID del usuario registrado en la base de datos.