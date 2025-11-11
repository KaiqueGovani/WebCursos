package com.morangosdoamor.WebCursos.api.controller;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.morangosdoamor.WebCursos.api.dto.CursoDetailResponse;
import com.morangosdoamor.WebCursos.api.dto.CursoRequest;
import com.morangosdoamor.WebCursos.api.dto.CursoResponse;
import com.morangosdoamor.WebCursos.api.dto.CursoUpdateRequest;
import com.morangosdoamor.WebCursos.api.mapper.CursoMapper;
import com.morangosdoamor.WebCursos.application.service.CursoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/cursos")
@RequiredArgsConstructor
@Validated
@Tag(name = "Cursos", description = "Gerenciamento de cursos")
public class CursoController {

    private final CursoService cursoService;
    private final CursoMapper cursoMapper;

    @PostMapping
    @Operation(summary = "Criar novo curso", description = "Cria um novo curso no sistema")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Curso criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos ou nome já cadastrado")
    })
    public ResponseEntity<CursoResponse> criar(@Valid @RequestBody CursoRequest dto) {
        var criado = cursoService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(cursoMapper.toResponse(criado));
    }

    @GetMapping
    @Operation(summary = "Listar todos os cursos", description = "Retorna lista de todos os cursos cadastrados")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    public ResponseEntity<List<CursoResponse>> listarTodos() {
        List<CursoResponse> cursos = cursoService.listarTodos().stream()
            .map(cursoMapper::toResponse)
            .toList();
        return ResponseEntity.ok(cursos);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar curso por ID", description = "Retorna detalhes completos do curso incluindo conversões de carga horária")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Curso encontrado"),
        @ApiResponse(responseCode = "404", description = "Curso não encontrado")
    })
    public ResponseEntity<CursoDetailResponse> buscarPorId(@PathVariable UUID id) {
        var curso = cursoService.buscarPorId(id);
        return ResponseEntity.ok(cursoMapper.toDetailResponse(curso));
    }

    @GetMapping("/carga-horaria/minima")
    @Operation(summary = "Buscar cursos por carga horária mínima", description = "Retorna cursos com carga horária maior ou igual ao valor informado")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    public ResponseEntity<List<CursoResponse>> buscarPorCargaHorariaMinima(
            @RequestParam Integer horas) {
        List<CursoResponse> cursos = cursoService.buscarPorCargaHorariaMinima(horas).stream()
            .map(cursoMapper::toResponse)
            .toList();
        return ResponseEntity.ok(cursos);
    }

    @GetMapping("/carga-horaria/maxima")
    @Operation(summary = "Buscar cursos por carga horária máxima", description = "Retorna cursos com carga horária menor ou igual ao valor informado")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    public ResponseEntity<List<CursoResponse>> buscarPorCargaHorariaMaxima(
            @RequestParam Integer horas) {
        List<CursoResponse> cursos = cursoService.buscarPorCargaHorariaMaxima(horas).stream()
            .map(cursoMapper::toResponse)
            .toList();
        return ResponseEntity.ok(cursos);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Atualizar curso", description = "Atualiza dados do curso (apenas campos fornecidos)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Curso atualizado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Curso não encontrado"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    public ResponseEntity<CursoResponse> atualizar(
            @PathVariable UUID id,
            @Valid @RequestBody CursoUpdateRequest dto) {
        var atualizado = cursoService.atualizar(id, dto);
        return ResponseEntity.ok(cursoMapper.toResponse(atualizado));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir curso", description = "Remove curso do sistema")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Curso excluído com sucesso"),
        @ApiResponse(responseCode = "404", description = "Curso não encontrado")
    })
    public ResponseEntity<Void> excluir(@PathVariable UUID id) {
        cursoService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
