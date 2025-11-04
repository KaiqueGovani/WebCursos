package com.morangosdoamor.WebCursos.infrastructure.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.morangosdoamor.WebCursos.domain.entity.Aluno;

public interface AlunoRepository extends JpaRepository<Aluno, UUID> {

    Optional<Aluno> findByMatricula(String matricula);
}
