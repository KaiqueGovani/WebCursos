package com.morangosdoamor.WebCursos.api.mapper;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Component;

import com.morangosdoamor.WebCursos.api.dto.AlunoDetailResponse;
import com.morangosdoamor.WebCursos.api.dto.AlunoRequest;
import com.morangosdoamor.WebCursos.api.dto.AlunoResponse;
import com.morangosdoamor.WebCursos.api.dto.AlunoUpdateRequest;
import com.morangosdoamor.WebCursos.api.dto.MatriculaResponse;
import com.morangosdoamor.WebCursos.domain.entity.Aluno;
import com.morangosdoamor.WebCursos.domain.valueobject.Email;

import lombok.RequiredArgsConstructor;

/**
 * Mapper para conversão entre Aluno (Domain) e DTOs.
 * 
 * Princípios aplicados:
 * - Clean Architecture: isolamento entre camadas (domain não conhece DTOs)
 * - DDD: conversão explícita de/para Value Objects
 * - Anti-Corruption Layer: DTOs protegem o domínio de mudanças externas
 * 
 * Responsabilidades:
 * - Conversão de DTOs de Request para entidades de domínio
 * - Conversão de entidades de domínio para DTOs de Response
 * - Atualização parcial de entidades a partir de DTOs de Update
 */
@Component
@RequiredArgsConstructor
public class AlunoMapper {

    private final MatriculaMapper matriculaMapper;

    /**
     * Converte DTO de Request para Entidade de Domínio.
     * Cria novos Value Objects (Email) a partir dos dados primitivos.
     * Define automaticamente a data de criação.
     * 
     * @param request DTO de request contendo dados do aluno
     * @return Entidade Aluno criada a partir do DTO
     */
    public Aluno toEntity(AlunoRequest request) {
        return Aluno.builder()
            .nome(request.nome())
            .email(new Email(request.email()))
            .matricula(request.matricula())
            .criadoEm(LocalDateTime.now())
            .build();
    }

    /**
     * Converte Entidade para DTO de Resposta resumido.
     * Extrai valores dos Value Objects para tipos primitivos.
     * Não inclui informações detalhadas como matrículas.
     * 
     * @param aluno Entidade de domínio Aluno
     * @return DTO de resposta resumido com dados básicos do aluno
     */
    public AlunoResponse toResponse(Aluno aluno) {
        return new AlunoResponse(
            aluno.getId(),
            aluno.getNome(),
            aluno.getEmail() != null ? aluno.getEmail().getValue() : null,
            aluno.getMatricula(),
            aluno.getCriadoEm()
        );
    }

    /**
     * Converte Entidade para DTO de Resposta detalhado.
     * Inclui todas as matrículas do aluno convertidas para DTOs de resposta.
     * Útil para visualizações que precisam de informações completas do aluno.
     * 
     * @param aluno Entidade de domínio Aluno
     * @return DTO de resposta detalhado com dados completos incluindo matrículas
     */
    public AlunoDetailResponse toDetailResponse(Aluno aluno) {
        List<MatriculaResponse> matriculas = aluno.getMatriculas() != null
            ? aluno.getMatriculas().stream()
                .map(matriculaMapper::toResponse)
                .toList()
            : List.of();

        return new AlunoDetailResponse(
            aluno.getId(),
            aluno.getNome(),
            aluno.getEmail() != null ? aluno.getEmail().getValue() : null,
            aluno.getMatricula(),
            aluno.getCriadoEm(),
            matriculas
        );
    }

    /**
     * Atualiza entidade existente com dados do UpdateDTO.
     * Apenas campos não-nulos e não-vazios são atualizados (PATCH semântico).
     * DDD: preserva a identidade da entidade, só atualiza atributos.
     * Cria novos Value Objects quando necessário (ex: Email).
     * 
     * @param aluno Entidade de domínio a ser atualizada
     * @param dto DTO contendo os campos a serem atualizados
     */
    public void updateEntity(Aluno aluno, AlunoUpdateRequest dto) {
        if (dto.nome() != null && !dto.nome().isBlank()) {
            aluno.setNome(dto.nome());
        }
        if (dto.email() != null && !dto.email().isBlank()) {
            aluno.setEmail(new Email(dto.email()));
        }
        if (dto.matricula() != null && !dto.matricula().isBlank()) {
            aluno.setMatricula(dto.matricula());
        }
    }
}
