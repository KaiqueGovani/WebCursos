package com.morangosdoamor.WebCursos.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AlunoRequest(
    @NotBlank(message = "Nome é obrigatório")
    String nome,

    @NotBlank(message = "E-mail é obrigatório")
    @Email(message = "Formato de e-mail inválido")
    String email,

    @NotBlank(message = "Matrícula é obrigatória")
    String matricula
) {}
