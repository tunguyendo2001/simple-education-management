package com.example.studentapi.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {
    @Value("${server.address:0.0.0.0}")
    private String address;

    @Value("${server.port:8080}")
    private String port;

    @Bean
    public OpenAPI myOpenAPI() {

        String apiUrl = String.format("http://%s:%s", address, port);
        System.out.println(apiUrl);

        Server devServer = new Server()
                .url(apiUrl)
                .description("Server URL in Development environment");

        Contact contact = new Contact()
                .email("support@education.com")
                .name("Education Department API Support")
                .url("https://education.com");

        License mitLicense = new License()
                .name("MIT License")
                .url("https://choosealicense.com/licenses/mit/");

        Info info = new Info()
                .title("Education Department API Documentation")
                .version("1.0")
                .contact(contact)
                .description("This API exposes endpoints for managing students, teachers, and scores.")
                .license(mitLicense);

        // Security Scheme: Bearer JWT
        SecurityScheme bearerAuth = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name("Authorization");

        // Gắn scheme vào OpenAPI
        return new OpenAPI()
                .info(info)
                .servers(List.of(devServer))
                .components(new Components().addSecuritySchemes("bearerAuth", bearerAuth))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}
