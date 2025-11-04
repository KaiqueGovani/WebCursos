package com.morangosdoamor.WebCursos.api.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.morangosdoamor.WebCursos.api.dto.CursoResponse;
import com.morangosdoamor.WebCursos.api.mapper.CursoMapper;
import com.morangosdoamor.WebCursos.application.service.CursoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/cursos")
@RequiredArgsConstructor
@Tag(name = "Cursos", description = "Catálogo de cursos")
public class CursoController {

    private final CursoService cursoService;
    private final CursoMapper cursoMapper;

    @GetMapping
    @Operation(summary = "Lista todos os cursos cadastrados")
    public ResponseEntity<List<CursoResponse>> listarCursos() {
        List<CursoResponse> cursos = cursoService.listarTodos().stream()
            .map(cursoMapper::toResponse)
            .toList();
        return ResponseEntity.ok(cursos);
    }
}
