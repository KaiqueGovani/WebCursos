package com.morangosdoamor.WebCursos.api.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record MatriculaResponse(
    UUID id,
    String status,
    Double notaFinal,
    LocalDateTime dataMatricula,
    LocalDateTime dataConclusao,
    CursoResponse curso
) {}
