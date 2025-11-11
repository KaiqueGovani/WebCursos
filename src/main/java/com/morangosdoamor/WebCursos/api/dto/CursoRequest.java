package com.morangosdoamor.WebCursos.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.Set;

public record CursoRequest(
    @NotBlank(message = "Código é obrigatório")
    String codigo,

    @NotBlank(message = "Nome é obrigatório")
    String nome,

    @NotBlank(message = "Descrição é obrigatória")
    String descricao,

    @NotNull(message = "Carga horária é obrigatória")
    @Positive(message = "Carga horária deve ser positiva")
    Integer cargaHoraria,

    Set<String> prerequisitos
) {}

