package com.morangosdoamor.WebCursos.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para resposta resumida de Curso
 * Usado em listagens onde não é necessário expor todos os detalhes
 * Clean Architecture: camada de apresentação não expõe domínio interno
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CursoResponseDTO {
    
    private String id;
    private String nome;
    private String descricao;
    private Integer cargaHoraria;
}
