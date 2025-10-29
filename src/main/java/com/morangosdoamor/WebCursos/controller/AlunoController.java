package com.morangosdoamor.WebCursos.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.morangosdoamor.WebCursos.controller.dto.AlunoRequestDTO;
import com.morangosdoamor.WebCursos.model.entity.Aluno;
import com.morangosdoamor.WebCursos.repository.AlunoRepository;

@RestController
@RequestMapping("/alunos")
public class AlunoController {

    @Autowired
    private AlunoRepository alunoRepository;

    @PostMapping
    public ResponseEntity<Aluno> criarAluno(@RequestBody AlunoRequestDTO request) {
        Aluno novoAluno = new Aluno(request.getNome(), request.getEmail(), request.getSenha());
        alunoRepository.save(novoAluno);
        return ResponseEntity.ok(novoAluno);
    }
}
