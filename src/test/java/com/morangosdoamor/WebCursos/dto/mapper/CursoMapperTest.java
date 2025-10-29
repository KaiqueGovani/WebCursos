package com.morangosdoamor.WebCursos.dto.mapper;

import com.morangosdoamor.WebCursos.domain.Curso;
import com.morangosdoamor.WebCursos.domain.valueobject.CargaHoraria;
import com.morangosdoamor.WebCursos.dto.request.CursoRequestDTO;
import com.morangosdoamor.WebCursos.dto.request.CursoUpdateDTO;
import com.morangosdoamor.WebCursos.dto.response.CursoDetailResponseDTO;
import com.morangosdoamor.WebCursos.dto.response.CursoResponseDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes para CursoMapper
 * Valida conversões entre DTOs e Entidades
 */
@DisplayName("CursoMapper - Testes de Mapeamento")
class CursoMapperTest {
    
    @Test
    @DisplayName("Deve converter CursoRequestDTO para Curso")
    void deveConverterCursoRequestDTOParaCurso() {
        // Arrange
        CursoRequestDTO dto = CursoRequestDTO.builder()
            .nome("Java Avançado")
            .descricao("Curso de Java com Spring Boot")
            .cargaHoraria(80)
            .prerequisitos(new String[]{"Java Básico", "POO"})
            .build();
        
        // Act
        Curso curso = CursoMapper.toEntity(dto);
        
        // Assert
        assertNotNull(curso);
        assertNull(curso.getId());
        assertEquals("Java Avançado", curso.getNome());
        assertEquals("Curso de Java com Spring Boot", curso.getDescricao());
        assertEquals(80, curso.getCargaHoraria().getHoras());
        assertArrayEquals(new String[]{"Java Básico", "POO"}, curso.getPrerequisitos());
    }
    
    @Test
    @DisplayName("Deve converter Curso para CursoResponseDTO")
    void deveConverterCursoParaCursoResponseDTO() {
        // Arrange
        Curso curso = new Curso(
            "id-123",
            "Python para Data Science",
            "Análise de dados",
            new CargaHoraria(60),
            new String[]{"Python Básico"}
        );
        
        // Act
        CursoResponseDTO dto = CursoMapper.toResponseDTO(curso);
        
        // Assert
        assertNotNull(dto);
        assertEquals("id-123", dto.getId());
        assertEquals("Python para Data Science", dto.getNome());
        assertEquals("Análise de dados", dto.getDescricao());
        assertEquals(60, dto.getCargaHoraria());
    }
    
    @Test
    @DisplayName("Deve converter Curso para CursoDetailResponseDTO com conversões")
    void deveConverterCursoParaCursoDetailResponseDTO() {
        // Arrange
        Curso curso = new Curso(
            "id-456",
            "React Native",
            "Desenvolvimento mobile",
            new CargaHoraria(40),
            new String[]{"JavaScript", "React"}
        );
        
        // Act
        CursoDetailResponseDTO dto = CursoMapper.toDetailResponseDTO(curso);
        
        // Assert
        assertNotNull(dto);
        assertEquals("id-456", dto.getId());
        assertEquals("React Native", dto.getNome());
        assertEquals(40, dto.getCargaHoraria());
        assertEquals(5.0, dto.getCargaHorariaEmDias()); // 40h / 8h = 5 dias
        assertEquals(1.0, dto.getCargaHorariaEmSemanas()); // 40h / 40h = 1 semana
        assertArrayEquals(new String[]{"JavaScript", "React"}, dto.getPrerequisitos());
    }
    
    @Test
    @DisplayName("Deve atualizar entidade a partir de CursoUpdateDTO com todos os campos")
    void deveAtualizarEntidadeComTodosOsCampos() {
        // Arrange
        Curso curso = new Curso(
            "id-789",
            "Nome Original",
            "Descrição Original",
            new CargaHoraria(30),
            new String[]{"Pre1"}
        );
        
        CursoUpdateDTO dto = CursoUpdateDTO.builder()
            .nome("Nome Atualizado")
            .descricao("Descrição Atualizada")
            .cargaHoraria(50)
            .prerequisitos(new String[]{"Pre2", "Pre3"})
            .build();
        
        // Act
        CursoMapper.updateEntityFromDto(curso, dto);
        
        // Assert
        assertEquals("Nome Atualizado", curso.getNome());
        assertEquals("Descrição Atualizada", curso.getDescricao());
        assertEquals(50, curso.getCargaHoraria().getHoras());
        assertArrayEquals(new String[]{"Pre2", "Pre3"}, curso.getPrerequisitos());
    }
    
    @Test
    @DisplayName("Deve atualizar apenas nome quando outros campos são null")
    void deveAtualizarApenasNomeQuandoOutrosCamposSaoNull() {
        // Arrange
        Curso curso = new Curso(
            "id-abc",
            "Nome Original",
            "Descrição Original",
            new CargaHoraria(25),
            null
        );
        
        CursoUpdateDTO dto = CursoUpdateDTO.builder()
            .nome("Nome Atualizado")
            .build();
        
        // Act
        CursoMapper.updateEntityFromDto(curso, dto);
        
        // Assert
        assertEquals("Nome Atualizado", curso.getNome());
        assertEquals("Descrição Original", curso.getDescricao());
        assertEquals(25, curso.getCargaHoraria().getHoras());
    }
    
    @Test
    @DisplayName("Deve atualizar apenas descrição quando outros campos são null")
    void deveAtualizarApenasDescricaoQuandoOutrosCamposSaoNull() {
        // Arrange
        Curso curso = new Curso(
            "id-def",
            "Nome Original",
            "Descrição Original",
            new CargaHoraria(35),
            null
        );
        
        CursoUpdateDTO dto = CursoUpdateDTO.builder()
            .descricao("Descrição Atualizada")
            .build();
        
        // Act
        CursoMapper.updateEntityFromDto(curso, dto);
        
        // Assert
        assertEquals("Nome Original", curso.getNome());
        assertEquals("Descrição Atualizada", curso.getDescricao());
        assertEquals(35, curso.getCargaHoraria().getHoras());
    }
    
    @Test
    @DisplayName("Deve atualizar apenas carga horária quando outros campos são null")
    void deveAtualizarApenasCargaHorariaQuandoOutrosCamposSaoNull() {
        // Arrange
        Curso curso = new Curso(
            "id-ghi",
            "Nome Original",
            "Descrição Original",
            new CargaHoraria(20),
            null
        );
        
        CursoUpdateDTO dto = CursoUpdateDTO.builder()
            .cargaHoraria(100)
            .build();
        
        // Act
        CursoMapper.updateEntityFromDto(curso, dto);
        
        // Assert
        assertEquals("Nome Original", curso.getNome());
        assertEquals("Descrição Original", curso.getDescricao());
        assertEquals(100, curso.getCargaHoraria().getHoras());
    }
    
    @Test
    @DisplayName("Deve atualizar apenas pré-requisitos quando outros campos são null")
    void deveAtualizarApenasPreRequisitosQuandoOutrosCamposSaoNull() {
        // Arrange
        Curso curso = new Curso(
            "id-jkl",
            "Nome Original",
            "Descrição Original",
            new CargaHoraria(45),
            new String[]{"Old"}
        );
        
        CursoUpdateDTO dto = CursoUpdateDTO.builder()
            .prerequisitos(new String[]{"New1", "New2"})
            .build();
        
        // Act
        CursoMapper.updateEntityFromDto(curso, dto);
        
        // Assert
        assertEquals("Nome Original", curso.getNome());
        assertEquals("Descrição Original", curso.getDescricao());
        assertEquals(45, curso.getCargaHoraria().getHoras());
        assertArrayEquals(new String[]{"New1", "New2"}, curso.getPrerequisitos());
    }

    @Test
    @DisplayName("Deve converter Curso com CargaHoraria null para DTO")
    void deveConverterCursoComCargaHorariaNullParaDTO() {
        // Arrange
        Curso curso = new Curso(
            "id-999",
            "Curso Sem Carga",
            "Descrição do Curso",
            null, // CargaHoraria null
            new String[]{"Req1"}
        );
        
        // Act
        CursoResponseDTO dto = CursoMapper.toResponseDTO(curso);
        
        // Assert
        assertNotNull(dto);
        assertEquals("id-999", dto.getId());
        assertEquals("Curso Sem Carga", dto.getNome());
        assertNull(dto.getCargaHoraria()); // CargaHoraria deve ser null
    }

    @Test
    @DisplayName("Deve converter Curso com CargaHoraria null para DetailDTO")
    void deveConverterCursoComCargaHorariaNullParaDetailDTO() {
        // Arrange
        Curso curso = new Curso(
            "id-888",
            "Curso Incompleto",
            "Descrição",
            null, // CargaHoraria null
            null
        );
        
        // Act
        CursoDetailResponseDTO dto = CursoMapper.toDetailResponseDTO(curso);
        
        // Assert
        assertNotNull(dto);
        assertEquals("id-888", dto.getId());
        assertEquals("Curso Incompleto", dto.getNome());
        assertNull(dto.getCargaHoraria());
        assertNull(dto.getCargaHorariaEmDias());
        assertNull(dto.getCargaHorariaEmSemanas());
    }

    @Test
    @DisplayName("Deve retornar null quando RequestDTO for null")
    void deveRetornarNullQuandoRequestDTOForNull() {
        // Act
        Curso curso = CursoMapper.toEntity(null);
        
        // Assert
        assertNull(curso);
    }

    @Test
    @DisplayName("Deve retornar null quando Curso for null ao converter para ResponseDTO")
    void deveRetornarNullQuandoCursoForNullAoConverterParaResponseDTO() {
        // Act
        CursoResponseDTO dto = CursoMapper.toResponseDTO(null);
        
        // Assert
        assertNull(dto);
    }

    @Test
    @DisplayName("Deve retornar null quando Curso for null ao converter para DetailDTO")
    void deveRetornarNullQuandoCursoForNullAoConverterParaDetailDTO() {
        // Act
        CursoDetailResponseDTO dto = CursoMapper.toDetailResponseDTO(null);
        
        // Assert
        assertNull(dto);
    }

    @Test
    @DisplayName("Não deve fazer nada quando UpdateDTO for null")
    void naoDeveFazerNadaQuandoUpdateDTOForNull() {
        // Arrange
        Curso curso = new Curso(
            "id-original",
            "Nome Original",
            "Descrição Original",
            new CargaHoraria(30),
            new String[]{"Req1"}
        );
        
        // Act
        CursoMapper.updateEntityFromDto(curso, null);
        
        // Assert - Valores devem permanecer inalterados
        assertEquals("Nome Original", curso.getNome());
        assertEquals("Descrição Original", curso.getDescricao());
        assertEquals(30, curso.getCargaHoraria().getHoras());
        assertArrayEquals(new String[]{"Req1"}, curso.getPrerequisitos());
    }
}
