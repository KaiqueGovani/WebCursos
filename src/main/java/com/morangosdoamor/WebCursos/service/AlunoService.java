package com.morangosdoamor.WebCursos.service;

import com.morangosdoamor.WebCursos.domain.Aluno;
import com.morangosdoamor.WebCursos.domain.valueobject.Email;
import com.morangosdoamor.WebCursos.domain.valueobject.Matricula;
import com.morangosdoamor.WebCursos.dto.mapper.AlunoMapper;
import com.morangosdoamor.WebCursos.dto.request.AlunoRequestDTO;
import com.morangosdoamor.WebCursos.dto.request.AlunoUpdateDTO;
import com.morangosdoamor.WebCursos.dto.response.AlunoDetailResponseDTO;
import com.morangosdoamor.WebCursos.dto.response.AlunoResponseDTO;
import com.morangosdoamor.WebCursos.exception.BusinessException;
import com.morangosdoamor.WebCursos.exception.ResourceNotFoundException;
import com.morangosdoamor.WebCursos.repository.AlunoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service de Aluno - Camada de Aplicação
 * 
 * Responsabilidades (Clean Architecture):
 * - Orquestrar casos de uso de negócio
 * - Validar regras de domínio
 * - Coordenar transações (@Transactional)
 * - Converter entre entidades e DTOs
 * 
 * DDD: Application Service que coordena operações sobre agregado Aluno
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AlunoService {
    
    private final AlunoRepository alunoRepository;
    
    /**
     * Cria novo aluno no sistema
     * Valida unicidade de email e matrícula
     * 
     * @param dto dados do aluno a criar
     * @return aluno criado
     * @throws BusinessException se email ou matrícula já existir
     */
    @Transactional
    public AlunoResponseDTO criar(AlunoRequestDTO dto) {
        validarEmailUnico(dto.getEmail());
        validarMatriculaUnica(dto.getMatricula());
        
        Aluno aluno = AlunoMapper.toEntity(dto);
        Aluno salvo = alunoRepository.save(aluno);
        
        return AlunoMapper.toResponseDTO(salvo);
    }
    
    /**
     * Busca aluno por ID
     * 
     * @param id identificador do aluno
     * @return detalhes completos do aluno
     * @throws ResourceNotFoundException se não encontrar
     */
    public AlunoDetailResponseDTO buscarPorId(String id) {
        Aluno aluno = alunoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Aluno", id));
        
        return AlunoMapper.toDetailResponseDTO(aluno);
    }
    
    /**
     * Busca aluno por email
     * DDD: consulta usando Value Object Email
     */
    public AlunoResponseDTO buscarPorEmail(String email) {
        Aluno aluno = alunoRepository.findByEmail(new Email(email))
            .orElseThrow(() -> new ResourceNotFoundException(
                String.format("Aluno não encontrado com email: %s", email)
            ));
        
        return AlunoMapper.toResponseDTO(aluno);
    }
    
    /**
     * Busca aluno por matrícula
     * DDD: consulta usando Value Object Matricula
     */
    public AlunoResponseDTO buscarPorMatricula(String matricula) {
        Aluno aluno = alunoRepository.findByMatricula(new Matricula(matricula))
            .orElseThrow(() -> new ResourceNotFoundException(
                String.format("Aluno não encontrado com matrícula: %s", matricula)
            ));
        
        return AlunoMapper.toResponseDTO(aluno);
    }
    
    /**
     * Lista todos os alunos
     */
    public List<AlunoResponseDTO> listarTodos() {
        return alunoRepository.findAll().stream()
            .map(AlunoMapper::toResponseDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * Atualiza dados do aluno
     * PATCH semântico: apenas campos fornecidos são alterados
     * 
     * @param id identificador do aluno
     * @param dto dados a atualizar
     * @return aluno atualizado
     */
    @Transactional
    public AlunoResponseDTO atualizar(String id, AlunoUpdateDTO dto) {
        Aluno aluno = alunoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Aluno", id));
        
        // Validar email único se estiver sendo alterado
        if (dto.getEmail() != null && !dto.getEmail().equals(aluno.getEmail().getEmail())) {
            validarEmailUnico(dto.getEmail());
        }
        
        // Validar matrícula única se estiver sendo alterada
        if (dto.getMatricula() != null && !dto.getMatricula().equals(aluno.getMatricula().getMatricula())) {
            validarMatriculaUnica(dto.getMatricula());
        }
        
        AlunoMapper.updateEntityFromDto(aluno, dto);
        Aluno atualizado = alunoRepository.save(aluno);
        
        return AlunoMapper.toResponseDTO(atualizado);
    }
    
    /**
     * Exclui aluno do sistema
     * Regra de negócio: poderia validar se há matrículas ativas
     * 
     * @param id identificador do aluno
     * @throws ResourceNotFoundException se não encontrar
     */
    @Transactional
    public void excluir(String id) {
        if (!alunoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Aluno", id);
        }
        
        alunoRepository.deleteById(id);
    }
    
    /**
     * Valida se email já está em uso
     * DDD: invariante do agregado Aluno - email deve ser único
     */
    private void validarEmailUnico(String email) {
        if (alunoRepository.existsByEmail(new Email(email))) {
            throw new BusinessException("Email já cadastrado: " + email);
        }
    }
    
    /**
     * Valida se matrícula já está em uso
     * DDD: invariante do agregado Aluno - matrícula deve ser única
     */
    private void validarMatriculaUnica(String matricula) {
        if (alunoRepository.existsByMatricula(new Matricula(matricula))) {
            throw new BusinessException("Matrícula já cadastrada: " + matricula);
        }
    }
}
