package com.morangosdoamor.WebCursos.controller.dto;

public class FinalizarCursoRequestDTO {
    private String alunoId;
    private String cursoId;
    private float nota;

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
    public float getNota() {
        return nota;
    }
    public void setNota(float nota) {
        this.nota = nota;
    }
}
