package com.morangosdoamor.WebCursos.application.processor;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.morangosdoamor.WebCursos.application.dto.CursoCompletoDTO;
import com.morangosdoamor.WebCursos.application.dto.CursoDisponivelDTO;
import com.morangosdoamor.WebCursos.application.service.AiRecommendationService;
import com.morangosdoamor.WebCursos.domain.entity.Curso;
import com.morangosdoamor.WebCursos.domain.entity.Matricula;
import com.morangosdoamor.WebCursos.domain.enums.MatriculaStatus;
import com.morangosdoamor.WebCursos.infrastructure.messaging.event.CursoConcluidoEvent;
import com.morangosdoamor.WebCursos.infrastructure.repository.CursoRepository;
import com.morangosdoamor.WebCursos.infrastructure.repository.MatriculaRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Processador responsável por orquestrar a geração de recomendações de cursos
 * quando um aluno conclui um curso.
 * 
 * Princípios aplicados:
 * - Clean Architecture: camada de aplicação orquestrando serviços
 * - Single Responsibility: focado apenas em processar eventos de conclusão
 * - Dependency Inversion: depende de abstrações (repositories, services)
 * 
 * Responsabilidades:
 * - Buscar histórico de cursos do aluno
 * - Buscar cursos disponíveis para recomendação
 * - Chamar serviço de IA para gerar mensagem personalizada
 * - Retornar mensagem de recomendação
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CursoConcluidoProcessor {

    private final MatriculaRepository matriculaRepository;
    private final CursoRepository cursoRepository;
    private final AiRecommendationService aiRecommendationService;

    /**
     * Processa um evento de conclusão de curso, gerando uma recomendação personalizada.
     * 
     * Fluxo:
     * 1. Busca os últimos 3 cursos concluídos pelo aluno
     * 2. Busca cursos disponíveis (não matriculados)
     * 3. Gera recomendação via IA
     * 4. Retorna mensagem de recomendação
     * 
     * @param event Evento de conclusão de curso
     * @return Mensagem de recomendação gerada
     */
    @Transactional(readOnly = true)
    public String process(CursoConcluidoEvent event) {
        log.info("Processando conclusão de curso para aluno: {} ({}), curso: {} ({})",
                event.alunoNome(), event.alunoId(), event.cursoNome(), event.cursoCodigo());

        // 1. Buscar últimos cursos concluídos
        List<CursoCompletoDTO> ultimosCursos = fetchUltimosCursos(event.alunoId());
        log.debug("Últimos {} cursos concluídos encontrados para aluno {}", 
                ultimosCursos.size(), event.alunoId());

        // 2. Buscar cursos disponíveis
        List<CursoDisponivelDTO> cursosDisponiveis = fetchCursosDisponiveis(event.alunoId());
        log.debug("{} cursos disponíveis para recomendação", cursosDisponiveis.size());

        // 3. Gerar recomendação via IA
        String mensagemRecomendacao = aiRecommendationService.generateRecommendation(
                event.alunoNome(),
                event.cursoNome(),
                event.notaFinal(),
                ultimosCursos,
                cursosDisponiveis
        );

        log.info("Recomendação gerada com sucesso para aluno: {}", event.alunoNome());

        return mensagemRecomendacao;
    }

    /**
     * Busca os últimos 3 cursos concluídos pelo aluno.
     */
    private List<CursoCompletoDTO> fetchUltimosCursos(java.util.UUID alunoId) {
        List<Matricula> matriculas = matriculaRepository
                .findTop3ByAlunoIdAndStatusOrderByDataConclusaoDesc(alunoId, MatriculaStatus.CONCLUIDO);

        return matriculas.stream()
                .map(this::toCursoCompletoDTO)
                .toList();
    }

    /**
     * Busca cursos disponíveis para o aluno (não matriculados).
     */
    private List<CursoDisponivelDTO> fetchCursosDisponiveis(java.util.UUID alunoId) {
        List<Curso> cursos = cursoRepository.findCursosNotEnrolledByAluno(alunoId);

        return cursos.stream()
                .map(this::toCursoDisponivelDTO)
                .toList();
    }

    /**
     * Converte Matricula para CursoCompletoDTO.
     */
    private CursoCompletoDTO toCursoCompletoDTO(Matricula matricula) {
        Curso curso = matricula.getCurso();
        return new CursoCompletoDTO(
                curso.getNome(),
                curso.getCodigo(),
                matricula.getNotaFinal()
        );
    }

    /**
     * Converte Curso para CursoDisponivelDTO.
     */
    private CursoDisponivelDTO toCursoDisponivelDTO(Curso curso) {
        return new CursoDisponivelDTO(
                curso.getNome(),
                curso.getCodigo(),
                curso.getDescricao(),
                curso.getCargaHoraria().getCargaHoraria()
        );
    }
}


