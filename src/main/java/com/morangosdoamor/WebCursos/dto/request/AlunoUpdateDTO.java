package com.morangosdoamor.WebCursos.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para atualização de Aluno existente
 * Campos opcionais permitem atualização parcial (PATCH)
 * Seguindo Clean Architecture: separação entre criação e atualização
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlunoUpdateDTO {
    
    @Size(min = 3, max = 255, message = "Nome deve ter entre 3 e 255 caracteres")
    private String nome;
    
    @Pattern(
        regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
        message = "Email inválido"
    )
    private String email;
    
    @Size(min = 5, max = 20, message = "Matrícula deve ter entre 5 e 20 caracteres")
    private String matricula;
}
