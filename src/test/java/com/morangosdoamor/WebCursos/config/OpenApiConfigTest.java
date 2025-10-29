package com.morangosdoamor.WebCursos.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testes unitários para OpenApiConfig
 * 
 * Objetivo: garantir que a configuração do Swagger/OpenAPI
 * está corretamente definida com título, versão e descrição.
 */
@DisplayName("OpenApiConfig - Testes de Configuração")
class OpenApiConfigTest {

    @Test
    @DisplayName("Deve criar bean OpenAPI com informações corretas")
    void deveCriarBeanOpenAPIComInformacoesCorretas() {
        // Given
        OpenApiConfig config = new OpenApiConfig();
        
        // When
        OpenAPI openAPI = config.customOpenAPI();
        Info info = openAPI.getInfo();
        
        // Then
        assertThat(openAPI).isNotNull();
        assertThat(info).isNotNull();
        assertThat(info.getTitle()).isEqualTo("WebCursos API");
        assertThat(info.getVersion()).isEqualTo("1.0");
        assertThat(info.getDescription()).isEqualTo("API REST para gerenciamento de cursos e alunos");
    }

    @Test
    @DisplayName("Deve retornar bean válido com estrutura OpenAPI correta")
    void deveRetornarBeanValidoComEstruturaOpenAPICorreta() {
        // Given
        OpenApiConfig config = new OpenApiConfig();
        
        // When
        OpenAPI openAPI = config.customOpenAPI();
        
        // Then
        assertThat(openAPI).isInstanceOf(OpenAPI.class);
        assertThat(openAPI.getInfo()).isNotNull();
    }
}
