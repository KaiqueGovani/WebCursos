package com.morangosdoamor.WebCursos.dto.mapper;

import com.morangosdoamor.WebCursos.domain.Curso;
import com.morangosdoamor.WebCursos.domain.valueobject.CargaHoraria;
import com.morangosdoamor.WebCursos.dto.request.CursoRequestDTO;
import com.morangosdoamor.WebCursos.dto.request.CursoUpdateDTO;
import com.morangosdoamor.WebCursos.dto.response.CursoDetailResponseDTO;
import com.morangosdoamor.WebCursos.dto.response.CursoResponseDTO;
import lombok.experimental.UtilityClass;

/**
 * Mapper para conversão entre Curso (Domain) e DTOs
 * 
 * Princípios Clean Architecture e DDD:
 * - Anti-Corruption Layer: protege o domínio de mudanças na API
 * - Value Objects: conversão explícita de/para CargaHoraria
 * - Separation of Concerns: lógica de conversão isolada dos controllers
 * 
 * @UtilityClass: classe utilitária com métodos estáticos (Lombok)
 */
@UtilityClass
public class CursoMapper {
    
    /**
     * Converte DTO de Request para Entidade de Domínio
     * Cria Value Object CargaHoraria a partir de Integer primitivo
     */
    public static Curso toEntity(CursoRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        
        return new Curso(
            null, // ID gerado pelo banco
            dto.getNome(),
            dto.getDescricao(),
            new CargaHoraria(dto.getCargaHoraria()),
            dto.getPrerequisitos()
        );
    }
    
    /**
     * Atualiza entidade existente com dados do UpdateDTO
     * Implementa PATCH semântico: apenas campos não-nulos são atualizados
     * DDD: mantém identidade da entidade, atualiza apenas valores
     */
    public static void updateEntityFromDto(Curso entity, CursoUpdateDTO dto) {
        if (dto == null) {
            return;
        }
        
        if (dto.getNome() != null) {
            entity.setNome(dto.getNome());
        }
        
        if (dto.getDescricao() != null) {
            entity.setDescricao(dto.getDescricao());
        }
        
        if (dto.getCargaHoraria() != null) {
            entity.setCargaHoraria(new CargaHoraria(dto.getCargaHoraria()));
        }
        
        if (dto.getPrerequisitos() != null) {
            entity.setPrerequisitos(dto.getPrerequisitos());
        }
    }
    
    /**
     * Converte Entidade para DTO de Resposta resumido
     * Extrai valor primitivo do Value Object CargaHoraria
     */
    public static CursoResponseDTO toResponseDTO(Curso entity) {
        if (entity == null) {
            return null;
        }
        
        return CursoResponseDTO.builder()
            .id(entity.getId())
            .nome(entity.getNome())
            .descricao(entity.getDescricao())
            .cargaHoraria(entity.getCargaHoraria() != null 
                ? entity.getCargaHoraria().getCargaHoraria() 
                : null)
            .build();
    }
    
    /**
     * Converte Entidade para DTO de Resposta detalhado
     * Expõe métodos utilitários do Value Object (emDias, emSemanas)
     * DDD: revela comportamentos do domínio através da API
     */
    public static CursoDetailResponseDTO toDetailResponseDTO(Curso entity) {
        if (entity == null) {
            return null;
        }
        
        CargaHoraria carga = entity.getCargaHoraria();
        
        return CursoDetailResponseDTO.builder()
            .id(entity.getId())
            .nome(entity.getNome())
            .descricao(entity.getDescricao())
            .cargaHoraria(carga != null ? carga.getCargaHoraria() : null)
            .prerequisitos(entity.getPrerequisitos())
            .cargaHorariaEmDias(carga != null ? Double.valueOf(carga.emDias()) : null)
            .cargaHorariaEmSemanas(carga != null ? Double.valueOf(carga.emSemanas()) : null)
            .build();
    }
}
