package com.morangosdoamor.WebCursos.api.dto;

import java.util.Set;
import java.util.UUID;

public record CursoDetailResponse(
    UUID id,
    String codigo,
    String nome,
    String descricao,
    int cargaHoraria,
    int cargaHorariaEmDias,
    int cargaHorariaEmSemanas,
    Set<String> prerequisitos
) {}

