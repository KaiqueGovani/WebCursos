package com.morangosdoamor.WebCursos.domain.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.morangosdoamor.WebCursos.domain.valueobject.Email;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidade de domínio que representa um aluno na plataforma de cursos.
 * 
 * Princípios DDD aplicados:
 * - Entidade com identidade única (UUID)
 * - Encapsula regras de negócio relacionadas a alunos
 * - Mantém relacionamento bidirecional com Matricula
 * - Value Object Email encapsula validação de email
 * 
 * Responsabilidades:
 * - Gerenciar matrículas do aluno
 * - Calcular estatísticas (total de cursos aprovados)
 * - Garantir data de criação consistente
 */
@Entity
@Table(name = "aluno")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Aluno {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "nome", nullable = false, length = 150)
    private String nome;

    @Embedded
    private Email email;

    @Column(name = "matricula", nullable = false, unique = true, length = 30)
    private String matricula;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;

    @Builder.Default
    @OneToMany(mappedBy = "aluno", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Matricula> matriculas = new HashSet<>();

    /**
     * Adiciona uma matrícula ao aluno e estabelece o relacionamento bidirecional.
     * Garante consistência do relacionamento aluno-matrícula.
     * 
     * @param matricula Matrícula a ser adicionada ao aluno
     */
    public void adicionarMatricula(Matricula matricula) {
        matriculas.add(matricula);
        matricula.setAluno(this);
    }

    /**
     * Calcula o total de cursos aprovados pelo aluno.
     * Considera apenas matrículas concluídas com nota final ≥ 7.0.
     * 
     * @return Quantidade de cursos aprovados (nota ≥ 7.0)
     */
    public long totalCursosAprovados() {
        return matriculas.stream().filter(Matricula::estaAprovado).count();
    }

    /**
     * Registra a data de criação do aluno se ainda não estiver definida.
     * Garante que toda entidade tenha uma data de criação consistente.
     * Útil para criação via builders ou mappers que podem não definir a data.
     */
    public void registrarCriacaoSeNecessario() {
        if (criadoEm == null) {
            criadoEm = LocalDateTime.now();
        }
    }
}
