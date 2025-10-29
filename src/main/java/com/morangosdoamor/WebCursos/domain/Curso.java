package com.morangosdoamor.WebCursos.domain;

import com.morangosdoamor.WebCursos.domain.valueobject.CargaHoraria;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cursos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Curso {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @Column(nullable = false)
    private String nome;
    
    @Column(length = 1000)
    private String descricao;
    
    @Embedded
    private CargaHoraria cargaHoraria;
    
    @Column(length = 500)
    private String[] prerequisitos;
}
