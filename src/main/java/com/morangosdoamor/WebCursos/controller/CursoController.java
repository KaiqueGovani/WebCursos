package com.morangosdoamor.WebCursos.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.morangosdoamor.WebCursos.model.entity.Curso;
import com.morangosdoamor.WebCursos.service.CursoService;

@RestController
@RequestMapping("/cursos")
public class CursoController {

    @Autowired
    private CursoService cursoService;

    @GetMapping
    public List<Curso> listarTodosCursos() {
        return cursoService.getAllCursos();
    }
    
}
