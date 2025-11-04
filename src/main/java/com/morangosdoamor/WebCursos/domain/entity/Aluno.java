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

    public void adicionarMatricula(Matricula matricula) {
        matriculas.add(matricula);
        matricula.setAluno(this);
    }

    public long totalCursosAprovados() {
        return matriculas.stream().filter(Matricula::estaAprovado).count();
    }

    public void registrarCriacaoSeNecessario() {
        if (criadoEm == null) {
            criadoEm = LocalDateTime.now();
        }
    }
}
