package com.morangosdoamor.WebCursos.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para atualização parcial de Curso
 * Todos os campos opcionais para permitir PATCH
 * DDD: separação clara entre comandos de criação e atualização
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CursoUpdateDTO {
    
    @Size(min = 3, max = 255, message = "Nome deve ter entre 3 e 255 caracteres")
    private String nome;
    
    @Size(max = 1000, message = "Descrição deve ter no máximo 1000 caracteres")
    private String descricao;
    
    @Min(value = 1, message = "Carga horária mínima é 1 hora")
    @Max(value = 1000, message = "Carga horária máxima é 1000 horas")
    private Integer cargaHoraria;
    
    @Size(max = 10, message = "Máximo de 10 pré-requisitos")
    private String[] prerequisitos;
}
