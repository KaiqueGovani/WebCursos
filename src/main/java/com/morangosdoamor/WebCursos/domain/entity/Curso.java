package com.morangosdoamor.WebCursos.domain.entity;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.morangosdoamor.WebCursos.domain.valueobject.CargaHoraria;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidade de domínio que representa um curso disponível na plataforma.
 * 
 * Princípios DDD aplicados:
 * - Entidade com identidade única (UUID)
 * - Value Object CargaHoraria encapsula regras de carga horária
 * - Coleção de pré-requisitos como elementos do domínio
 * - Mantém relacionamento com Matricula
 * 
 * Responsabilidades:
 * - Representar informações do curso (código, nome, descrição)
 * - Gerenciar pré-requisitos do curso
 * - Encapsular carga horária com validações e conversões
 */
@Entity
@Table(name = "curso")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Curso {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "codigo", nullable = false, unique = true, length = 20)
    private String codigo;

    @Column(name = "nome", nullable = false, length = 120)
    private String nome;

    @Column(name = "descricao", nullable = false, length = 1000)
    private String descricao;

    @Embedded
    private CargaHoraria cargaHoraria;

    @Builder.Default
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "curso_prerequisito", joinColumns = @JoinColumn(name = "curso_id"))
    @Column(name = "codigo_prerequisito", length = 20)
    private Set<String> prerequisitos = new HashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "curso", fetch = FetchType.LAZY)
    private Set<Matricula> matriculas = new HashSet<>();
}
