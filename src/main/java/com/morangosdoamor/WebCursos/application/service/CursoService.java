package com.morangosdoamor.WebCursos.application.service;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.morangosdoamor.WebCursos.domain.entity.Curso;
import com.morangosdoamor.WebCursos.domain.entity.Matricula;
import com.morangosdoamor.WebCursos.domain.enums.MatriculaStatus;
import com.morangosdoamor.WebCursos.domain.exception.ResourceNotFoundException;
import com.morangosdoamor.WebCursos.infrastructure.repository.CursoRepository;
import com.morangosdoamor.WebCursos.infrastructure.repository.MatriculaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CursoService {

    private final CursoRepository cursoRepository;
    private final MatriculaRepository matriculaRepository;

    @Transactional(readOnly = true)
    public List<Curso> listarTodos() {
        return cursoRepository.findAll(Sort.by("nome").ascending());
    }

    @Transactional(readOnly = true)
    public Curso buscarPorCodigo(String codigo) {
        return cursoRepository.findByCodigo(codigo)
            .orElseThrow(() -> new ResourceNotFoundException("Curso não encontrado"));
    }

    @Transactional(readOnly = true)
    public List<Curso> buscarCursosLiberados(UUID alunoId) {
        long cursosAprovados = matriculaRepository
            .countByAlunoIdAndStatusAndNotaFinalGreaterThanEqual(alunoId, MatriculaStatus.CONCLUIDO, 7.0);

        if (cursosAprovados == 0) {
            return List.of();
        }

        List<Matricula> matriculas = matriculaRepository.findAllByAlunoId(alunoId);
        Set<UUID> cursosIndisponiveis = matriculas.stream()
            .map(m -> m.getCurso().getId())
            .collect(Collectors.toSet());

        long limite = Math.min(Integer.MAX_VALUE, cursosAprovados * 3L);

        return cursoRepository.findAll(Sort.by("nome").ascending()).stream()
            .filter(curso -> !cursosIndisponiveis.contains(curso.getId()))
            .limit(limite)
            .toList();
    }
}
