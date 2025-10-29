package com.morangosdoamor.WebCursos.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para resposta resumida de Aluno
 * Usado em listagens e operações que não requerem todos os detalhes
 * Clean Architecture: camada externa não expõe entidade de domínio diretamente
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlunoResponseDTO {
    
    private String id;
    private String nome;
    private String email;
    private String matricula;
}
