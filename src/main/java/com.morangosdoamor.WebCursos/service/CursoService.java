import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.morangosdoamor.WebCursos.model.entity.Aluno;
import com.morangosdoamor.WebCursos.model.entity.Curso;
import com.morangosdoamor.WebCursos.model.entity.Matricula;
import com.morangosdoamor.WebCursos.model.entity.Matricula.StatusMatricula;
import com.morangosdoamor.WebCursos.model.valueobject.Nota;
import com.morangosdoamor.WebCursos.repository.CursoRepository;
import com.morangosdoamor.WebCursos.repository.MatriculaRepository;

@Service
public class CursoService {

    @Autowired
    private CursoRepository cursoRepository;

    @Autowired
    private MatriculaRepository matriculaRepository;
    
    @Transactional
    public void adicionarCurso(Aluno aluno, String cursoId) {
        Curso curso = cursoRepository.findById(cursoId)
                .orElseThrow(() -> new IllegalArgumentException("Curso não encontrado: " + cursoId));

        if (matriculaRepository.existsByAlunoAndCurso(aluno, curso)) {
            throw new IllegalStateException("Aluno já está matriculado neste curso");
        }
        
        Matricula novaMatricula = new Matricula(aluno, curso);
        matriculaRepository.save(novaMatricula);
    }

    @Transactional(readOnly = true)
    public List<Curso> getCursos(Aluno aluno) {
        return matriculaRepository.findByAlunoAndStatus(aluno, StatusMatricula.EM_ANDAMENTO)
                .stream()
                .map(Matricula::getCurso)
                .collect(Collectors.toList());
    }

    @Transactional
    public void finalizarCurso(Aluno aluno, Curso curso, float notaValor) {
        Matricula matricula = matriculaRepository.findByAlunoAndCurso(aluno, curso)
                .orElseThrow(() -> new IllegalStateException("Aluno não está matriculado neste curso"));

        if (matricula.getStatus() != StatusMatricula.EM_ANDAMENTO) {
            throw new IllegalStateException("Curso não está em andamento");
        }
        
        Nota nota = new Nota(notaValor);
        matricula.setNotaFinal(nota);
        matricula.setStatus(nota.isAprovado() ? StatusMatricula.CONCLUIDO : StatusMatricula.REPROVADO);
        
        matriculaRepository.save(matricula);
    }

    @Transactional(readOnly = true)
    public List<Curso> findLiberadosByAluno(Aluno aluno) {
        long cursosFinalizadosCount = matriculaRepository.countByAlunoAndStatus(aluno, StatusMatricula.CONCLUIDO);
        long cursosParaLiberar = cursosFinalizadosCount * 3;

        if (cursosParaLiberar == 0) {
            return new ArrayList<>();
        }

        List<String> idsCursosOcupados = matriculaRepository.findByAluno(aluno).stream()
            .map(m -> m.getCurso().getId())
            .collect(Collectors.toList());

        List<Curso> cursosDisponiveis = cursoRepository.findAll().stream()
            .filter(curso -> !idsCursosOcupados.contains(curso.getId()))
            .limit(cursosParaLiberar)
            .collect(Collectors.toList());
            
        return cursosDisponiveis;
    }
    
    @Transactional(readOnly = true)
    public Nota getNota(Aluno aluno, Curso curso) {
        return matriculaRepository.findByAlunoAndCurso(aluno, curso)
                .map(Matricula::getNotaFinal)
                .orElse(null);
    }
    
    @Transactional(readOnly = true)
    public boolean isCursoFinalizado(Aluno aluno, Curso curso) {
        return matriculaRepository.findByAlunoAndCurso(aluno, curso)
                .map(m -> m.getStatus() == StatusMatricula.CONCLUIDO)
                .orElse(false);
    }
    
    @Transactional(readOnly = true)
    public List<Curso> getAllCursos() {
        return cursoRepository.findAll();
    }
}
