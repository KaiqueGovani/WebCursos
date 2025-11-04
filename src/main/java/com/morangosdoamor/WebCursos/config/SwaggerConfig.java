package com.morangosdoamor.WebCursos.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI webCursosOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("WebCursos API")
                .description("Catálogo de cursos com liberação automática conforme desempenho do aluno")
                .version("v1")
                .contact(new Contact().name("Equipe WebCursos").email("contato@webcursos.local"))
                .license(new License().name("Apache 2.0")))
            .externalDocs(new ExternalDocumentation()
                .description("Repositório do projeto")
                .url("https://github.com/morangosdoamor/WebCursos"));
    }
}
