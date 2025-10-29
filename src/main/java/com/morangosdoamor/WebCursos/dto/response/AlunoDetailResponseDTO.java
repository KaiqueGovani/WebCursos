package com.morangosdoamor.WebCursos.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para resposta detalhada de Aluno
 * Inclui metadados adicionais como timestamps de auditoria
 * Usado em consultas individuais (GET /alunos/{id})
 * DDD: evita exposição de Value Objects internos
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlunoDetailResponseDTO {
    
    private String id;
    private String nome;
    private String email;
    private String matricula;
    
    // Metadados que podem ser adicionados futuramente
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;
}
