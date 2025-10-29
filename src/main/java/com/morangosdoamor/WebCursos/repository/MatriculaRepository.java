package com.morangosdoamor.WebCursos.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.morangosdoamor.WebCursos.model.entity.Aluno;
import com.morangosdoamor.WebCursos.model.entity.Curso;
import com.morangosdoamor.WebCursos.model.entity.Matricula;
import com.morangosdoamor.WebCursos.model.entity.Matricula.StatusMatricula;

@Repository
public interface MatriculaRepository extends JpaRepository<Matricula, Long> {
    
    Optional<Matricula> findByAlunoAndCurso(Aluno aluno, Curso curso);

    List<Matricula> findByAlunoAndStatus(Aluno aluno, StatusMatricula status);

    long countByAlunoAndStatus(Aluno aluno, StatusMatricula status);

    boolean existsByAlunoAndCurso(Aluno aluno, Curso curso);

    List<Matricula> findByAluno(Aluno aluno);
}
