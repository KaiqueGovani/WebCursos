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
}
