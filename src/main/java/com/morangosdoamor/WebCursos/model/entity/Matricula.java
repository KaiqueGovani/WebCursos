package com.morangosdoamor.WebCursos.model.entity;

import java.time.LocalDate;
import java.util.Objects;

import com.morangosdoamor.WebCursos.model.valueobject.Nota;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class Matricula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Aluno aluno;

    @ManyToOne
    private Curso curso;
    private LocalDate dataInscricao;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "nota_final"))
    private Nota notaFinal;

    @Enumerated(EnumType.STRING)
    private StatusMatricula status;

    public enum StatusMatricula {
        EM_ANDAMENTO,
        CONCLUIDO,
        REPROVADO,
        CANCELADO
    }

    public Matricula(Aluno aluno, Curso curso) {
        this.aluno = aluno;
        this.curso = curso;
        this.dataInscricao = LocalDate.now();
        this.status = StatusMatricula.EM_ANDAMENTO;
    }

    protected Matricula() {
        // For JPA
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Aluno getAluno() {
        return aluno;
    }

    public void setAluno(Aluno aluno) {
        this.aluno = aluno;
    }

    public Curso getCurso() {
        return curso;
    }

    public void setCurso(Curso curso) {
        this.curso = curso;
    }

    public LocalDate getDataInscricao() {
        return dataInscricao;
    }

    public void setDataInscricao(LocalDate dataInscricao) {
        this.dataInscricao = dataInscricao;
    }

    public Nota getNotaFinal() {
        return notaFinal;
    }

    public void setNotaFinal(Nota notaFinal) {
        this.notaFinal = notaFinal;
    }

    public StatusMatricula getStatus() {
        return status;
    }

    public void setStatus(StatusMatricula status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Matricula matricula = (Matricula) o;
        return Objects.equals(id, matricula.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
