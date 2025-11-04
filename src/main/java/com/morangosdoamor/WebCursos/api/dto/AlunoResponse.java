package com.morangosdoamor.WebCursos.api.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record AlunoResponse(
    UUID id,
    String nome,
    String email,
    String matricula,
    LocalDateTime criadoEm
) {}
