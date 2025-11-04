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
}
