package com.morangosdoamor.WebCursos.api.dto;

import jakarta.validation.constraints.Email;

public record AlunoUpdateRequest(
    String nome,
    @Email(message = "Formato de e-mail inválido")
    String email,
    String matricula
) {}

