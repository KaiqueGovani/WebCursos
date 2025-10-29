package com.morangosdoamor.WebCursos.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para resposta detalhada de Aluno
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
}
