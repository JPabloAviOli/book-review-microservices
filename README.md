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

Esta arquitectura garantiza sincronización y consistencia entre microservicios manteniendo la independencia y escalabilidad de cada uno.


