package com.morangosdoamor.WebCursos.controller;

import com.morangosdoamor.WebCursos.dto.request.AlunoRequestDTO;
import com.morangosdoamor.WebCursos.dto.request.AlunoUpdateDTO;
import com.morangosdoamor.WebCursos.dto.response.AlunoDetailResponseDTO;
import com.morangosdoamor.WebCursos.dto.response.AlunoResponseDTO;
import com.morangosdoamor.WebCursos.service.AlunoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST para operações de Aluno
 * 
 * Clean Architecture: Camada de Interface/Adapters
 * - Recebe requisições HTTP
 * - Valida dados de entrada (@Valid)
 * - Delega lógica para Service
 * - Retorna respostas HTTP apropriadas
 * 
 * Endpoints:
 * POST   /api/alunos           - Cria novo aluno
 * GET    /api/alunos           - Lista todos os alunos
 * GET    /api/alunos/{id}      - Busca aluno por ID
 * GET    /api/alunos/email/{email} - Busca por email
 * GET    /api/alunos/matricula/{matricula} - Busca por matrícula
 * PATCH  /api/alunos/{id}      - Atualiza aluno
 * DELETE /api/alunos/{id}      - Remove aluno
 */
@RestController
@RequestMapping("/api/alunos")
@RequiredArgsConstructor
public class AlunoController {
    
    private final AlunoService alunoService;
    
    /**
     * POST /api/alunos
     * Cria um novo aluno
     * 
     * @param dto dados do aluno
     * @return 201 Created com dados do aluno criado
     */
    @PostMapping
    public ResponseEntity<AlunoResponseDTO> criar(@Valid @RequestBody AlunoRequestDTO dto) {
        AlunoResponseDTO criado = alunoService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(criado);
    }
    
    /**
     * GET /api/alunos
     * Lista todos os alunos
     * 
     * @return 200 OK com lista de alunos
     */
    @GetMapping
    public ResponseEntity<List<AlunoResponseDTO>> listarTodos() {
        List<AlunoResponseDTO> alunos = alunoService.listarTodos();
        return ResponseEntity.ok(alunos);
    }
    
    /**
     * GET /api/alunos/{id}
     * Busca aluno por ID
     * 
     * @param id identificador do aluno
     * @return 200 OK com detalhes do aluno
     * @throws ResourceNotFoundException se não encontrar
     */
    @GetMapping("/{id}")
    public ResponseEntity<AlunoDetailResponseDTO> buscarPorId(@PathVariable String id) {
        AlunoDetailResponseDTO aluno = alunoService.buscarPorId(id);
        return ResponseEntity.ok(aluno);
    }
    
    /**
     * GET /api/alunos/email/{email}
     * Busca aluno por email
     * 
     * @param email email do aluno
     * @return 200 OK com dados do aluno
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<AlunoResponseDTO> buscarPorEmail(@PathVariable String email) {
        AlunoResponseDTO aluno = alunoService.buscarPorEmail(email);
        return ResponseEntity.ok(aluno);
    }
    
    /**
     * GET /api/alunos/matricula/{matricula}
     * Busca aluno por matrícula
     * 
     * @param matricula matrícula do aluno
     * @return 200 OK com dados do aluno
     */
    @GetMapping("/matricula/{matricula}")
    public ResponseEntity<AlunoResponseDTO> buscarPorMatricula(@PathVariable String matricula) {
        AlunoResponseDTO aluno = alunoService.buscarPorMatricula(matricula);
        return ResponseEntity.ok(aluno);
    }
    
    /**
     * PATCH /api/alunos/{id}
     * Atualiza dados do aluno (PATCH semântico)
     * Apenas campos fornecidos são atualizados
     * 
     * @param id identificador do aluno
     * @param dto dados a atualizar
     * @return 200 OK com dados atualizados
     */
    @PatchMapping("/{id}")
    public ResponseEntity<AlunoResponseDTO> atualizar(
            @PathVariable String id,
            @Valid @RequestBody AlunoUpdateDTO dto) {
        AlunoResponseDTO atualizado = alunoService.atualizar(id, dto);
        return ResponseEntity.ok(atualizado);
    }
    
    /**
     * DELETE /api/alunos/{id}
     * Remove aluno do sistema
     * 
     * @param id identificador do aluno
     * @return 204 No Content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable String id) {
        alunoService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
