package com.morangosdoamor.WebCursos.repository;

import com.morangosdoamor.WebCursos.domain.Curso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CursoRepository extends JpaRepository<Curso, String> {
    
    /**
     * Busca um curso pelo nome.
     */
    Optional<Curso> findByNome(String nome);
    
    /**
     * Busca cursos com carga horária mínima.
     */
    @Query("SELECT c FROM Curso c WHERE c.cargaHoraria.cargaHoraria >= :horasMinimas")
    List<Curso> findByCargaHorariaMinima(int horasMinimas);
    
    /**
     * Busca cursos com carga horária máxima.
     */
    @Query("SELECT c FROM Curso c WHERE c.cargaHoraria.cargaHoraria <= :horasMaximas")
    List<Curso> findByCargaHorariaMaxima(int horasMaximas);
    
    /**
     * Verifica se existe um curso com o nome informado.
     */
    boolean existsByNome(String nome);
}
