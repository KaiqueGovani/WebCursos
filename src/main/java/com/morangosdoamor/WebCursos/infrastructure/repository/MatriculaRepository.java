package com.morangosdoamor.WebCursos.infrastructure.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.morangosdoamor.WebCursos.domain.entity.Matricula;
import com.morangosdoamor.WebCursos.domain.enums.MatriculaStatus;

public interface MatriculaRepository extends JpaRepository<Matricula, UUID> {

    boolean existsByAlunoIdAndCursoId(UUID alunoId, UUID cursoId);

    Optional<Matricula> findByIdAndAlunoId(UUID matriculaId, UUID alunoId);

    long countByAlunoIdAndStatusAndNotaFinalGreaterThanEqual(UUID alunoId, MatriculaStatus status, double nota);

    List<Matricula> findAllByAlunoId(UUID alunoId);

    /**
     * Busca os últimos N cursos concluídos por um aluno, ordenados por data de conclusão decrescente.
     * Usado pelo serviço de recomendação de IA para analisar histórico do aluno.
     * 
     * @param alunoId ID do aluno
     * @param status Status da matrícula (CONCLUIDO)
     * @return Lista das últimas 3 matrículas concluídas
     */
    List<Matricula> findTop3ByAlunoIdAndStatusOrderByDataConclusaoDesc(UUID alunoId, MatriculaStatus status);
}
