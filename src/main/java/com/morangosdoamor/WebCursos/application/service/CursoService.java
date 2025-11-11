package com.morangosdoamor.WebCursos.application.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.morangosdoamor.WebCursos.api.dto.CursoRequest;
import com.morangosdoamor.WebCursos.api.dto.CursoUpdateRequest;
import com.morangosdoamor.WebCursos.domain.entity.Curso;
import com.morangosdoamor.WebCursos.domain.entity.Matricula;
import com.morangosdoamor.WebCursos.domain.enums.MatriculaStatus;
import com.morangosdoamor.WebCursos.domain.exception.BusinessRuleException;
import com.morangosdoamor.WebCursos.domain.exception.ResourceNotFoundException;
import com.morangosdoamor.WebCursos.domain.valueobject.CargaHoraria;
import com.morangosdoamor.WebCursos.infrastructure.repository.CursoRepository;
import com.morangosdoamor.WebCursos.infrastructure.repository.MatriculaRepository;

import lombok.RequiredArgsConstructor;

/**
 * Serviço responsável pela gestão de cursos e sistema de liberação automática.
 * 
 * Princípios aplicados:
 * - Clean Architecture: encapsula regras de negócio relacionadas a cursos
 * - DDD: opera sobre entidades de domínio preservando invariantes
 * - Transaction Management: métodos transacionais garantem consistência de dados
 * 
 * Responsabilidades:
 * - CRUD completo de cursos
 * - Validação de unicidade de código de curso
 * - Sistema de liberação automática: cada curso concluído com média ≥ 7.0 libera 3 novos cursos
 * - Busca por diferentes critérios (ID, código, carga horária)
 */
@Service
@RequiredArgsConstructor
public class CursoService {

    private final CursoRepository cursoRepository;
    private final MatriculaRepository matriculaRepository;

    /**
     * Lista todos os cursos cadastrados no sistema.
     * Retorna lista ordenada alfabeticamente por nome.
     * 
     * @return Lista de todos os cursos ordenados por nome
     */
    @Transactional(readOnly = true)
    public List<Curso> listarTodos() {
        return cursoRepository.findAll(Sort.by("nome").ascending());
    }

    /**
     * Busca um curso pelo código único.
     * 
     * @param codigo Código único do curso (ex: "JAVA001")
     * @return Curso encontrado com o código informado
     * @throws ResourceNotFoundException se nenhum curso for encontrado com o código informado
     */
    @Transactional(readOnly = true)
    public Curso buscarPorCodigo(String codigo) {
        return cursoRepository.findByCodigo(codigo)
            .orElseThrow(() -> new ResourceNotFoundException("Curso não encontrado"));
    }

    /**
     * Busca cursos liberados para matrícula baseado no desempenho do aluno.
     * 
     * Regra de negócio: Cada curso concluído com média ≥ 7.0 libera automaticamente 3 novos cursos.
     * A liberação é acumulativa (ex: 2 cursos aprovados = 6 cursos liberados).
     * 
     * Cursos já matriculados ou concluídos pelo aluno são excluídos da lista.
     * 
     * @param alunoId UUID do aluno para o qual buscar cursos liberados
     * @return Lista de cursos liberados para matrícula, ordenados por nome. Lista vazia se o aluno não tiver cursos aprovados.
     */
    @Transactional(readOnly = true)
    public List<Curso> buscarCursosLiberados(UUID alunoId) {
        long cursosAprovados = matriculaRepository
            .countByAlunoIdAndStatusAndNotaFinalGreaterThanEqual(alunoId, MatriculaStatus.CONCLUIDO, 7.0);

        if (cursosAprovados == 0) {
            return List.of();
        }

        List<Matricula> matriculas = matriculaRepository.findAllByAlunoId(alunoId);
        Set<UUID> cursosIndisponiveis = matriculas.stream()
            .map(m -> m.getCurso().getId())
            .collect(Collectors.toSet());

        long limite = Math.min(Integer.MAX_VALUE, cursosAprovados * 3L);

        return cursoRepository.findAll(Sort.by("nome").ascending()).stream()
            .filter(curso -> !cursosIndisponiveis.contains(curso.getId()))
            .limit(limite)
            .toList();
    }

    /**
     * Cria um novo curso no sistema.
     * Valida unicidade do código antes de persistir.
     * Cria Value Object CargaHoraria a partir do valor primitivo.
     * 
     * @param dto DTO contendo os dados do curso a ser criado
     * @return Curso criado e persistido
     * @throws BusinessRuleException se o código já estiver cadastrado para outro curso
     */
    @Transactional
    public Curso criar(CursoRequest dto) {
        cursoRepository.findByCodigo(dto.codigo())
            .ifPresent(existing -> {
                throw new BusinessRuleException("Código já cadastrado para outro curso");
            });

        Curso curso = Curso.builder()
            .codigo(dto.codigo())
            .nome(dto.nome())
            .descricao(dto.descricao())
            .cargaHoraria(new CargaHoraria(dto.cargaHoraria()))
            .prerequisitos(dto.prerequisitos() != null ? new HashSet<>(dto.prerequisitos()) : new HashSet<>())
            .build();

        return cursoRepository.save(curso);
    }

    /**
     * Busca um curso por seu identificador único.
     * 
     * @param id UUID do curso a ser buscado
     * @return Curso encontrado
     * @throws ResourceNotFoundException se o curso não for encontrado
     */
    @Transactional(readOnly = true)
    public Curso buscarPorId(UUID id) {
        return cursoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Curso não encontrado"));
    }

    /**
     * Busca cursos com carga horária maior ou igual ao valor informado.
     * 
     * @param horas Carga horária mínima em horas
     * @return Lista de cursos que possuem carga horária maior ou igual ao valor informado
     */
    @Transactional(readOnly = true)
    public List<Curso> buscarPorCargaHorariaMinima(int horas) {
        return cursoRepository.findByCargaHorariaMinima(horas);
    }

    /**
     * Busca cursos com carga horária menor ou igual ao valor informado.
     * 
     * @param horas Carga horária máxima em horas
     * @return Lista de cursos que possuem carga horária menor ou igual ao valor informado
     */
    @Transactional(readOnly = true)
    public List<Curso> buscarPorCargaHorariaMaxima(int horas) {
        return cursoRepository.findByCargaHorariaMaxima(horas);
    }

    /**
     * Atualiza dados de um curso existente (atualização parcial - PATCH semântico).
     * Apenas campos não-nulos e não-vazios do DTO são atualizados.
     * Valida unicidade do código antes de atualizar.
     * 
     * @param id UUID do curso a ser atualizado
     * @param dto DTO contendo os campos a serem atualizados (campos nulos/vazios são ignorados)
     * @return Curso atualizado e persistido
     * @throws ResourceNotFoundException se o curso não for encontrado
     * @throws BusinessRuleException se o código já estiver cadastrado para outro curso
     */
    @Transactional
    public Curso atualizar(UUID id, CursoUpdateRequest dto) {
        Curso curso = buscarPorId(id);

        if (dto.codigo() != null && !dto.codigo().isBlank()) {
            cursoRepository.findByCodigo(dto.codigo())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new BusinessRuleException("Código já cadastrado para outro curso");
                });
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

        return cursoRepository.save(curso);
    }

    /**
     * Exclui um curso do sistema.
     * 
     * @param id UUID do curso a ser excluído
     * @throws ResourceNotFoundException se o curso não for encontrado
     */
    @Transactional
    public void excluir(UUID id) {
        Curso curso = buscarPorId(id);
        cursoRepository.delete(curso);
    }
}
