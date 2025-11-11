package com.morangosdoamor.WebCursos.api.controller;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.morangosdoamor.WebCursos.api.dto.AlunoDetailResponse;
import com.morangosdoamor.WebCursos.api.dto.AlunoRequest;
import com.morangosdoamor.WebCursos.api.dto.AlunoResponse;
import com.morangosdoamor.WebCursos.api.dto.AlunoUpdateRequest;
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
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Operation(summary = "Criar novo aluno", description = "Cria um novo aluno no sistema")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Aluno criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos ou email/matrícula já cadastrados")
    })
    public ResponseEntity<AlunoResponse> criar(@Valid @RequestBody AlunoRequest request) {
        Aluno aluno = alunoMapper.toEntity(request);
        Aluno salvo = alunoService.criar(aluno);
        AlunoResponse response = alunoMapper.toResponse(salvo);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(response.id())
            .toUri();

        return ResponseEntity.status(HttpStatus.CREATED).location(location).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar todos os alunos", description = "Retorna lista de todos os alunos cadastrados")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    public ResponseEntity<List<AlunoResponse>> listarTodos() {
        List<AlunoResponse> alunos = alunoService.listarTodos().stream()
            .map(alunoMapper::toResponse)
            .toList();
        return ResponseEntity.ok(alunos);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar aluno por ID", description = "Retorna detalhes completos do aluno")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Aluno encontrado"),
        @ApiResponse(responseCode = "404", description = "Aluno não encontrado")
    })
    public ResponseEntity<AlunoDetailResponse> buscarPorId(@PathVariable UUID id) {
        Aluno aluno = alunoService.buscarPorId(id);
        return ResponseEntity.ok(alunoMapper.toDetailResponse(aluno));
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Buscar aluno por email", description = "Busca aluno pelo endereço de email")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Aluno encontrado"),
        @ApiResponse(responseCode = "404", description = "Aluno não encontrado")
    })
    public ResponseEntity<AlunoResponse> buscarPorEmail(@PathVariable String email) {
        Aluno aluno = alunoService.buscarPorEmail(email);
        return ResponseEntity.ok(alunoMapper.toResponse(aluno));
    }

    @GetMapping("/matricula/{matricula}")
    @Operation(summary = "Buscar aluno por matrícula", description = "Busca aluno pelo número de matrícula")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Aluno encontrado"),
        @ApiResponse(responseCode = "404", description = "Aluno não encontrado")
    })
    public ResponseEntity<AlunoResponse> buscarPorMatricula(@PathVariable String matricula) {
        Aluno aluno = alunoService.buscarPorMatricula(matricula);
        return ResponseEntity.ok(alunoMapper.toResponse(aluno));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Atualizar aluno", description = "Atualiza dados do aluno (apenas campos fornecidos)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Aluno atualizado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Aluno não encontrado"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    public ResponseEntity<AlunoResponse> atualizar(
            @PathVariable UUID id,
            @Valid @RequestBody AlunoUpdateRequest dto) {
        Aluno atualizado = alunoService.atualizar(id, dto);
        return ResponseEntity.ok(alunoMapper.toResponse(atualizado));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir aluno", description = "Remove aluno do sistema")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Aluno excluído com sucesso"),
        @ApiResponse(responseCode = "404", description = "Aluno não encontrado")
    })
    public ResponseEntity<Void> excluir(@PathVariable UUID id) {
        alunoService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/matriculas")
    @Operation(summary = "Lista as matrículas de um aluno")
    public ResponseEntity<List<MatriculaResponse>> listarMatriculas(@PathVariable("id") UUID alunoId) {
        List<Matricula> matriculas = matriculaService.listarPorAluno(alunoId);
        return ResponseEntity.ok(matriculaMapper.toResponse(matriculas));
    }

    @PostMapping("/{id}/matriculas")
    @Operation(summary = "Matricula um aluno em um curso")
    public ResponseEntity<MatriculaResponse> matricular(@PathVariable("id") UUID alunoId,
                                                        @Valid @RequestBody MatriculaRequest request) {
        Matricula matricula = matriculaService.matricular(alunoId, request.codigoCurso());
        MatriculaResponse response = matriculaMapper.toResponse(matricula);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{matriculaId}")
            .buildAndExpand(response.id())
            .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @PostMapping("/{id}/matriculas/{matriculaId}/conclusao")
    @Operation(summary = "Finaliza um curso para o aluno")
    public ResponseEntity<MatriculaResponse> concluir(@PathVariable("id") UUID alunoId,
                                                      @PathVariable UUID matriculaId,
                                                      @Valid @RequestBody ConclusaoRequest request) {
        Matricula matricula = matriculaService.concluir(alunoId, matriculaId, request.notaFinal());
        return ResponseEntity.ok(matriculaMapper.toResponse(matricula));
    }

    @GetMapping("/{id}/cursos/liberados")
    @Operation(summary = "Lista cursos liberados pelo desempenho do aluno")
    public ResponseEntity<List<CursoResponse>> cursosLiberados(@PathVariable("id") UUID alunoId) {
        List<CursoResponse> cursos = cursoService.buscarCursosLiberados(alunoId).stream()
            .map(cursoMapper::toResponse)
            .toList();
        return ResponseEntity.ok(cursos);
    }

    @GetMapping("/{id}/matriculas/{matriculaId}/nota")
    @Operation(summary = "Obtém a nota final de uma matrícula")
    public ResponseEntity<Double> obterNota(@PathVariable("id") UUID alunoId, @PathVariable UUID matriculaId) {
        Double nota = matriculaService.buscarNotaFinal(alunoId, matriculaId);
        return nota == null ? ResponseEntity.noContent().build() : ResponseEntity.ok(nota);
    }
}
