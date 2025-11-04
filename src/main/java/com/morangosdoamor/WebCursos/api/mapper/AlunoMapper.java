package com.morangosdoamor.WebCursos.api.mapper;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.morangosdoamor.WebCursos.api.dto.AlunoRequest;
import com.morangosdoamor.WebCursos.api.dto.AlunoResponse;
import com.morangosdoamor.WebCursos.domain.entity.Aluno;
import com.morangosdoamor.WebCursos.domain.valueobject.Email;

@Component
public class AlunoMapper {

    public Aluno toEntity(AlunoRequest request) {
        return Aluno.builder()
            .nome(request.nome())
            .email(new Email(request.email()))
            .matricula(request.matricula())
            .criadoEm(LocalDateTime.now())
            .build();
    }

    public AlunoResponse toResponse(Aluno aluno) {
        return new AlunoResponse(
            aluno.getId(),
            aluno.getNome(),
            aluno.getEmail() != null ? aluno.getEmail().getValue() : null,
            aluno.getMatricula(),
            aluno.getCriadoEm()
        );
    }
}
