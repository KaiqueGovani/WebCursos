package com.morangosdoamor.WebCursos.api.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.morangosdoamor.WebCursos.api.dto.MatriculaResponse;
import com.morangosdoamor.WebCursos.domain.entity.Matricula;

import lombok.RequiredArgsConstructor;

/**
 * Mapper para conversão entre Matricula (Domain) e DTOs.
 * 
 * Princípios aplicados:
 * - Clean Architecture: isolamento entre camadas (domain não conhece DTOs)
 * - DDD: conversão explícita de enums e relacionamentos
 * - Anti-Corruption Layer: DTOs protegem o domínio de mudanças externas
 * 
 * Responsabilidades:
 * - Conversão de entidades de domínio para DTOs de Response
 * - Conversão de coleções de matrículas
 * - Inclusão de dados relacionados (curso) nos DTOs de resposta
 */
@Component
@RequiredArgsConstructor
public class MatriculaMapper {

    private final CursoMapper cursoMapper;

    /**
     * Converte Entidade para DTO de Resposta.
     * Inclui dados do curso relacionado convertido para DTO.
     * Converte enum MatriculaStatus para String.
     * 
     * @param matricula Entidade de domínio Matricula
     * @return DTO de resposta com dados da matrícula e curso relacionado
     */
    public MatriculaResponse toResponse(Matricula matricula) {
        return new MatriculaResponse(
            matricula.getId(),
            matricula.getStatus().name(),
            matricula.getNotaFinal(),
            matricula.getDataMatricula(),
            matricula.getDataConclusao(),
            cursoMapper.toResponse(matricula.getCurso())
        );
    }

    /**
     * Converte lista de Entidades para lista de DTOs de Resposta.
     * Aplica conversão individual para cada matrícula da lista.
     * 
     * @param matriculas Lista de entidades de domínio Matricula
     * @return Lista de DTOs de resposta convertidos
     */
    public List<MatriculaResponse> toResponse(List<Matricula> matriculas) {
        return matriculas.stream().map(this::toResponse).toList();
    }
}
