# 📚 Book & Review Microservices

Microservicio de gestión de libros y reseñas desarrollado con Spring Boot. Proyecto de práctica para aplicar arquitectura de microservicios, comunicación entre servicios y buenas prácticas de desarrollo backend.

Este proyecto es un sistema de ejemplo basado en microservicios para la gestión de libros (`book-ms`) y sus reseñas (`review-ms`). Fue desarrollado con fines educativos y de práctica para aplicar los principios de arquitectura de microservicios con Java y Spring Boot.

## 🧱 Arquitectura General

Este sistema está compuesto por los siguientes servicios:

| Microservicio     | Descripción                                              |
|-------------------|----------------------------------------------------------|
| `book-ms`         | Gestión de libros: título, autor, año, etc.              |
| `review-ms`       | Gestión de reseñas relacionadas a libros                 |
| `gateway`         | API Gateway que enruta las solicitudes                   |
| `config-server`   | Proporciona configuración centralizada desde GitHub      |
| `eureka-server`   | Registro y descubrimiento de servicios                   |
| `message-broker`  | Manejo de eventos con Kafka (functions)                  |
| `security` (Keycloak) | Seguridad basada en el estandar OAuth 2.0            |

---

## 🚀 Tecnologías utilizadas

- **Java 17**
- **Spring Boot**
- **Spring Cloud**
- **Spring Security + Keycloak + OAuth2.0**
- **Feign Client**
- **Eureka Server**
- **Spring Cloud Config Server** con conexión al repositorio de GitHub vía **SSH**
- **Apache Kafka** (event-driven communication con funciones)
- **Spring Cloud Gateway**
- **Spring Data JPA**
- **Validaciones** en los DTOs usando `@Valid` y anotaciones como `@NotNull`, `@Size`, etc.
- **Manejo global de errores** con `@RestControllerAdvice` para respuestas consistentes, centralizadas y personalizadas.
- **Manejo adecuado de códigos HTTP** según el contexto (`200 OK`, `201 Created`, `400 Bad Request`, `404 Not Found`, `500 Internal Server Error`, etc.)
- **Actuator** (monitoring endpoints)
- **OpenAPI / Swagger**
- **Lombok / Java Records**
- **DTOs + Mappers sin librerías externas**
- **Paginación**
- **Docker + Jib** (Google plugin para generar imágenes Docker)
- **Base de datos H2 en memoria** (por rendimiento en entorno local)

---

## 🛠️ Funcionalidades implementadas

- Comunicación entre microservicios usando **Feign Client**
- Seguridad **OAuth2.0 con JWT y roles**
- Configuración centralizada con **Spring Cloud Config**
- Descubrimiento dinámico con **Eureka**
- Eventos asincrónicos entre microservicios con **Kafka**
- Separación clara por capas (**Controller, Service, DTO, Mapper, Repository**)
- Documentación automática con **Swagger**
- Contenedores dockerizados con **Jib** para facilitar despliegue

## 🔐 Flujos de autenticación OAuth 2.0 utilizados

Este sistema implementa distintos tipos de flujo OAuth 2.0 según el caso de uso:

| Grant Type              | Uso específico                                      |
|-------------------------|-----------------------------------------------------|
| `client_credentials`    | Comunicación entre microservicios internos (APIs)   |
| `authorization_code`    | Aplicaciones backend confidenciales (con secret)    |
| `authorization_code + PKCE` | Aplicaciones públicas como Angular, React o móviles (sin secret) |

Cada flujo fue configurado en Keycloak y probado con postman.  
El uso de PKCE permite mayor seguridad en aplicaciones frontend, ya que evita el envío del `client_secret` y valida la autenticidad del cliente mediante `code_verifier` y `code_challenge`.

## 🔄 Comunicación y eventos entre microservicios

Este proyecto implementa una comunicación efectiva entre los microservicios `book-ms` y `review-ms` utilizando tanto:

- **REST** (mediante Feign Client) para llamadas síncronas
- **Eventos asíncronos con Kafka** para mantener sincronizada la información (por ejemplo, al crear, actualizar o eliminar una review)

🛠️ Para desacoplar la lógica de mensajería, se trabajó con **funciones (Spring Cloud Function)** en lugar de depender directamente del cliente de Kafka.  
Esto permite:

- Cambiar fácilmente entre **Kafka** y otros brokers como **RabbitMQ**
- Preparar el sistema para un posible despliegue en entornos como **AWS Lambda** o **AWS EventBridge** sin necesidad de reescribir la lógica de negocio

Esta arquitectura mejora la mantenibilidad, escalabilidad y portabilidad del sistema.

### Flujo de eventos y acciones

- **Crear una review**  
  Al crear una review, se genera un evento Kafka que contiene el ID del libro asociado. Este evento es consumido por `book-ms`, que actualiza:  
  - `ratingCount` (número total de reseñas)  
  - `averageRating` (promedio de calificaciones)

- **Modificar una review**  
  Si se modifica una review, solo se envía un evento si el **rating** cambió. Si solo se modificó el comentario, no se envía evento. Al recibir el evento, `book-ms` recalcula únicamente el `averageRating`, ya que el `ratingCount` no cambia.

- **Eliminar una review**  
  Al eliminar una review, se envía un evento con el ID del libro y la acción realizada. `book-ms` actualiza su información de reseñas para ese libro.

### Uso de Feign Client

- Para traer la información completa de un libro junto con sus reviews, `book-ms` utiliza **Feign Client** para hacer llamadas REST a `review-ms`, facilitando la agregación de datos sin exponer la complejidad interna.

---

### 🔐 Acceso al Config Server

Este proyecto utiliza **Spring Cloud Config Server**, el cual se conecta a un repositorio privado de GitHub mediante autenticación **SSH**.

> ⚠️ Por razones de seguridad, la clave privada (`privateKey`) ha sido removida del proyecto y **no debe colocarse directamente** en el archivo `application.yml`.

📦 En entornos productivos, esta clave debe manejarse de forma segura usando:

- **Variables de entorno** (ej. `SPRING_CLOUD_CONFIG_SERVER_GIT_PRIVATE_KEY`)
- O mediante un **Vault** seguro (como **HashiCorp Vault**, **AWS Secrets Manager**, **Docker secrets**, etc.)

Esto garantiza que las credenciales sensibles no se expongan en el código ni en repositorios públicos.  
En este proyecto, se deja una plantilla simulada como demostración de cómo se configuraría.

```yaml

# Ejemplo ilustrativo (no funcional sin la clave)
spring:
  cloud:
    config:
      server:
        git:
          uri: git@github.com:tuusuario/tu-repo-config.git
          private-key: |
            -----BEGIN RSA PRIVATE KEY----- 
            [REMOVIDA]
            -----END RSA PRIVATE KEY-----
```

📌 Si deseas ejecutar el proyecto por completo, puedes reemplazar esa parte con tu propia clave privada o adaptar el acceso al repositorio con HTTPS y token personal.

## 📸 Ejemplos visuales

En esta sección se agregarán imágenes de:

- Creación de un libro
- Creación de una review y envío de evento Kafka
- Actualización condicional del rating y evento
- Eliminación de review y evento
- Llamadas Feign Client para obtener detalles completos

---

## 📘 Crear libro (POST /api/books)

Este endpoint permite registrar un nuevo libro en el sistema.

- **Método:** `POST`
- **Ruta:** `/api/books`
- **Código de respuesta:** `201 Created`
- **Mensaje:** `Book created successfully`

📸 **En la siguiente imagen se muestra:**
- El JSON enviado en el cuerpo de la solicitud (`request body`)
- La respuesta exitosa del servidor (`statusCode 201` y mensaje)

#### 🖼️ Captura:

![crear-libro](https://github.com/user-attachments/assets/7c0c482a-f614-4e2e-a3a1-2aae935798d8)

## 📘 Manejo de mensajes de error personalizados con Validation al crear un libro(Book) o una Calificación(Review)

Al enviar datos inválidos al crear un libro, el sistema valida automáticamente el contenido del cuerpo (`request body`) mediante anotaciones de **Bean Validation** (`@NotBlank`, `@Size`, etc.) en los DTOs.

Cuando ocurre un error de validación, se lanza una excepción controlada que es interceptada por un manejador global, el cual retorna una respuesta estructurada y clara al cliente.

🔧 Este manejo personalizado de errores:

- Utiliza `@RestControllerAdvice` y `@ExceptionHandler` para centralizar la lógica de excepciones
- Retorna un JSON con mensaje de error, campos inválidos y códigos HTTP apropiados
- Mejora la experiencia del cliente (por ejemplo: aplicaciones frontend como Angular)

📸 En la imagen se muestra un ejemplo de error al enviar un título vacío al crear un libro. La respuesta contiene:

- `statusCode: 400 Bab Request`

![validacion](https://github.com/user-attachments/assets/81337275-e1ef-4c57-b856-8aa00652a79d)

## 📘 Crear review (POST /api/reviews)

Este endpoint permite registrar una review a un libro en el sistema mediante el id del libro.

- **Método:** `POST`
- **Ruta:** `/api/reviews`
- **Código de respuesta:** `201 Created`
- **Mensaje:** `Review created successfully`

📸 **En la siguiente imagen se muestra:**
- El JSON enviado en el cuerpo de la solicitud (`request body`)
- La respuesta exitosa del servidor (`statusCode 201` y mensaje)

#### 🖼️ Captura:

![crear-review](https://github.com/user-attachments/assets/28a9aaf4-1b7c-42bf-a5a0-47c3e4ac072e)

📬 De forma asíncrona, al crear una nueva review se produce un evento en Kafka con el `ID` del libro y el tipo de evento: `"Review Created"`.

El microservicio `book-ms` escucha este evento, localiza el libro correspondiente y obtiene todas sus reviews consultando al `review-ms`. Luego:

- Calcula la cantidad total de calificaciones (`reviewCount`)
- Calcula el promedio de las calificaciones (`averageRating`)
- Asigna ambos valores al libro y lo actualiza

Este proceso asegura que cada vez que se crea una review, se actualicen automáticamente los datos agregados del libro sin acoplar directamente los microservicios.

## 📖 Obtener libro con sus reviews (GET /api/books/{id})

Este endpoint permite obtener la información detallada de un libro, incluyendo sus calificaciones y comentarios (`reviews`), mediante su `id`.

Para lograrlo, el microservicio `book-ms` utiliza **comunicación síncrona** con el microservicio `review-ms` a través de **Feign Client**. De esta forma, puede agregar a la respuesta del libro sus respectivas reviews sin duplicar lógica ni acceder directamente a la base de datos del otro servicio.

- **Método:** `GET`
- **Ruta:** `/api/books/{id}`
- **Código de respuesta exitosa:** `200 OK`

📸 **En la siguiente imagen se muestra:**
- La respuesta exitosa del servidor (`statusCode 200 OK`)

#### 🖼️ Captura:
![toda-lainfo-del libro](https://github.com/user-attachments/assets/b5838836-fabd-4cde-8a6e-5fc252778f75)

---

## ✏️ Actualización de recursos (PUT)

### 📝 1. Actualizar solo el comentario de una review

Este endpoint permite modificar únicamente el comentario de una review existente.  
En este caso, como el `rating` no se modifica, **no se genera ningún evento** ni se actualiza la información del libro relacionado.

- **Endpoint:** `PUT /api/reviews/{id}`
- **Acción:** Se actualiza el `comment` de la review con `id = 3`, manteniendo el mismo `rating`.

📸 A continuación se muestra el cuerpo de la solicitud enviado y la respuesta exitosa del servidor.

![actualizar-comentario](https://github.com/user-attachments/assets/ad01fdaa-53bd-48b1-8cf9-ab33ab69175d)

---

### 📗 2. Actualizar el título de un libro

Este endpoint permite modificar los datos de un libro, por ejemplo su `title`.  
En este caso, se actualiza el libro con `id = 1`.

- **Endpoint:** `PUT /api/books/{id}`
- **Acción:** Se actualiza el `title`, conservando el autor y el año.

📸 La imagen muestra el request body enviado y la respuesta con estado `200 OK`.

![actualizar-libro](https://github.com/user-attachments/assets/dd89ec27-67cb-4cae-bbd1-f740fbbbb4a7)

---
### ✅ Resultado de los cambios aplicados

Después de realizar las actualizaciones, al consultar los recursos modificados se reflejan correctamente los nuevos valores.

- El **comentario de la review con ID 3** fue actualizado exitosamente.
- El **título del libro con ID 1** muestra el nuevo valor asignado.

📸 A continuación se muestran las respuestas obtenidas al hacer una consulta (`GET`) después de los cambios.

![todo-comentario-actualizado](https://github.com/user-attachments/assets/8c2c23ba-8c46-4d10-9fc8-862768881dbb)

## 🔍 3. Búsqueda de libros mediante paginación

Para localizar recursos (libros) se utiliza el endpoint de paginación que recibe parametros.

- **Endpoint:** `GET /api/reviews?p=0&sz=5`
- **Descripción:** Permite obtener un conjunto de elementos paginados.  
  La respuesta incluye un objeto DTO con los campos: `content`, `pageNumber`, `pageSize`, `totalElements`, `totalPages` etc.

📸 En la imagen se muestra una respuesta con una lista paginada de libros.

![paginacion](https://github.com/user-attachments/assets/4b9418ec-bb97-4bb8-8683-e83f7f1c06bd)

## 🔁 Actualización y eliminación de review (con eventos Kafka)

### 🎯 1. Actualización del rating de una review

Cuando se modifica el `rating` de una review existente, se dispara un evento Kafka con la siguiente información:

![Captura de Pantalla 2025-07-09 a la(s) 19 39 14](https://github.com/user-attachments/assets/67bb9cbb-e02d-4890-a999-26a279e2a9d2)

- **ID del libro asociado**
- **Tipo de evento:** `"RATING_UPDATE"`

El microservicio `book-ms`, al recibir este evento, realiza las siguientes acciones:

- Recalcula el `averageRating` del libro
- El `ratingCount` **no se modifica**, ya que la cantidad de reviews no cambió

📸 En la imagen se muestra la solicitud con el nuevo rating y la respuesta exitosa.

![Captura de Pantalla 2025-07-09 a la(s) 19 39 34](https://github.com/user-attachments/assets/f275a1a2-1942-43f2-8e5a-250bf1b6634e)

---

### 🗑️ 2. Eliminación de una review

- **Método:** `DELETE`
- **Ruta:** `/api/books/{id}`
- **Código de respuesta:** `204 No Content`

📸 En la imagen se muestra la solicitud y la respuesta exitosa.

![Captura de Pantalla 2025-07-09 a la(s) 19 40 28](https://github.com/user-attachments/assets/fb09916a-a3ac-4d38-b71b-2287cf63e03c)

Cuando se elimina una review, se envía un evento Kafka con:

- **ID del libro asociado**
- **Tipo de evento:** `"REVIEW_DELETED"`

El microservicio `book-ms` responde realizando:

- Recalculación del `averageRating`
- Disminución del `ratingCount` (ya que hay una review menos)

📸 La imagen muestra la ejecución del `DELETE`, la respuesta del servidor y el efecto reflejado al consultar el libro actualizado.

![Captura de Pantalla 2025-07-09 a la(s) 19 41 06](https://github.com/user-attachments/assets/be2e5215-6821-42e2-957d-359820fa996f)

---

## 📚 Documentación Swagger (OpenAPI)

Cada microservicio expone su propia documentación OpenAPI mediante Swagger, lo que facilita la visualización y prueba de los endpoints directamente desde el navegador.

### 🔌 Acceso a Swagger

Para efectos de demostración, en este proyecto se expusieron los puertos HTTP de los microservicios `book-ms` y `review-ms` en el archivo `docker-compose.yml`, permitiendo acceder a sus interfaces Swagger desde el navegador:

- `http://localhost:9090/swagger-ui.html` → Book Service
- `http://localhost:9091/swagger-ui.html` → Review Service

📸 A continuación se muestran las capturas de ambas interfaces:

- `book-ms`: muestra endpoints para crear, listar, actualizar, eliminar y obtener libros

![Captura de Pantalla 2025-07-06 a la(s) 19 08 26](https://github.com/user-attachments/assets/70c59b21-9338-4979-b275-f2cfd89459a0)
![Captura de Pantalla 2025-07-06 a la(s) 19 09 06](https://github.com/user-attachments/assets/01b25167-b65b-4615-8f0f-95ece032642d)

- `review-ms`: permite crear, actualizar, listar y eliminar reviews

![Captura de Pantalla 2025-07-06 a la(s) 19 41 24](https://github.com/user-attachments/assets/ee5f4cca-b3a0-4a85-bdca-535a96c4de09)
![Captura de Pantalla 2025-07-06 a la(s) 19 41 35](https://github.com/user-attachments/assets/9f8c343c-7fdc-4973-9741-2d35989263e3)

### 📦 Nota técnica

En una arquitectura real con microservicios en producción, **no es necesario exponer los puertos individuales de cada servicio**.  
La comunicación entre servicios se realiza internamente entre contenedores mediante **Docker networks** o un **Service Discovery** como Eureka, y los clientes acceden a través de un **API Gateway** centralizado.

En este caso, los puertos fueron expuestos únicamente para mostrar Swagger y facilitar las pruebas locales.

---
Esta arquitectura garantiza sincronización y consistencia entre microservicios manteniendo la independencia y escalabilidad de cada uno.


