package com.morangosdoamor.WebCursos.controller;

import com.morangosdoamor.WebCursos.dto.request.AlunoRequestDTO;
import com.morangosdoamor.WebCursos.dto.request.AlunoUpdateDTO;
import com.morangosdoamor.WebCursos.dto.response.AlunoDetailResponseDTO;
import com.morangosdoamor.WebCursos.dto.response.AlunoResponseDTO;
import com.morangosdoamor.WebCursos.service.AlunoService;
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
 * Controller REST para operações de Aluno
 * Endpoints para CRUD completo de alunos
 */
@RestController
@RequestMapping("/api/alunos")
@RequiredArgsConstructor
@Tag(name = "Alunos", description = "Gerenciamento de alunos")
public class AlunoController {
    
    private final AlunoService alunoService;
    
    @Operation(summary = "Criar novo aluno", description = "Cria um novo aluno no sistema")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Aluno criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos ou email/matrícula já cadastrados")
    })
    @PostMapping
    public ResponseEntity<AlunoResponseDTO> criar(@Valid @RequestBody AlunoRequestDTO dto) {
        AlunoResponseDTO criado = alunoService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(criado);
    }
    
    @Operation(summary = "Listar todos os alunos", description = "Retorna lista de todos os alunos cadastrados")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @GetMapping
    public ResponseEntity<List<AlunoResponseDTO>> listarTodos() {
        List<AlunoResponseDTO> alunos = alunoService.listarTodos();
        return ResponseEntity.ok(alunos);
    }
    
    @Operation(summary = "Buscar aluno por ID", description = "Retorna detalhes completos do aluno")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Aluno encontrado"),
        @ApiResponse(responseCode = "404", description = "Aluno não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<AlunoDetailResponseDTO> buscarPorId(@PathVariable String id) {
        AlunoDetailResponseDTO aluno = alunoService.buscarPorId(id);
        return ResponseEntity.ok(aluno);
    }
    
    @Operation(summary = "Buscar aluno por email", description = "Busca aluno pelo endereço de email")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Aluno encontrado"),
        @ApiResponse(responseCode = "404", description = "Aluno não encontrado")
    })
    @GetMapping("/email/{email}")
    public ResponseEntity<AlunoResponseDTO> buscarPorEmail(@PathVariable String email) {
        AlunoResponseDTO aluno = alunoService.buscarPorEmail(email);
        return ResponseEntity.ok(aluno);
    }
    
    @Operation(summary = "Buscar aluno por matrícula", description = "Busca aluno pelo número de matrícula")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Aluno encontrado"),
        @ApiResponse(responseCode = "404", description = "Aluno não encontrado")
    })
    @GetMapping("/matricula/{matricula}")
    public ResponseEntity<AlunoResponseDTO> buscarPorMatricula(@PathVariable String matricula) {
        AlunoResponseDTO aluno = alunoService.buscarPorMatricula(matricula);
        return ResponseEntity.ok(aluno);
    }
    
    @Operation(summary = "Atualizar aluno", description = "Atualiza dados do aluno (apenas campos fornecidos)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Aluno atualizado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Aluno não encontrado"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PatchMapping("/{id}")
    public ResponseEntity<AlunoResponseDTO> atualizar(
            @PathVariable String id,
            @Valid @RequestBody AlunoUpdateDTO dto) {
        AlunoResponseDTO atualizado = alunoService.atualizar(id, dto);
        return ResponseEntity.ok(atualizado);
    }
    
    @Operation(summary = "Excluir aluno", description = "Remove aluno do sistema")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Aluno excluído com sucesso"),
        @ApiResponse(responseCode = "404", description = "Aluno não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable String id) {
        alunoService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
