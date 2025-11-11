package com.morangosdoamor.WebCursos.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

/**
 * Configuração do Swagger/OpenAPI para documentação da API REST.
 * 
 * Define metadados da API, informações de licença e links externos.
 * A documentação interativa estará disponível em /swagger-ui.html após inicialização.
 */
@Configuration
public class SwaggerConfig {

    /**
     * Configura e retorna a definição OpenAPI da API WebCursos.
     * Define título, descrição, versão, licença e documentação externa.
     * 
     * @return Configuração OpenAPI completa da API
     */
    @Bean
    public OpenAPI webCursosOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("WebCursos API")
                .description("Catálogo de cursos com liberação automática conforme desempenho do aluno")
                .version("v1")
                .license(new License().name("Apache 2.0")))
            .externalDocs(new ExternalDocumentation()
                .description("Repositório do projeto")
                .url("https://github.com/morangosdoamor/WebCursos"));
    }
}
