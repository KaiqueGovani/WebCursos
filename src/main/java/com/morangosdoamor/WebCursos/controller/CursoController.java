package com.morangosdoamor.WebCursos.controller;

import com.morangosdoamor.WebCursos.dto.request.CursoRequestDTO;
import com.morangosdoamor.WebCursos.dto.request.CursoUpdateDTO;
import com.morangosdoamor.WebCursos.dto.response.CursoDetailResponseDTO;
import com.morangosdoamor.WebCursos.dto.response.CursoResponseDTO;
import com.morangosdoamor.WebCursos.service.CursoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST para operações de Curso
 * Endpoints para CRUD completo de cursos
 */
@RestController
@RequestMapping("/api/cursos")
@RequiredArgsConstructor
@Tag(name = "Cursos", description = "Gerenciamento de cursos")
public class CursoController {
    
    private final CursoService cursoService;
    
    @Operation(summary = "Criar novo curso", description = "Cria um novo curso no sistema")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Curso criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos ou nome já cadastrado")
    })
    @PostMapping
    public ResponseEntity<CursoResponseDTO> criar(@Valid @RequestBody CursoRequestDTO dto) {
        CursoResponseDTO criado = cursoService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(criado);
    }
    
    @Operation(summary = "Listar todos os cursos", description = "Retorna lista de todos os cursos cadastrados")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @GetMapping
    public ResponseEntity<List<CursoResponseDTO>> listarTodos() {
        List<CursoResponseDTO> cursos = cursoService.listarTodos();
        return ResponseEntity.ok(cursos);
    }
    
    @Operation(summary = "Buscar curso por ID", description = "Retorna detalhes completos do curso incluindo conversões de carga horária")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Curso encontrado"),
        @ApiResponse(responseCode = "404", description = "Curso não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CursoDetailResponseDTO> buscarPorId(@PathVariable String id) {
        CursoDetailResponseDTO curso = cursoService.buscarPorId(id);
        return ResponseEntity.ok(curso);
    }
    
    @Operation(summary = "Buscar cursos por carga horária mínima", description = "Retorna cursos com carga horária maior ou igual ao valor informado")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @GetMapping("/carga-horaria/minima")
    public ResponseEntity<List<CursoResponseDTO>> buscarPorCargaHorariaMinima(
            @RequestParam Integer horas) {
        List<CursoResponseDTO> cursos = cursoService.buscarPorCargaHorariaMinima(horas);
        return ResponseEntity.ok(cursos);
    }
    
    @Operation(summary = "Buscar cursos por carga horária máxima", description = "Retorna cursos com carga horária menor ou igual ao valor informado")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @GetMapping("/carga-horaria/maxima")
    public ResponseEntity<List<CursoResponseDTO>> buscarPorCargaHorariaMaxima(
            @RequestParam Integer horas) {
        List<CursoResponseDTO> cursos = cursoService.buscarPorCargaHorariaMaxima(horas);
        return ResponseEntity.ok(cursos);
    }
    
    @Operation(summary = "Atualizar curso", description = "Atualiza dados do curso (apenas campos fornecidos)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Curso atualizado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Curso não encontrado"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PatchMapping("/{id}")
    public ResponseEntity<CursoResponseDTO> atualizar(
            @PathVariable String id,
            @Valid @RequestBody CursoUpdateDTO dto) {
        CursoResponseDTO atualizado = cursoService.atualizar(id, dto);
        return ResponseEntity.ok(atualizado);
    }
    
    @Operation(summary = "Excluir curso", description = "Remove curso do sistema")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Curso excluído com sucesso"),
        @ApiResponse(responseCode = "404", description = "Curso não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable String id) {
        cursoService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
