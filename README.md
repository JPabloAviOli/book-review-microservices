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
- **Config Server** (con repositorio de GitHub)
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

Este proyecto implementa una comunicación efectiva entre los microservicios `book-ms` y `review-ms` utilizando REST y eventos con Kafka para mantener sincronizada la información.

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

### 📸 Ejemplos visuales

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

- **ID del libro asociado**
- **Tipo de evento:** `"RATING_UPDATE"`

El microservicio `book-ms`, al recibir este evento, realiza las siguientes acciones:

- Recalcula el `averageRating` del libro
- El `ratingCount` **no se modifica**, ya que la cantidad de reviews no cambió

📸 En la imagen se muestra la solicitud con el nuevo rating y la respuesta exitosa.

---
### 🗑️ 2. Eliminación de una review

- **Método:** `DELETE`
- **Ruta:** `/api/books/{id}`
- **Código de respuesta:** `204 No Content`

📸 En la imagen se muestra la solicitud y la respuesta exitosa.

![eliminar-review](https://github.com/user-attachments/assets/aab0055a-7b09-448b-916d-d1616eee4cd2)

Cuando se elimina una review, se envía un evento Kafka con:

- **ID del libro asociado**
- **Tipo de evento:** `"REVIEW_DELETED"`

El microservicio `book-ms` responde realizando:

- Recalculación del `averageRating`
- Disminución del `ratingCount` (ya que hay una review menos)

📸 La imagen muestra la ejecución del `DELETE`, la respuesta del servidor y el efecto reflejado al consultar el libro actualizado.

---


Esta arquitectura garantiza sincronización y consistencia entre microservicios manteniendo la independencia y escalabilidad de cada uno.


