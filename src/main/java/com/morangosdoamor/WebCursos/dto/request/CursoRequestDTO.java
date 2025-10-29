package com.morangosdoamor.WebCursos.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para criação de novo Curso
 * Bean Validation garante regras de negócio básicas na camada de interface
 * Clean Architecture: DTOs isolam o domínio da infraestrutura externa
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CursoRequestDTO {
    
    @NotBlank(message = "Nome do curso é obrigatório")
    @Size(min = 3, max = 255, message = "Nome deve ter entre 3 e 255 caracteres")
    private String nome;
    
    @Size(max = 1000, message = "Descrição deve ter no máximo 1000 caracteres")
    private String descricao;
    
    @NotNull(message = "Carga horária é obrigatória")
    @Min(value = 1, message = "Carga horária mínima é 1 hora")
    @Max(value = 1000, message = "Carga horária máxima é 1000 horas")
    private Integer cargaHoraria;
    
    @Size(max = 10, message = "Máximo de 10 pré-requisitos")
    private String[] prerequisitos;
}
