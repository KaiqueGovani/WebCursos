package com.morangosdoamor.WebCursos.application.dto;

/**
 * DTO que representa um curso disponível para matrícula.
 * Usado pelo serviço de recomendação de IA para sugerir novos cursos ao aluno.
 * 
 * @param nome Nome do curso
 * @param codigo Código único do curso
 * @param descricao Descrição do conteúdo do curso
 * @param cargaHoraria Carga horária em horas
 */
public record CursoDisponivelDTO(
    String nome,
    String codigo,
    String descricao,
    int cargaHoraria
) {}


