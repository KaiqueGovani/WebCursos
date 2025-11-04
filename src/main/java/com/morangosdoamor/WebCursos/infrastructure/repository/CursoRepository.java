package com.morangosdoamor.WebCursos.infrastructure.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.morangosdoamor.WebCursos.domain.entity.Curso;

public interface CursoRepository extends JpaRepository<Curso, UUID> {

    Optional<Curso> findByCodigo(String codigo);
}
