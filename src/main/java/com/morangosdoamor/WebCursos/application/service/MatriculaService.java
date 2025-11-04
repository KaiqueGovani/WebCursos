package com.morangosdoamor.WebCursos.application.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.morangosdoamor.WebCursos.domain.entity.Aluno;
import com.morangosdoamor.WebCursos.domain.entity.Curso;
import com.morangosdoamor.WebCursos.domain.entity.Matricula;
import com.morangosdoamor.WebCursos.domain.enums.MatriculaStatus;
import com.morangosdoamor.WebCursos.domain.exception.BusinessRuleException;
import com.morangosdoamor.WebCursos.domain.exception.ResourceNotFoundException;
import com.morangosdoamor.WebCursos.infrastructure.repository.AlunoRepository;
import com.morangosdoamor.WebCursos.infrastructure.repository.CursoRepository;
import com.morangosdoamor.WebCursos.infrastructure.repository.MatriculaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MatriculaService {

    private final AlunoRepository alunoRepository;
    private final CursoRepository cursoRepository;
    private final MatriculaRepository matriculaRepository;

    @Transactional
    public Matricula matricular(UUID alunoId, String codigoCurso) {
        Aluno aluno = alunoRepository.findById(alunoId)
            .orElseThrow(() -> new ResourceNotFoundException("Aluno não encontrado"));
        Curso curso = cursoRepository.findByCodigo(codigoCurso)
            .orElseThrow(() -> new ResourceNotFoundException("Curso não encontrado"));

        if (matriculaRepository.existsByAlunoIdAndCursoId(aluno.getId(), curso.getId())) {
            throw new BusinessRuleException("Aluno já matriculado ou curso concluído anteriormente");
        }

        Matricula matricula = Matricula.builder()
            .aluno(aluno)
            .curso(curso)
            .status(MatriculaStatus.MATRICULADO)
            .build();

        matricula.registrarMatricula();
        aluno.adicionarMatricula(matricula);

        return matriculaRepository.save(matricula);
    }

    @Transactional
    public Matricula concluir(UUID alunoId, UUID matriculaId, double notaFinal) {
        if (notaFinal < 0 || notaFinal > 10) {
            throw new BusinessRuleException("Nota final deve estar entre 0 e 10");
        }

        Matricula matricula = matriculaRepository.findByIdAndAlunoId(matriculaId, alunoId)
            .orElseThrow(() -> new ResourceNotFoundException("Matrícula não encontrada para o aluno informado"));

        if (MatriculaStatus.CONCLUIDO.equals(matricula.getStatus())) {
            throw new BusinessRuleException("O curso já está concluído");
        }

        matricula.concluir(notaFinal);
        return matricula;
    }

    @Transactional(readOnly = true)
    public List<Matricula> listarPorAluno(UUID alunoId) {
        validarExistenciaAluno(alunoId);
        return matriculaRepository.findAllByAlunoId(alunoId);
    }

    @Transactional(readOnly = true)
    public Double buscarNotaFinal(UUID alunoId, UUID matriculaId) {
        return matriculaRepository.findByIdAndAlunoId(matriculaId, alunoId)
            .map(Matricula::getNotaFinal)
            .orElse(null);
    }

    private void validarExistenciaAluno(UUID alunoId) {
        if (!alunoRepository.existsById(alunoId)) {
            throw new ResourceNotFoundException("Aluno não encontrado");
        }
    }
}
