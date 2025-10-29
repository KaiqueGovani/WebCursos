package com.morangosdoamor.WebCursos.dto.mapper;

import com.morangosdoamor.WebCursos.domain.Aluno;
import com.morangosdoamor.WebCursos.domain.valueobject.Email;
import com.morangosdoamor.WebCursos.domain.valueobject.Matricula;
import com.morangosdoamor.WebCursos.dto.request.AlunoRequestDTO;
import com.morangosdoamor.WebCursos.dto.request.AlunoUpdateDTO;
import com.morangosdoamor.WebCursos.dto.response.AlunoDetailResponseDTO;
import com.morangosdoamor.WebCursos.dto.response.AlunoResponseDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes para AlunoMapper
 * Valida conversões entre DTOs e Entidades
 */
@DisplayName("AlunoMapper - Testes de Mapeamento")
class AlunoMapperTest {
    
    @Test
    @DisplayName("Deve converter AlunoRequestDTO para Aluno")
    void deveConverterAlunoRequestDTOParaAluno() {
        // Arrange
        AlunoRequestDTO dto = AlunoRequestDTO.builder()
            .nome("João Silva")
            .email("joao@exemplo.com")
            .matricula("MAT12345")
            .build();
        
        // Act
        Aluno aluno = AlunoMapper.toEntity(dto);
        
        // Assert
        assertNotNull(aluno);
        assertNull(aluno.getId()); // ID não deve ser setado
        assertEquals("João Silva", aluno.getNome());
        assertEquals("joao@exemplo.com", aluno.getEmail().getEmail());
        assertEquals("MAT12345", aluno.getMatricula().getMatricula());
    }
    
    @Test
    @DisplayName("Deve converter Aluno para AlunoResponseDTO")
    void deveConverterAlunoParaAlunoResponseDTO() {
        // Arrange
        Aluno aluno = new Aluno(
            "id-123",
            "Maria Santos",
            new Email("maria@exemplo.com"),
            new Matricula("MAT54321")
        );
        
        // Act
        AlunoResponseDTO dto = AlunoMapper.toResponseDTO(aluno);
        
        // Assert
        assertNotNull(dto);
        assertEquals("id-123", dto.getId());
        assertEquals("Maria Santos", dto.getNome());
        assertEquals("maria@exemplo.com", dto.getEmail());
        assertEquals("MAT54321", dto.getMatricula());
    }
    
    @Test
    @DisplayName("Deve converter Aluno para AlunoDetailResponseDTO")
    void deveConverterAlunoParaAlunoDetailResponseDTO() {
        // Arrange
        Aluno aluno = new Aluno(
            "id-456",
            "Pedro Oliveira",
            new Email("pedro@exemplo.com"),
            new Matricula("MAT99999")
        );
        
        // Act
        AlunoDetailResponseDTO dto = AlunoMapper.toDetailResponseDTO(aluno);
        
        // Assert
        assertNotNull(dto);
        assertEquals("id-456", dto.getId());
        assertEquals("Pedro Oliveira", dto.getNome());
        assertEquals("pedro@exemplo.com", dto.getEmail());
        assertEquals("MAT99999", dto.getMatricula());
    }
    
    @Test
    @DisplayName("Deve atualizar entidade a partir de AlunoUpdateDTO com todos os campos")
    void deveAtualizarEntidadeComTodosOsCampos() {
        // Arrange
        Aluno aluno = new Aluno(
            "id-789",
            "Nome Original",
            new Email("original@exemplo.com"),
            new Matricula("MAT00000")
        );
        
        AlunoUpdateDTO dto = AlunoUpdateDTO.builder()
            .nome("Nome Atualizado")
            .email("atualizado@exemplo.com")
            .matricula("MAT11111")
            .build();
        
        // Act
        AlunoMapper.updateEntityFromDto(aluno, dto);
        
        // Assert
        assertEquals("Nome Atualizado", aluno.getNome());
        assertEquals("atualizado@exemplo.com", aluno.getEmail().getEmail());
        assertEquals("MAT11111", aluno.getMatricula().getMatricula());
    }
    
    @Test
    @DisplayName("Deve atualizar apenas nome quando outros campos são null")
    void deveAtualizarApenasNomeQuandoOutrosCamposSaoNull() {
        // Arrange
        Aluno aluno = new Aluno(
            "id-abc",
            "Nome Original",
            new Email("original@exemplo.com"),
            new Matricula("MAT22222")
        );
        
        AlunoUpdateDTO dto = AlunoUpdateDTO.builder()
            .nome("Nome Atualizado")
            .build();
        
        // Act
        AlunoMapper.updateEntityFromDto(aluno, dto);
        
        // Assert
        assertEquals("Nome Atualizado", aluno.getNome());
        assertEquals("original@exemplo.com", aluno.getEmail().getEmail());
        assertEquals("MAT22222", aluno.getMatricula().getMatricula());
    }
    
    @Test
    @DisplayName("Deve atualizar apenas email quando outros campos são null")
    void deveAtualizarApenasEmailQuandoOutrosCamposSaoNull() {
        // Arrange
        Aluno aluno = new Aluno(
            "id-def",
            "Nome Original",
            new Email("original@exemplo.com"),
            new Matricula("MAT33333")
        );
        
        AlunoUpdateDTO dto = AlunoUpdateDTO.builder()
            .email("novo@exemplo.com")
            .build();
        
        // Act
        AlunoMapper.updateEntityFromDto(aluno, dto);
        
        // Assert
        assertEquals("Nome Original", aluno.getNome());
        assertEquals("novo@exemplo.com", aluno.getEmail().getEmail());
        assertEquals("MAT33333", aluno.getMatricula().getMatricula());
    }
    
    @Test
    @DisplayName("Deve atualizar apenas matrícula quando outros campos são null")
    void deveAtualizarApenasMatriculaQuandoOutrosCamposSaoNull() {
        // Arrange
        Aluno aluno = new Aluno(
            "id-ghi",
            "Nome Original",
            new Email("original@exemplo.com"),
            new Matricula("MAT44444")
        );
        
        AlunoUpdateDTO dto = AlunoUpdateDTO.builder()
            .matricula("MAT55555")
            .build();
        
        // Act
        AlunoMapper.updateEntityFromDto(aluno, dto);
        
        // Assert
        assertEquals("Nome Original", aluno.getNome());
        assertEquals("original@exemplo.com", aluno.getEmail().getEmail());
        assertEquals("MAT55555", aluno.getMatricula().getMatricula());
    }

    @Test
    @DisplayName("Deve converter Aluno com Email null para DTO")
    void deveConverterAlunoComEmailNullParaDTO() {
        // Arrange
        Aluno aluno = new Aluno(
            "id-999",
            "Aluno Sem Email",
            null, // Email null
            new Matricula("MAT11111")
        );
        
        // Act
        AlunoResponseDTO dto = AlunoMapper.toResponseDTO(aluno);
        
        // Assert
        assertNotNull(dto);
        assertEquals("id-999", dto.getId());
        assertEquals("Aluno Sem Email", dto.getNome());
        assertNull(dto.getEmail()); // Email deve ser null
        assertEquals("MAT11111", dto.getMatricula());
    }

    @Test
    @DisplayName("Deve converter Aluno com Matricula null para DTO")
    void deveConverterAlunoComMatriculaNullParaDTO() {
        // Arrange
        Aluno aluno = new Aluno(
            "id-888",
            "Aluno Sem Matrícula",
            new Email("sem.matricula@exemplo.com"),
            null // Matricula null
        );
        
        // Act
        AlunoResponseDTO dto = AlunoMapper.toResponseDTO(aluno);
        
        // Assert
        assertNotNull(dto);
        assertEquals("id-888", dto.getId());
        assertEquals("Aluno Sem Matrícula", dto.getNome());
        assertEquals("sem.matricula@exemplo.com", dto.getEmail());
        assertNull(dto.getMatricula()); // Matricula deve ser null
    }

    @Test
    @DisplayName("Deve converter Aluno com Email e Matricula null para DetailDTO")
    void deveConverterAlunoComEmailEMatriculaNullParaDetailDTO() {
        // Arrange
        Aluno aluno = new Aluno(
            "id-777",
            "Aluno Incompleto",
            null, // Email null
            null  // Matricula null
        );
        
        // Act
        AlunoDetailResponseDTO dto = AlunoMapper.toDetailResponseDTO(aluno);
        
        // Assert
        assertNotNull(dto);
        assertEquals("id-777", dto.getId());
        assertEquals("Aluno Incompleto", dto.getNome());
        assertNull(dto.getEmail());
        assertNull(dto.getMatricula());
    }

    @Test
    @DisplayName("Deve retornar null quando RequestDTO for null")
    void deveRetornarNullQuandoRequestDTOForNull() {
        // Act
        Aluno aluno = AlunoMapper.toEntity(null);
        
        // Assert
        assertNull(aluno);
    }

    @Test
    @DisplayName("Deve retornar null quando Aluno for null ao converter para ResponseDTO")
    void deveRetornarNullQuandoAlunoForNullAoConverterParaResponseDTO() {
        // Act
        AlunoResponseDTO dto = AlunoMapper.toResponseDTO(null);
        
        // Assert
        assertNull(dto);
    }

    @Test
    @DisplayName("Deve retornar null quando Aluno for null ao converter para DetailDTO")
    void deveRetornarNullQuandoAlunoForNullAoConverterParaDetailDTO() {
        // Act
        AlunoDetailResponseDTO dto = AlunoMapper.toDetailResponseDTO(null);
        
        // Assert
        assertNull(dto);
    }

    @Test
    @DisplayName("Não deve fazer nada quando UpdateDTO for null")
    void naoDeveFazerNadaQuandoUpdateDTOForNull() {
        // Arrange
        Aluno aluno = new Aluno(
            "id-original",
            "Nome Original",
            new Email("original@exemplo.com"),
            new Matricula("MAT00000")
        );
        
        // Act
        AlunoMapper.updateEntityFromDto(aluno, null);
        
        // Assert - Valores devem permanecer inalterados
        assertEquals("Nome Original", aluno.getNome());
        assertEquals("original@exemplo.com", aluno.getEmail().getEmail());
        assertEquals("MAT00000", aluno.getMatricula().getMatricula());
    }
}
