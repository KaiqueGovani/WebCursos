package com.morangosdoamor.WebCursos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.morangosdoamor.WebCursos.model.entity.Aluno;

@Repository
public interface AlunoRepository extends JpaRepository<Aluno, String> {
    
}
