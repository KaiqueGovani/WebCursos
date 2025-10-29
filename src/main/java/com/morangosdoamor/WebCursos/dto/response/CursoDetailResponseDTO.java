package com.morangosdoamor.WebCursos.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para resposta detalhada de Curso
 * Inclui todos os campos, incluindo pré-requisitos
 * Usado em consultas específicas (GET /cursos/{id})
 * DDD: conversão explícita de Value Objects para tipos primitivos
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CursoDetailResponseDTO {
    
    private String id;
    private String nome;
    private String descricao;
    private Integer cargaHoraria;
    private String[] prerequisitos;
    
    private Double cargaHorariaEmDias;
    private Double cargaHorariaEmSemanas;
}
