package com.morangosdoamor.WebCursos.repository;

import com.morangosdoamor.WebCursos.domain.Aluno;
import com.morangosdoamor.WebCursos.domain.valueobject.Email;
import com.morangosdoamor.WebCursos.domain.valueobject.Matricula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AlunoRepository extends JpaRepository<Aluno, String> {
    
    /**
     * Busca um aluno pelo email.
     */
    Optional<Aluno> findByEmail(Email email);
    
    /**
     * Busca um aluno pela matrícula.
     */
    Optional<Aluno> findByMatricula(Matricula matricula);
    
    /**
     * Verifica se já existe um aluno com o email informado.
     */
    boolean existsByEmail(Email email);
    
    /**
     * Verifica se já existe um aluno com a matrícula informada.
     */
    boolean existsByMatricula(Matricula matricula);
}
