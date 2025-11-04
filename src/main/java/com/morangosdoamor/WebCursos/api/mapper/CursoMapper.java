package com.morangosdoamor.WebCursos.api.mapper;

import org.springframework.stereotype.Component;

import com.morangosdoamor.WebCursos.api.dto.CursoResponse;
import com.morangosdoamor.WebCursos.domain.entity.Curso;

@Component
public class CursoMapper {

    public CursoResponse toResponse(Curso curso) {
        if (curso == null) {
            return null;
        }

        return new CursoResponse(
            curso.getId(),
            curso.getCodigo(),
            curso.getNome(),
            curso.getDescricao(),
            curso.getCargaHoraria(),
            curso.getPrerequisitos()
        );
    }
}
