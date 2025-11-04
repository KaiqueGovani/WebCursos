package com.morangosdoamor.WebCursos.api.controller;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.morangosdoamor.WebCursos.api.dto.AlunoRequest;
import com.morangosdoamor.WebCursos.api.dto.AlunoResponse;
import com.morangosdoamor.WebCursos.api.dto.ConclusaoRequest;
import com.morangosdoamor.WebCursos.api.dto.CursoResponse;
import com.morangosdoamor.WebCursos.api.dto.MatriculaRequest;
import com.morangosdoamor.WebCursos.api.dto.MatriculaResponse;
import com.morangosdoamor.WebCursos.api.mapper.AlunoMapper;
import com.morangosdoamor.WebCursos.api.mapper.CursoMapper;
import com.morangosdoamor.WebCursos.api.mapper.MatriculaMapper;
import com.morangosdoamor.WebCursos.application.service.AlunoService;
import com.morangosdoamor.WebCursos.application.service.CursoService;
import com.morangosdoamor.WebCursos.application.service.MatriculaService;
import com.morangosdoamor.WebCursos.domain.entity.Aluno;
import com.morangosdoamor.WebCursos.domain.entity.Matricula;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/alunos")
@RequiredArgsConstructor
@Validated
@Tag(name = "Alunos", description = "Gestão de alunos e matrículas")
public class AlunoController {

    private final AlunoService alunoService;
    private final MatriculaService matriculaService;
    private final CursoService cursoService;
    private final AlunoMapper alunoMapper;
    private final MatriculaMapper matriculaMapper;
    private final CursoMapper cursoMapper;

    @PostMapping
    @Operation(summary = "Cadastra um novo aluno")
    public ResponseEntity<AlunoResponse> criarAluno(@Valid @RequestBody AlunoRequest request) {
        Aluno aluno = alunoMapper.toEntity(request);
        Aluno salvo = alunoService.criar(aluno);
        AlunoResponse response = alunoMapper.toResponse(salvo);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(response.id())
            .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/{alunoId}")
    @Operation(summary = "Recupera os dados de um aluno")
    public ResponseEntity<AlunoResponse> buscarAluno(@PathVariable UUID alunoId) {
        Aluno aluno = alunoService.buscarPorId(alunoId);
        return ResponseEntity.ok(alunoMapper.toResponse(aluno));
    }

    @GetMapping("/{alunoId}/matriculas")
    @Operation(summary = "Lista as matrículas de um aluno")
    public ResponseEntity<List<MatriculaResponse>> listarMatriculas(@PathVariable UUID alunoId) {
        List<Matricula> matriculas = matriculaService.listarPorAluno(alunoId);
        return ResponseEntity.ok(matriculaMapper.toResponse(matriculas));
    }

    @PostMapping("/{alunoId}/matriculas")
    @Operation(summary = "Matricula um aluno em um curso")
    public ResponseEntity<MatriculaResponse> matricular(@PathVariable UUID alunoId,
                                                        @Valid @RequestBody MatriculaRequest request) {
        Matricula matricula = matriculaService.matricular(alunoId, request.codigoCurso());
        MatriculaResponse response = matriculaMapper.toResponse(matricula);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{matriculaId}")
            .buildAndExpand(response.id())
            .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @PostMapping("/{alunoId}/matriculas/{matriculaId}/conclusao")
    @Operation(summary = "Finaliza um curso para o aluno")
    public ResponseEntity<MatriculaResponse> concluir(@PathVariable UUID alunoId,
                                                      @PathVariable UUID matriculaId,
                                                      @Valid @RequestBody ConclusaoRequest request) {
        Matricula matricula = matriculaService.concluir(alunoId, matriculaId, request.notaFinal());
        return ResponseEntity.ok(matriculaMapper.toResponse(matricula));
    }

    @GetMapping("/{alunoId}/cursos/liberados")
    @Operation(summary = "Lista cursos liberados pelo desempenho do aluno")
    public ResponseEntity<List<CursoResponse>> cursosLiberados(@PathVariable UUID alunoId) {
        List<CursoResponse> cursos = cursoService.buscarCursosLiberados(alunoId).stream()
            .map(cursoMapper::toResponse)
            .toList();
        return ResponseEntity.ok(cursos);
    }

    @GetMapping("/{alunoId}/matriculas/{matriculaId}/nota")
    @Operation(summary = "Obtém a nota final de uma matrícula")
    public ResponseEntity<Double> obterNota(@PathVariable UUID alunoId, @PathVariable UUID matriculaId) {
        Double nota = matriculaService.buscarNotaFinal(alunoId, matriculaId);
        return nota == null ? ResponseEntity.noContent().build() : ResponseEntity.ok(nota);
    }
}
