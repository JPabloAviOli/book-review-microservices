package com.pavila.config;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(

        info = @Info(
                contact = @Contact(
                        name = "J. Pablo Avila",
                        //email = ""
                        url = "https://www.linkedin.com/in/pablo-avila-olivar/"

                ),
                title = "Book Microservice API",
                description = "RESTful API for book management operations within the Book Microservice.",
                version = "v1",
                license = @License(
                        name = "MIT License",
                        url = "https://opensource.org/licenses/MIT"
        )

        ),

        servers = {
                @Server(
                        description = "Local",
                        url = "http://localhost:8080"
                )
        },
        externalDocs = @ExternalDocumentation(
                description =  "Book microservice REST API Documentation",
                url = "https://github.com/JPabloAviOli/book-review-microservices/blob/main/README.md"
        )
)
public class OpenApiConfig {
}
