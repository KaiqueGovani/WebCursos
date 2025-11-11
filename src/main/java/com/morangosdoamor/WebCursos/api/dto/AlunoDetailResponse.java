package com.morangosdoamor.WebCursos.api.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record AlunoDetailResponse(
    UUID id,
    String nome,
    String email,
    String matricula,
    LocalDateTime criadoEm,
    List<MatriculaResponse> matriculas
) {}

