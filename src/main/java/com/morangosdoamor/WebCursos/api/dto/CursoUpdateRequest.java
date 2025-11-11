package com.morangosdoamor.WebCursos.api.dto;

import jakarta.validation.constraints.Positive;
import java.util.Set;

public record CursoUpdateRequest(
    String codigo,
    String nome,
    String descricao,
    @Positive(message = "Carga horária deve ser positiva")
    Integer cargaHoraria,
    Set<String> prerequisitos
) {}

