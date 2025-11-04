package com.morangosdoamor.WebCursos.api.dto;

import jakarta.validation.constraints.NotBlank;

public record MatriculaRequest(
    @NotBlank(message = "Código do curso é obrigatório")
    String codigoCurso
) {}
