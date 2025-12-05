package com.morangosdoamor.WebCursos.infrastructure.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.morangosdoamor.WebCursos.domain.entity.Curso;

public interface CursoRepository extends JpaRepository<Curso, UUID> {

    Optional<Curso> findByCodigo(String codigo);

    @Query("SELECT c FROM Curso c WHERE c.cargaHoraria.cargaHoraria >= :horas")
    List<Curso> findByCargaHorariaMinima(@Param("horas") int horas);

    @Query("SELECT c FROM Curso c WHERE c.cargaHoraria.cargaHoraria <= :horas")
    List<Curso> findByCargaHorariaMaxima(@Param("horas") int horas);

    /**
     * Busca cursos que o aluno ainda não está matriculado (nem iniciou, nem concluiu).
     * Usado pelo serviço de recomendação de IA para sugerir novos cursos.
     * 
     * @param alunoId ID do aluno
     * @return Lista de cursos disponíveis para matrícula, ordenados por nome
     */
    @Query("""
        SELECT c FROM Curso c 
        WHERE c.id NOT IN (
            SELECT m.curso.id FROM Matricula m 
            WHERE m.aluno.id = :alunoId
        )
        ORDER BY c.nome
    """)
    List<Curso> findCursosNotEnrolledByAluno(@Param("alunoId") UUID alunoId);
}
