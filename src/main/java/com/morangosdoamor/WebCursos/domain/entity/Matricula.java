package com.morangosdoamor.WebCursos.domain.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import com.morangosdoamor.WebCursos.domain.enums.MatriculaStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "matricula")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Matricula {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "aluno_id")
    private Aluno aluno;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "curso_id")
    private Curso curso;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private MatriculaStatus status;

    @Column(name = "nota_final")
    private Double notaFinal;

    @Column(name = "data_matricula", nullable = false)
    private LocalDateTime dataMatricula;

    @Column(name = "data_conclusao")
    private LocalDateTime dataConclusao;

    public void registrarMatricula() {
        dataMatricula = LocalDateTime.now();
        status = MatriculaStatus.MATRICULADO;
    }

    public void concluir(Double nota) {
        notaFinal = nota;
        dataConclusao = LocalDateTime.now();
        status = MatriculaStatus.CONCLUIDO;
    }

    public boolean estaAprovado() {
        return notaFinal != null && notaFinal >= 7.0 && MatriculaStatus.CONCLUIDO.equals(status);
    }
}
