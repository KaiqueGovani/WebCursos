package com.morangosdoamor.WebCursos.controller;

import com.morangosdoamor.WebCursos.dto.request.CursoRequestDTO;
import com.morangosdoamor.WebCursos.dto.request.CursoUpdateDTO;
import com.morangosdoamor.WebCursos.dto.response.CursoDetailResponseDTO;
import com.morangosdoamor.WebCursos.dto.response.CursoResponseDTO;
import com.morangosdoamor.WebCursos.service.CursoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST para operações de Curso
 * 
 * Clean Architecture: Camada de Interface/Adapters
 * - Adapta requisições HTTP para casos de uso do Service
 * - Bean Validation automática com @Valid
 * - Tratamento de erros delegado ao GlobalExceptionHandler
 * 
 * Endpoints:
 * POST   /api/cursos                        - Cria novo curso
 * GET    /api/cursos                        - Lista todos os cursos
 * GET    /api/cursos/{id}                   - Busca curso por ID
 * GET    /api/cursos/carga-horaria/minima  - Busca por carga horária mínima
 * GET    /api/cursos/carga-horaria/maxima  - Busca por carga horária máxima
 * PATCH  /api/cursos/{id}                   - Atualiza curso
 * DELETE /api/cursos/{id}                   - Remove curso
 */
@RestController
@RequestMapping("/api/cursos")
@RequiredArgsConstructor
public class CursoController {
    
    private final CursoService cursoService;
    
    /**
     * POST /api/cursos
     * Cria um novo curso
     * 
     * @param dto dados do curso
     * @return 201 Created com dados do curso criado
     */
    @PostMapping
    public ResponseEntity<CursoResponseDTO> criar(@Valid @RequestBody CursoRequestDTO dto) {
        CursoResponseDTO criado = cursoService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(criado);
    }
    
    /**
     * GET /api/cursos
     * Lista todos os cursos
     * 
     * @return 200 OK com lista de cursos
     */
    @GetMapping
    public ResponseEntity<List<CursoResponseDTO>> listarTodos() {
        List<CursoResponseDTO> cursos = cursoService.listarTodos();
        return ResponseEntity.ok(cursos);
    }
    
    /**
     * GET /api/cursos/{id}
     * Busca curso por ID com detalhes completos
     * Inclui conversões de carga horária (dias, semanas)
     * 
     * @param id identificador do curso
     * @return 200 OK com detalhes do curso
     */
    @GetMapping("/{id}")
    public ResponseEntity<CursoDetailResponseDTO> buscarPorId(@PathVariable String id) {
        CursoDetailResponseDTO curso = cursoService.buscarPorId(id);
        return ResponseEntity.ok(curso);
    }
    
    /**
     * GET /api/cursos/carga-horaria/minima?horas={horas}
     * Busca cursos com carga horária >= valor informado
     * 
     * @param horas carga horária mínima
     * @return 200 OK com lista de cursos
     */
    @GetMapping("/carga-horaria/minima")
    public ResponseEntity<List<CursoResponseDTO>> buscarPorCargaHorariaMinima(
            @RequestParam Integer horas) {
        List<CursoResponseDTO> cursos = cursoService.buscarPorCargaHorariaMinima(horas);
        return ResponseEntity.ok(cursos);
    }
    
    /**
     * GET /api/cursos/carga-horaria/maxima?horas={horas}
     * Busca cursos com carga horária <= valor informado
     * 
     * @param horas carga horária máxima
     * @return 200 OK com lista de cursos
     */
    @GetMapping("/carga-horaria/maxima")
    public ResponseEntity<List<CursoResponseDTO>> buscarPorCargaHorariaMaxima(
            @RequestParam Integer horas) {
        List<CursoResponseDTO> cursos = cursoService.buscarPorCargaHorariaMaxima(horas);
        return ResponseEntity.ok(cursos);
    }
    
    /**
     * PATCH /api/cursos/{id}
     * Atualiza dados do curso (PATCH semântico)
     * Apenas campos fornecidos são atualizados
     * 
     * @param id identificador do curso
     * @param dto dados a atualizar
     * @return 200 OK com dados atualizados
     */
    @PatchMapping("/{id}")
    public ResponseEntity<CursoResponseDTO> atualizar(
            @PathVariable String id,
            @Valid @RequestBody CursoUpdateDTO dto) {
        CursoResponseDTO atualizado = cursoService.atualizar(id, dto);
        return ResponseEntity.ok(atualizado);
    }
    
    /**
     * DELETE /api/cursos/{id}
     * Remove curso do sistema
     * 
     * @param id identificador do curso
     * @return 204 No Content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable String id) {
        cursoService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
