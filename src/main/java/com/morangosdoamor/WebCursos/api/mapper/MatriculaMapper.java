package com.morangosdoamor.WebCursos.api.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.morangosdoamor.WebCursos.api.dto.MatriculaResponse;
import com.morangosdoamor.WebCursos.domain.entity.Matricula;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MatriculaMapper {

    private final CursoMapper cursoMapper;

    public MatriculaResponse toResponse(Matricula matricula) {
        return new MatriculaResponse(
            matricula.getId(),
            matricula.getStatus().name(),
            matricula.getNotaFinal(),
            matricula.getDataMatricula(),
            matricula.getDataConclusao(),
            cursoMapper.toResponse(matricula.getCurso())
        );
    }

    public List<MatriculaResponse> toResponse(List<Matricula> matriculas) {
        return matriculas.stream().map(this::toResponse).toList();
    }
}
