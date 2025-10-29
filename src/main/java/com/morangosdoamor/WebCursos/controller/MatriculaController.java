package com.morangosdoamor.WebCursos.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.morangosdoamor.WebCursos.controller.dto.FinalizarCursoRequestDTO;
import com.morangosdoamor.WebCursos.controller.dto.MatriculaRequestDTO;
import com.morangosdoamor.WebCursos.model.entity.Aluno;
import com.morangosdoamor.WebCursos.model.entity.Curso;
import com.morangosdoamor.WebCursos.repository.AlunoRepository;
import com.morangosdoamor.WebCursos.repository.CursoRepository;
import com.morangosdoamor.WebCursos.service.CursoService;

@RestController
@RequestMapping("/matriculas")
public class MatriculaController {

    @Autowired
    private CursoService cursoService;

    @Autowired
    private AlunoRepository alunoRepository;

    @Autowired
    private CursoRepository cursoRepository;

    @PostMapping
    public ResponseEntity<String> matricularAluno(@RequestBody MatriculaRequestDTO request) {
        Aluno aluno = alunoRepository.findById(request.getAlunoId())
                .orElseThrow(() -> new IllegalArgumentException("Aluno não encontrado"));
        
        cursoService.adicionarCurso(aluno, request.getCursoId());
        
        return ResponseEntity.ok("Matrícula realizada com sucesso!");
    }

    @PostMapping("/finalizar")
    public ResponseEntity<String> finalizarCurso(@RequestBody FinalizarCursoRequestDTO request) {
        Aluno aluno = alunoRepository.findById(request.getAlunoId())
                .orElseThrow(() -> new IllegalArgumentException("Aluno não encontrado"));
        
        Curso curso = cursoRepository.findById(request.getCursoId())
                .orElseThrow(() -> new IllegalArgumentException("Curso não encontrado"));

        cursoService.finalizarCurso(aluno, curso, request.getNota());

        return ResponseEntity.ok("Curso finalizado com sucesso!");
    }
}
