package com.morangosdoamor.WebCursos.dto.mapper;

import com.morangosdoamor.WebCursos.domain.Aluno;
import com.morangosdoamor.WebCursos.domain.valueobject.Email;
import com.morangosdoamor.WebCursos.domain.valueobject.Matricula;
import com.morangosdoamor.WebCursos.dto.request.AlunoRequestDTO;
import com.morangosdoamor.WebCursos.dto.request.AlunoUpdateDTO;
import com.morangosdoamor.WebCursos.dto.response.AlunoDetailResponseDTO;
import com.morangosdoamor.WebCursos.dto.response.AlunoResponseDTO;
import lombok.experimental.UtilityClass;

/**
 * Mapper para conversão entre Aluno (Domain) e DTOs
 * 
 * Princípios aplicados:
 * - Clean Architecture: isolamento entre camadas (domain não conhece DTOs)
 * - DDD: conversão explícita de/para Value Objects
 * - Anti-Corruption Layer: DTOs protegem o domínio de mudanças externas
 * 
 * @UtilityClass do Lombok gera construtor privado e métodos estáticos
 */
@UtilityClass
public class AlunoMapper {
    
    /**
     * Converte DTO de Request para Entidade de Domínio
     * Cria novos Value Objects a partir dos dados primitivos
     */
    public static Aluno toEntity(AlunoRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        
        return new Aluno(
            null, // ID será gerado pelo banco
            dto.getNome(),
            new Email(dto.getEmail()),
            new Matricula(dto.getMatricula())
        );
    }
    
    /**
     * Atualiza entidade existente com dados do UpdateDTO
     * Apenas campos não-nulos são atualizados (PATCH semântico)
     * DDD: preserva a identidade da entidade, só atualiza atributos
     */
    public static void updateEntityFromDto(Aluno entity, AlunoUpdateDTO dto) {
        if (dto == null) {
            return;
        }
        
        if (dto.getNome() != null) {
            entity.setNome(dto.getNome());
        }
        
        if (dto.getEmail() != null) {
            entity.setEmail(new Email(dto.getEmail()));
        }
        
        if (dto.getMatricula() != null) {
            entity.setMatricula(new Matricula(dto.getMatricula()));
        }
    }
    
    /**
     * Converte Entidade para DTO de Resposta resumido
     * Extrai valores dos Value Objects para tipos primitivos
     */
    public static AlunoResponseDTO toResponseDTO(Aluno entity) {
        if (entity == null) {
            return null;
        }
        
        return AlunoResponseDTO.builder()
            .id(entity.getId())
            .nome(entity.getNome())
            .email(entity.getEmail() != null ? entity.getEmail().getEmail() : null)
            .matricula(entity.getMatricula() != null ? entity.getMatricula().getMatricula() : null)
            .build();
    }
    
    /**
     * Converte Entidade para DTO de Resposta detalhado
     * Inclui metadados adicionais quando disponíveis
     */
    public static AlunoDetailResponseDTO toDetailResponseDTO(Aluno entity) {
        if (entity == null) {
            return null;
        }
        
        return AlunoDetailResponseDTO.builder()
            .id(entity.getId())
            .nome(entity.getNome())
            .email(entity.getEmail() != null ? entity.getEmail().getEmail() : null)
            .matricula(entity.getMatricula() != null ? entity.getMatricula().getMatricula() : null)
            .build();
    }
}
