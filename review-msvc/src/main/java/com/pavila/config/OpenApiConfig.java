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
                title = "Review Microservice API",
                description = "RESTful API for review management operations within the Review Microservice.",
                version = "v1",
                license = @License(
                        name = "MIT License",
                        url = "https://opensource.org/licenses/MIT"
        )

        ),

        servers = {
                @Server(
                        description = "Local",
                        url = "http://localhost:8001"
                )
        },
        externalDocs = @ExternalDocumentation(
                description =  "Review microservice REST API Documentation",
                url = "https://github.com/JPabloAviOli/book-review-microservices/blob/main/README.md"
        )
)
public class OpenApiConfig {
}
