package com.morangosdoamor.WebCursos.application.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.morangosdoamor.WebCursos.domain.entity.Aluno;
import com.morangosdoamor.WebCursos.domain.entity.Curso;
import com.morangosdoamor.WebCursos.domain.entity.Matricula;
import com.morangosdoamor.WebCursos.domain.enums.MatriculaStatus;
import com.morangosdoamor.WebCursos.domain.exception.BusinessRuleException;
import com.morangosdoamor.WebCursos.domain.exception.ResourceNotFoundException;
import com.morangosdoamor.WebCursos.infrastructure.messaging.event.CursoConcluidoEvent;
import com.morangosdoamor.WebCursos.infrastructure.messaging.publisher.CursoConcluidoEventPublisher;
import com.morangosdoamor.WebCursos.infrastructure.repository.AlunoRepository;
import com.morangosdoamor.WebCursos.infrastructure.repository.CursoRepository;
import com.morangosdoamor.WebCursos.infrastructure.repository.MatriculaRepository;

import lombok.RequiredArgsConstructor;

/**
 * Serviço responsável pela gestão de matrículas de alunos em cursos.
 * 
 * Princípios aplicados:
 * - Clean Architecture: encapsula regras de negócio relacionadas a matrículas
 * - DDD: opera sobre entidades de domínio preservando invariantes
 * - Transaction Management: métodos transacionais garantem consistência de dados
 * - Event-Driven: publica eventos de conclusão de curso para processamento assíncrono
 * 
 * Responsabilidades:
 * - Matrícula de alunos em cursos
 * - Conclusão de cursos com registro de nota final
 * - Publicação de eventos de conclusão de curso
 * - Validação de regras de negócio (nota entre 0 e 10, evitar matrícula duplicada)
 * - Consulta de matrículas e notas finais
 */
@Service
@RequiredArgsConstructor
public class MatriculaService {

    private final AlunoRepository alunoRepository;
    private final CursoRepository cursoRepository;
    private final MatriculaRepository matriculaRepository;
    private final CursoConcluidoEventPublisher eventPublisher;

    /**
     * Matricula um aluno em um curso.
     * Valida se o aluno já não está matriculado no curso antes de criar a matrícula.
     * Registra automaticamente a data de matrícula e define o status como MATRICULADO.
     * 
     * @param alunoId UUID do aluno a ser matriculado
     * @param codigoCurso Código único do curso (ex: "JAVA001")
     * @return Matrícula criada e persistida
     * @throws ResourceNotFoundException se aluno ou curso não forem encontrados
     * @throws BusinessRuleException se o aluno já estiver matriculado no curso
     */
    @Transactional
    public Matricula matricular(UUID alunoId, String codigoCurso) {
        Aluno aluno = alunoRepository.findById(alunoId)
            .orElseThrow(() -> new ResourceNotFoundException("Aluno não encontrado"));
        Curso curso = cursoRepository.findByCodigo(codigoCurso)
            .orElseThrow(() -> new ResourceNotFoundException("Curso não encontrado"));

        if (matriculaRepository.existsByAlunoIdAndCursoId(aluno.getId(), curso.getId())) {
            throw new BusinessRuleException("Aluno já matriculado ou curso concluído anteriormente");
        }

        Matricula matricula = Matricula.builder()
            .aluno(aluno)
            .curso(curso)
            .status(MatriculaStatus.MATRICULADO)
            .build();

        matricula.registrarMatricula();
        aluno.adicionarMatricula(matricula);

        return matriculaRepository.save(matricula);
    }

    /**
     * Conclui uma matrícula registrando a nota final do aluno.
     * Valida se a nota está no intervalo válido (0 a 10).
     * Valida se a matrícula ainda não foi concluída.
     * Registra automaticamente a data de conclusão e atualiza o status para CONCLUIDO.
     * Publica um evento de conclusão de curso para processamento assíncrono.
     * 
     * @param alunoId UUID do aluno proprietário da matrícula
     * @param matriculaId UUID da matrícula a ser concluída
     * @param notaFinal Nota final do curso (deve estar entre 0 e 10)
     * @return Matrícula atualizada com nota final e data de conclusão
     * @throws ResourceNotFoundException se a matrícula não for encontrada para o aluno informado
     * @throws BusinessRuleException se a nota estiver fora do intervalo válido ou se o curso já estiver concluído
     */
    @Transactional
    public Matricula concluir(UUID alunoId, UUID matriculaId, double notaFinal) {
        if (notaFinal < 0 || notaFinal > 10) {
            throw new BusinessRuleException("Nota final deve estar entre 0 e 10");
        }

        Matricula matricula = matriculaRepository.findByIdAndAlunoId(matriculaId, alunoId)
            .orElseThrow(() -> new ResourceNotFoundException("Matrícula não encontrada para o aluno informado"));

        if (MatriculaStatus.CONCLUIDO.equals(matricula.getStatus())) {
            throw new BusinessRuleException("O curso já está concluído");
        }

        matricula.concluir(notaFinal);

        // Publica evento de conclusão de curso para processamento assíncrono
        publishCursoConcluidoEvent(matricula);

        return matricula;
    }

    /**
     * Publica um evento de conclusão de curso no RabbitMQ.
     * O evento é publicado independentemente se o aluno foi aprovado ou não,
     * permitindo que os consumidores decidam como processar cada caso.
     * 
     * @param matricula Matrícula concluída com todos os dados necessários
     */
    private void publishCursoConcluidoEvent(Matricula matricula) {
        Aluno aluno = matricula.getAluno();
        Curso curso = matricula.getCurso();

        CursoConcluidoEvent event = CursoConcluidoEvent.of(
            aluno.getId(),
            aluno.getNome(),
            aluno.getEmail() != null ? aluno.getEmail().getValue() : null,
            curso.getId(),
            curso.getNome(),
            curso.getCodigo(),
            matricula.getNotaFinal(),
            matricula.getDataConclusao()
        );

        eventPublisher.publish(event);
    }

    /**
     * Lista todas as matrículas de um aluno.
     * Valida a existência do aluno antes de buscar as matrículas.
     * 
     * @param alunoId UUID do aluno
     * @return Lista de todas as matrículas do aluno (matriculados e concluídos)
     * @throws ResourceNotFoundException se o aluno não for encontrado
     */
    @Transactional(readOnly = true)
    public List<Matricula> listarPorAluno(UUID alunoId) {
        validarExistenciaAluno(alunoId);
        return matriculaRepository.findAllByAlunoId(alunoId);
    }

    /**
     * Busca a nota final de uma matrícula específica.
     * Retorna null se a matrícula não for encontrada ou se ainda não tiver nota final.
     * 
     * @param alunoId UUID do aluno proprietário da matrícula
     * @param matriculaId UUID da matrícula
     * @return Nota final da matrícula, ou null se não encontrada ou sem nota
     */
    @Transactional(readOnly = true)
    public Double buscarNotaFinal(UUID alunoId, UUID matriculaId) {
        return matriculaRepository.findByIdAndAlunoId(matriculaId, alunoId)
            .map(Matricula::getNotaFinal)
            .orElse(null);
    }

    /**
     * Valida se um aluno existe no sistema.
     * Método auxiliar para garantir consistência antes de operações relacionadas a matrículas.
     * 
     * @param alunoId UUID do aluno a ser validado
     * @throws ResourceNotFoundException se o aluno não existir
     */
    private void validarExistenciaAluno(UUID alunoId) {
        if (!alunoRepository.existsById(alunoId)) {
            throw new ResourceNotFoundException("Aluno não encontrado");
        }
    }
}

