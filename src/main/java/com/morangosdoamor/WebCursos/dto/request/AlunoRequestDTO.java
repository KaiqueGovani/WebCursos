package com.morangosdoamor.WebCursos.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para criação de novo Aluno
 * Segue princípios de Clean Architecture: DTOs na camada de interface
 * Validações Bean Validation garantem integridade antes de chegar ao domínio
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlunoRequestDTO {
    
    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 3, max = 255, message = "Nome deve ter entre 3 e 255 caracteres")
    private String nome;
    
    @NotBlank(message = "Email é obrigatório")
    @Pattern(
        regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
        message = "Email inválido"
    )
    private String email;
    
    @NotBlank(message = "Matrícula é obrigatória")
    @Size(min = 5, max = 20, message = "Matrícula deve ter entre 5 e 20 caracteres")
    private String matricula;
}
