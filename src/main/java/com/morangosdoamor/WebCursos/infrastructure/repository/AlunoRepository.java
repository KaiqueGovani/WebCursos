package com.morangosdoamor.WebCursos.infrastructure.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.morangosdoamor.WebCursos.domain.entity.Aluno;

public interface AlunoRepository extends JpaRepository<Aluno, UUID> {

    Optional<Aluno> findByMatricula(String matricula);

    @Query("SELECT a FROM Aluno a WHERE a.email.value = :email")
    Optional<Aluno> findByEmail(@Param("email") String email);
}
