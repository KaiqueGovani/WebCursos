package com.morangosdoamor.WebCursos.controller.dto;

public class MatriculaRequestDTO {
    private String alunoId;
    private String cursoId;

    // Getters and Setters
    public String getAlunoId() {
        return alunoId;
    }
    public void setAlunoId(String alunoId) {
        this.alunoId = alunoId;
    }
    public String getCursoId() {
        return cursoId;
    }
    public void setCursoId(String cursoId) {
        this.cursoId = cursoId;
    }
}
