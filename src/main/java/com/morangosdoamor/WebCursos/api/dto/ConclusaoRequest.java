package com.morangosdoamor.WebCursos.api.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ConclusaoRequest(
    @NotNull(message = "Nota final é obrigatória")
    @Min(value = 0, message = "Nota mínima é 0")
    @Max(value = 10, message = "Nota máxima é 10")
    Double notaFinal
) {}
