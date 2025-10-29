package com.morangosdoamor.WebCursos.service;

import com.morangosdoamor.WebCursos.domain.Curso;
import com.morangosdoamor.WebCursos.domain.valueobject.CargaHoraria;
import com.morangosdoamor.WebCursos.dto.mapper.CursoMapper;
import com.morangosdoamor.WebCursos.dto.request.CursoRequestDTO;
import com.morangosdoamor.WebCursos.dto.request.CursoUpdateDTO;
import com.morangosdoamor.WebCursos.dto.response.CursoDetailResponseDTO;
import com.morangosdoamor.WebCursos.dto.response.CursoResponseDTO;
import com.morangosdoamor.WebCursos.exception.BusinessException;
import com.morangosdoamor.WebCursos.exception.ResourceNotFoundException;
import com.morangosdoamor.WebCursos.repository.CursoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service de Curso - Camada de Aplicação
 * 
 * Responsabilidades (Clean Architecture):
 * - Orquestrar casos de uso
 * - Validar regras de negócio
 * - Coordenar transações
 * - Converter entre Domain e DTOs
 * 
 * DDD: Application Service que coordena agregados
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CursoService {
    
    private final CursoRepository cursoRepository;
    
    /**
     * Cria um novo curso
     * @Transactional para garantir consistência
     */
    @Transactional
    public CursoResponseDTO criar(CursoRequestDTO dto) {
        validarNomeDuplicado(dto.getNome());
        
        Curso curso = CursoMapper.toEntity(dto);
        Curso salvo = cursoRepository.save(curso);
        
        return CursoMapper.toResponseDTO(salvo);
    }
    
    /**
     * Busca curso por ID
     * Retorna detalhes completos incluindo conversões de carga horária
     */
    public CursoDetailResponseDTO buscarPorId(String id) {
        Curso curso = cursoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Curso", id));
        
        return CursoMapper.toDetailResponseDTO(curso);
    }
    
    /**
     * Lista todos os cursos
     */
    public List<CursoResponseDTO> listarTodos() {
        return cursoRepository.findAll().stream()
            .map(CursoMapper::toResponseDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * Busca cursos por carga horária mínima
     * DDD: expõe consulta específica do domínio
     */
    public List<CursoResponseDTO> buscarPorCargaHorariaMinima(Integer horasMinimas) {
        if (horasMinimas == null || horasMinimas < 0) {
            throw new BusinessException("Carga horária mínima deve ser maior ou igual a zero");
        }
        
        return cursoRepository.findByCargaHorariaMinima(horasMinimas).stream()
            .map(CursoMapper::toResponseDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * Busca cursos por carga horária máxima
     */
    public List<CursoResponseDTO> buscarPorCargaHorariaMaxima(Integer horasMaximas) {
        if (horasMaximas == null || horasMaximas < 0) {
            throw new BusinessException("Carga horária máxima deve ser maior ou igual a zero");
        }
        
        return cursoRepository.findByCargaHorariaMaxima(horasMaximas).stream()
            .map(CursoMapper::toResponseDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * Atualiza curso existente
     * PATCH semântico: apenas campos fornecidos são atualizados
     */
    @Transactional
    public CursoResponseDTO atualizar(String id, CursoUpdateDTO dto) {
        Curso curso = cursoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Curso", id));
        
        // Validar nome duplicado se estiver sendo alterado
        if (dto.getNome() != null && !dto.getNome().equals(curso.getNome())) {
            validarNomeDuplicado(dto.getNome());
        }
        
        CursoMapper.updateEntityFromDto(curso, dto);
        Curso atualizado = cursoRepository.save(curso);
        
        return CursoMapper.toResponseDTO(atualizado);
    }
    
    /**
     * Exclui curso por ID
     * Regra de negócio: poderia validar se há alunos matriculados
     */
    @Transactional
    public void excluir(String id) {
        if (!cursoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Curso", id);
        }
        
        cursoRepository.deleteById(id);
    }
    
    /**
     * Valida se já existe curso com o mesmo nome
     * DDD: invariante do agregado Curso
     */
    private void validarNomeDuplicado(String nome) {
        if (cursoRepository.existsByNome(nome)) {
            throw new BusinessException("Já existe um curso com o nome: " + nome);
        }
    }
}
