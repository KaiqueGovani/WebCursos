package com.morangosdoamor.WebCursos.api.dto;

import java.util.Set;
import java.util.UUID;

public record CursoResponse(
    UUID id,
    String codigo,
    String nome,
    String descricao,
    int cargaHoraria,
    Set<String> prerequisitos
) {}
