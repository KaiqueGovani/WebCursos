package com.morangosdoamor.WebCursos.api.mapper;

import org.springframework.stereotype.Component;

import com.morangosdoamor.WebCursos.api.dto.CursoDetailResponse;
import com.morangosdoamor.WebCursos.api.dto.CursoRequest;
import com.morangosdoamor.WebCursos.api.dto.CursoResponse;
import com.morangosdoamor.WebCursos.api.dto.CursoUpdateRequest;
import com.morangosdoamor.WebCursos.domain.entity.Curso;
import com.morangosdoamor.WebCursos.domain.valueobject.CargaHoraria;

import java.util.HashSet;

/**
 * Mapper para conversão entre Curso (Domain) e DTOs.
 * 
 * Princípios aplicados:
 * - Clean Architecture: isolamento entre camadas (domain não conhece DTOs)
 * - DDD: conversão explícita de/para Value Objects
 * - Anti-Corruption Layer: DTOs protegem o domínio de mudanças externas
 * 
 * Responsabilidades:
 * - Conversão de DTOs de Request para entidades de domínio
 * - Conversão de entidades de domínio para DTOs de Response (resumido e detalhado)
 * - Atualização parcial de entidades a partir de DTOs de Update
 * - Conversão de Value Objects (CargaHoraria) para tipos primitivos e múltiplas unidades
 */
@Component
public class CursoMapper {

    /**
     * Converte Entidade para DTO de Resposta resumido.
     * Extrai valores dos Value Objects para tipos primitivos.
     * Retorna carga horária apenas em horas.
     * 
     * @param curso Entidade de domínio Curso
     * @return DTO de resposta resumido, ou null se curso for null
     */
    public CursoResponse toResponse(Curso curso) {
        if (curso == null) {
            return null;
        }

        return new CursoResponse(
            curso.getId(),
            curso.getCodigo(),
            curso.getNome(),
            curso.getDescricao(),
            curso.getCargaHoraria() != null ? curso.getCargaHoraria().getHoras() : 0,
            curso.getPrerequisitos()
        );
    }

    /**
     * Converte Entidade para DTO de Resposta detalhado.
     * Inclui conversões de carga horária em múltiplas unidades (horas, dias, semanas).
     * Útil para visualizações que precisam de informações completas do curso.
     * 
     * @param curso Entidade de domínio Curso
     * @return DTO de resposta detalhado com carga horária em múltiplas unidades, ou null se curso for null
     */
    public CursoDetailResponse toDetailResponse(Curso curso) {
        if (curso == null) {
            return null;
        }

        CargaHoraria cargaHoraria = curso.getCargaHoraria();
        int horas = cargaHoraria != null ? cargaHoraria.getHoras() : 0;
        int dias = cargaHoraria != null ? cargaHoraria.emDias() : 0;
        int semanas = cargaHoraria != null ? cargaHoraria.emSemanas() : 0;

        return new CursoDetailResponse(
            curso.getId(),
            curso.getCodigo(),
            curso.getNome(),
            curso.getDescricao(),
            horas,
            dias,
            semanas,
            curso.getPrerequisitos()
        );
    }

    /**
     * Converte DTO de Request para Entidade de Domínio.
     * Cria novos Value Objects (CargaHoraria) a partir dos dados primitivos.
     * Converte coleção de pré-requisitos para HashSet.
     * 
     * @param dto DTO de request contendo dados do curso
     * @return Entidade Curso criada a partir do DTO
     */
    public Curso toEntity(CursoRequest dto) {
        return Curso.builder()
            .codigo(dto.codigo())
            .nome(dto.nome())
            .descricao(dto.descricao())
            .cargaHoraria(new CargaHoraria(dto.cargaHoraria()))
            .prerequisitos(dto.prerequisitos() != null ? new HashSet<>(dto.prerequisitos()) : new HashSet<>())
            .build();
    }

    /**
     * Atualiza entidade existente com dados do UpdateDTO.
     * Apenas campos não-nulos e não-vazios são atualizados (PATCH semântico).
     * DDD: preserva a identidade da entidade, só atualiza atributos.
     * Cria novos Value Objects quando necessário (ex: CargaHoraria).
     * 
     * @param curso Entidade de domínio a ser atualizada
     * @param dto DTO contendo os campos a serem atualizados
     */
    public void updateEntity(Curso curso, CursoUpdateRequest dto) {
        if (dto.codigo() != null && !dto.codigo().isBlank()) {
            curso.setCodigo(dto.codigo());
        }
        if (dto.nome() != null && !dto.nome().isBlank()) {
            curso.setNome(dto.nome());
        }
        if (dto.descricao() != null && !dto.descricao().isBlank()) {
            curso.setDescricao(dto.descricao());
        }
        if (dto.cargaHoraria() != null) {
            curso.setCargaHoraria(new CargaHoraria(dto.cargaHoraria()));
        }
        if (dto.prerequisitos() != null) {
            curso.setPrerequisitos(new HashSet<>(dto.prerequisitos()));
        }
    }
}
