package com.morangosdoamor.WebCursos.application.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.morangosdoamor.WebCursos.domain.entity.Aluno;
import com.morangosdoamor.WebCursos.domain.exception.BusinessRuleException;
import com.morangosdoamor.WebCursos.domain.exception.ResourceNotFoundException;
import com.morangosdoamor.WebCursos.infrastructure.repository.AlunoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AlunoService {

    private final AlunoRepository alunoRepository;

    @Transactional
    public Aluno criar(Aluno aluno) {
        alunoRepository.findByMatricula(aluno.getMatricula())
            .ifPresent(existing -> {
                throw new BusinessRuleException("Matrícula já cadastrada para outro aluno");
            });

        aluno.registrarCriacaoSeNecessario();
        return alunoRepository.save(aluno);
    }

    @Transactional(readOnly = true)
    public Aluno buscarPorId(UUID alunoId) {
        return alunoRepository.findById(alunoId)
            .orElseThrow(() -> new ResourceNotFoundException("Aluno não encontrado"));
    }
}
