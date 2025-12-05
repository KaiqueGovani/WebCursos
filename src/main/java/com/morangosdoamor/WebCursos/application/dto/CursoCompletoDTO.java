package com.morangosdoamor.WebCursos.application.dto;

/**
 * DTO que representa um curso concluído pelo aluno.
 * Usado pelo serviço de recomendação de IA para analisar o histórico do aluno.
 * 
 * @param nome Nome do curso concluído
 * @param codigo Código único do curso
 * @param nota Nota final obtida pelo aluno
 */
public record CursoCompletoDTO(
    String nome,
    String codigo,
    Double nota
) {}


