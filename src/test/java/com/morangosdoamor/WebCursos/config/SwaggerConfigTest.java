package com.morangosdoamor.WebCursos.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import io.swagger.v3.oas.models.OpenAPI;

class SwaggerConfigTest {

    @Test
    void deveConstruirOpenApiComInformacoesBasicas() {
        SwaggerConfig config = new SwaggerConfig();
        OpenAPI openAPI = config.webCursosOpenAPI();

        assertThat(openAPI.getInfo().getTitle()).isEqualTo("WebCursos API");
        assertThat(openAPI.getExternalDocs().getUrl()).contains("github.com");
    }
}
