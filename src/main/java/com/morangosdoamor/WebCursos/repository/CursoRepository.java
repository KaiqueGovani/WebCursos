package com.morangosdoamor.WebCursos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.morangosdoamor.WebCursos.model.entity.Curso;

@Repository
public interface CursoRepository extends JpaRepository<Curso, String> {
    
}
