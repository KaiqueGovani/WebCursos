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

/**
 * Entidade de domínio que representa a matrícula de um aluno em um curso.
 * 
 * Princípios DDD aplicados:
 * - Entidade com identidade única (UUID)
 * - Encapsula regras de negócio relacionadas a matrículas
 * - Mantém relacionamentos bidirecionais com Aluno e Curso
 * - Enum MatriculaStatus representa estados do domínio
 * 
 * Responsabilidades:
 * - Gerenciar ciclo de vida da matrícula (matriculado → concluído)
 * - Registrar nota final e datas importantes
 * - Determinar se o aluno foi aprovado (nota ≥ 7.0)
 */
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

    /**
     * Registra a data de matrícula e define o status como MATRICULADO.
     * Método de domínio que encapsula a lógica de registro de matrícula.
     * Garante que a data seja sempre definida no momento da matrícula.
     */
    public void registrarMatricula() {
        dataMatricula = LocalDateTime.now();
        status = MatriculaStatus.MATRICULADO;
    }

    /**
     * Conclui a matrícula registrando a nota final.
     * Define automaticamente a data de conclusão e atualiza o status para CONCLUIDO.
     * 
     * @param nota Nota final do curso (deve estar entre 0 e 10, validação feita no serviço)
     */
    public void concluir(Double nota) {
        notaFinal = nota;
        dataConclusao = LocalDateTime.now();
        status = MatriculaStatus.CONCLUIDO;
    }

    /**
     * Verifica se o aluno foi aprovado no curso.
     * Regra de negócio: nota final ≥ 7.0 e status CONCLUIDO.
     * 
     * @return true se o aluno foi aprovado (nota ≥ 7.0 e curso concluído), false caso contrário
     */
    public boolean estaAprovado() {
        return notaFinal != null && notaFinal >= 7.0 && MatriculaStatus.CONCLUIDO.equals(status);
    }
}
