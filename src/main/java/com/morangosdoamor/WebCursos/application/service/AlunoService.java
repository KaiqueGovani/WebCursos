package com.morangosdoamor.WebCursos.application.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.morangosdoamor.WebCursos.api.dto.AlunoUpdateRequest;
import com.morangosdoamor.WebCursos.domain.entity.Aluno;
import com.morangosdoamor.WebCursos.domain.exception.BusinessRuleException;
import com.morangosdoamor.WebCursos.domain.exception.ResourceNotFoundException;
import com.morangosdoamor.WebCursos.domain.valueobject.Email;
import com.morangosdoamor.WebCursos.infrastructure.repository.AlunoRepository;

import lombok.RequiredArgsConstructor;

/**
 * Serviço responsável pela gestão de alunos na plataforma.
 * 
 * Princípios aplicados:
 * - Clean Architecture: encapsula regras de negócio relacionadas a alunos
 * - DDD: opera sobre entidades de domínio preservando invariantes
 * - Transaction Management: métodos transacionais garantem consistência de dados
 * 
 * Responsabilidades:
 * - Validação de unicidade de matrícula e email
 * - CRUD completo de alunos
 * - Busca por diferentes critérios (ID, email, matrícula)
 */
@Service
@RequiredArgsConstructor
public class AlunoService {

    private final AlunoRepository alunoRepository;

    /**
     * Cria um novo aluno no sistema.
     * Valida unicidade da matrícula antes de persistir.
     * Registra automaticamente a data de criação se não estiver definida.
     * 
     * @param aluno Entidade de domínio Aluno a ser criada
     * @return Aluno criado e persistido
     * @throws BusinessRuleException se a matrícula já estiver cadastrada para outro aluno
     */
    @Transactional
    public Aluno criar(Aluno aluno) {
        alunoRepository.findByMatricula(aluno.getMatricula())
            .ifPresent(existing -> {
                throw new BusinessRuleException("Matrícula já cadastrada para outro aluno");
            });

        aluno.registrarCriacaoSeNecessario();
        return alunoRepository.save(aluno);
    }

    /**
     * Busca um aluno por seu identificador único.
     * Inicializa a coleção lazy de matrículas para visualização detalhada.
     * 
     * @param alunoId UUID do aluno a ser buscado
     * @return Aluno encontrado com matrículas inicializadas
     * @throws ResourceNotFoundException se o aluno não for encontrado
     */
    @Transactional(readOnly = true)
    public Aluno buscarPorId(UUID alunoId) {
        Aluno aluno = alunoRepository.findById(alunoId)
            .orElseThrow(() -> new ResourceNotFoundException("Aluno não encontrado"));
        // Force initialization of lazy collection for detail view
        aluno.getMatriculas().size();
        return aluno;
    }

    /**
     * Lista todos os alunos cadastrados no sistema.
     * Retorna lista ordenada alfabeticamente por nome.
     * 
     * @return Lista de todos os alunos ordenados por nome
     */
    @Transactional(readOnly = true)
    public List<Aluno> listarTodos() {
        return alunoRepository.findAll(Sort.by("nome").ascending());
    }

    /**
     * Busca um aluno pelo endereço de email.
     * 
     * @param email Endereço de email do aluno (deve ser único no sistema)
     * @return Aluno encontrado com o email informado
     * @throws ResourceNotFoundException se nenhum aluno for encontrado com o email informado
     */
    @Transactional(readOnly = true)
    public Aluno buscarPorEmail(String email) {
        return alunoRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("Aluno não encontrado"));
    }

    /**
     * Busca um aluno pelo número de matrícula.
     * 
     * @param matricula Número de matrícula do aluno (deve ser único no sistema)
     * @return Aluno encontrado com a matrícula informada
     * @throws ResourceNotFoundException se nenhum aluno for encontrado com a matrícula informada
     */
    @Transactional(readOnly = true)
    public Aluno buscarPorMatricula(String matricula) {
        return alunoRepository.findByMatricula(matricula)
            .orElseThrow(() -> new ResourceNotFoundException("Aluno não encontrado"));
    }

    /**
     * Atualiza dados de um aluno existente (atualização parcial - PATCH semântico).
     * Apenas campos não-nulos e não-vazios do DTO são atualizados.
     * Valida unicidade de email e matrícula antes de atualizar.
     * 
     * @param id UUID do aluno a ser atualizado
     * @param dto DTO contendo os campos a serem atualizados (campos nulos/vazios são ignorados)
     * @return Aluno atualizado e persistido
     * @throws ResourceNotFoundException se o aluno não for encontrado
     * @throws BusinessRuleException se email ou matrícula já estiverem cadastrados para outro aluno
     */
    @Transactional
    public Aluno atualizar(UUID id, AlunoUpdateRequest dto) {
        Aluno aluno = buscarPorId(id);

        if (dto.nome() != null && !dto.nome().isBlank()) {
            aluno.setNome(dto.nome());
        }

        if (dto.email() != null && !dto.email().isBlank()) {
            alunoRepository.findByEmail(dto.email())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new BusinessRuleException("Email já cadastrado para outro aluno");
                });
            aluno.setEmail(new Email(dto.email()));
        }

        if (dto.matricula() != null && !dto.matricula().isBlank()) {
            alunoRepository.findByMatricula(dto.matricula())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new BusinessRuleException("Matrícula já cadastrada para outro aluno");
                });
            aluno.setMatricula(dto.matricula());
        }

        return alunoRepository.save(aluno);
    }

    /**
     * Exclui um aluno do sistema.
     * A exclusão é em cascata: todas as matrículas do aluno também são removidas.
     * 
     * @param id UUID do aluno a ser excluído
     * @throws ResourceNotFoundException se o aluno não for encontrado
     */
    @Transactional
    public void excluir(UUID id) {
        Aluno aluno = buscarPorId(id);
        alunoRepository.delete(aluno);
    }
}
