package com.morangosdoamor.WebCursos.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.morangosdoamor.WebCursos.domain.Aluno;
import com.morangosdoamor.WebCursos.domain.Curso;
import com.morangosdoamor.WebCursos.domain.valueobject.CargaHoraria;

public class CursoService {
    
    // In-memory data structures
    private Map<String, Curso> cursosDisponiveis; // cursoId -> Curso
    private Map<String, Set<String>> matriculas; // alunoId -> Set<cursoId>
    private Map<String, Map<String, Float>> notasFinais; // alunoId -> cursoId -> nota
    private Map<String, Set<String>> cursosFinalizados; // alunoId -> Set<cursoId>
    
    public CursoService() {
        initializeData();
    }
    
    private void initializeData() {
        cursosDisponiveis = new HashMap<>();
        matriculas = new HashMap<>();
        notasFinais = new HashMap<>();
        cursosFinalizados = new HashMap<>();
        
        // Inicializar cursos de exemplo - expandido para suportar o sistema de liberação
        Curso java = new Curso("JAVA001", "Programação Java", "Curso básico de Java", new CargaHoraria(40), new String[]{});
        Curso spring = new Curso("SPRING001", "Spring Framework", "Curso de Spring Boot", new CargaHoraria(60), new String[]{});
        Curso web = new Curso("WEB001", "Desenvolvimento Web", "HTML, CSS, JavaScript", new CargaHoraria(50), new String[]{});
        Curso react = new Curso("REACT001", "React.js", "Desenvolvimento com React", new CargaHoraria(45), new String[]{});
        Curso python = new Curso("PYTHON001", "Programação Python", "Curso básico de Python", new CargaHoraria(45), new String[]{});
        Curso django = new Curso("DJANGO001", "Django Framework", "Desenvolvimento web com Django", new CargaHoraria(55), new String[]{});
        Curso node = new Curso("NODE001", "Node.js", "Desenvolvimento backend com Node.js", new CargaHoraria(50), new String[]{});
        Curso angular = new Curso("ANGULAR001", "Angular", "Framework Angular para frontend", new CargaHoraria(60), new String[]{});
        Curso vue = new Curso("VUE001", "Vue.js", "Framework Vue.js para frontend", new CargaHoraria(45), new String[]{});
        Curso database = new Curso("DB001", "Banco de Dados", "Fundamentos de banco de dados", new CargaHoraria(40), new String[]{});
        
        cursosDisponiveis.put(java.getId(), java);
        cursosDisponiveis.put(spring.getId(), spring);
        cursosDisponiveis.put(web.getId(), web);
        cursosDisponiveis.put(react.getId(), react);
        cursosDisponiveis.put(python.getId(), python);
        cursosDisponiveis.put(django.getId(), django);
        cursosDisponiveis.put(node.getId(), node);
        cursosDisponiveis.put(angular.getId(), angular);
        cursosDisponiveis.put(vue.getId(), vue);
        cursosDisponiveis.put(database.getId(), database);
    }

    public void adicionarCurso(Aluno aluno, String cursoId) {
        validateAdicionarCursoParams(aluno, cursoId);
        validateCursoExists(cursoId);
        
        String alunoId = aluno.getId();
        validateAlunoNaoMatriculado(alunoId, cursoId);
        
        // Adicionar matrícula (sem verificar pré-requisitos para o novo sistema de liberação)
        matriculas.computeIfAbsent(alunoId, k -> new HashSet<>()).add(cursoId);
    }

    public ArrayList<Curso> getCursos(Aluno aluno) {
        if (aluno == null) {
            return new ArrayList<>();
        }
        
        String alunoId = aluno.getId();
        Set<String> cursosMatriculados = matriculas.getOrDefault(alunoId, new HashSet<>());
        
        ArrayList<Curso> cursos = new ArrayList<>();
        for (String cursoId : cursosMatriculados) {
            Curso curso = cursosDisponiveis.get(cursoId);
            if (curso != null) {
                cursos.add(curso);
            }
        }
        
        return cursos;
    }

    public void finalizarCurso(Aluno aluno, Curso curso, float nota) {
        validateFinalizarCursoParams(aluno, curso, nota);
        
        String alunoId = aluno.getId();
        String cursoId = curso.getId();
        
        validateAlunoMatriculado(alunoId, cursoId);
        validateCursoNaoFinalizado(alunoId, cursoId);
        
        processarFinalizacao(alunoId, cursoId, nota);
    }

    public ArrayList<Curso> findLiberadosByAluno(Aluno aluno) {
        if (aluno == null) {
            return new ArrayList<>();
        }
        
        String alunoId = aluno.getId();
        Set<String> cursosFinalizadosAluno = cursosFinalizados.getOrDefault(alunoId, new HashSet<>());
        Set<String> cursosMatriculados = matriculas.getOrDefault(alunoId, new HashSet<>());
        
        int cursosParaLiberar = calculateCursosParaLiberar(cursosFinalizadosAluno.size());
        
        return cursosParaLiberar == 0 ? new ArrayList<>() : 
               buscarCursosDisponiveis(cursosMatriculados, cursosFinalizadosAluno, cursosParaLiberar);
    }
    
    // Métodos auxiliares para consultas
    public Float getNota(Aluno aluno, Curso curso) {
        if (!isValidAlunoAndCurso(aluno, curso)) {
            return null;
        }
        
        Map<String, Float> notasAluno = notasFinais.get(aluno.getId());
        if (notasAluno == null) {
            return null;
        }
        
        return notasAluno.get(curso.getId());
    }
    
    public boolean isCursoFinalizado(Aluno aluno, Curso curso) {
        if (!isValidAlunoAndCurso(aluno, curso)) {
            return false;
        }
        
        return isCursoFinalizadoById(aluno.getId(), curso.getId());
    }
    
    public List<Curso> getAllCursos() {
        return new ArrayList<>(cursosDisponiveis.values());
    }
    
    // Métodos privados para reduzir complexidade ciclomática
    
    private void validateAdicionarCursoParams(Aluno aluno, String cursoId) {
        if (aluno == null || cursoId == null || cursoId.trim().isEmpty()) {
            throw new IllegalArgumentException("Aluno e ID do curso são obrigatórios");
        }
    }
    
    private void validateCursoExists(String cursoId) {
        if (!cursosDisponiveis.containsKey(cursoId)) {
            throw new IllegalArgumentException("Curso não encontrado: " + cursoId);
        }
    }
    
    private void validateAlunoNaoMatriculado(String alunoId, String cursoId) {
        if (isAlunoMatriculadoById(alunoId, cursoId)) {
            throw new IllegalStateException("Aluno já está matriculado neste curso");
        }
    }
    
    private void validateFinalizarCursoParams(Aluno aluno, Curso curso, float nota) {
        if (aluno == null || curso == null) {
            throw new IllegalArgumentException("Aluno e curso são obrigatórios");
        }
        if (nota < 0 || nota > 10) {
            throw new IllegalArgumentException("Nota deve estar entre 0 e 10");
        }
    }
    
    private void validateAlunoMatriculado(String alunoId, String cursoId) {
        if (!isAlunoMatriculadoById(alunoId, cursoId)) {
            throw new IllegalStateException("Aluno não está matriculado neste curso");
        }
    }
    
    private void validateCursoNaoFinalizado(String alunoId, String cursoId) {
        if (isCursoFinalizadoById(alunoId, cursoId)) {
            throw new IllegalStateException("Curso já foi finalizado");
        }
    }
    
    private void processarFinalizacao(String alunoId, String cursoId, float nota) {
        // Registrar nota
        notasFinais.computeIfAbsent(alunoId, k -> new HashMap<>()).put(cursoId, nota);
        
        // Se aprovado (nota >= 7), marcar como finalizado
        if (isNotaAprovacao(nota)) {
            cursosFinalizados.computeIfAbsent(alunoId, k -> new HashSet<>()).add(cursoId);
        }
        
        // Remover da lista de matrículas ativas
        matriculas.get(alunoId).remove(cursoId);
    }
    
    private boolean isNotaAprovacao(float nota) {
        return nota >= 7.0f;
    }
    
    private int calculateCursosParaLiberar(int cursosFinalizados) {
        return cursosFinalizados * 3;
    }
    
    private ArrayList<Curso> buscarCursosDisponiveis(Set<String> cursosMatriculados, 
                                                     Set<String> cursosFinalizados, 
                                                     int cursosParaLiberar) {
        ArrayList<Curso> cursosLiberados = new ArrayList<>();
        
        for (Curso curso : cursosDisponiveis.values()) {
            if (podeLiberar(curso.getId(), cursosMatriculados, cursosFinalizados)) {
                cursosLiberados.add(curso);
                
                if (cursosLiberados.size() >= cursosParaLiberar) {
                    break;
                }
            }
        }
        
        return cursosLiberados;
    }
    
    private boolean podeLiberar(String cursoId, Set<String> cursosMatriculados, Set<String> cursosFinalizados) {
        return !cursosMatriculados.contains(cursoId) && !cursosFinalizados.contains(cursoId);
    }
    
    private boolean isValidAlunoAndCurso(Aluno aluno, Curso curso) {
        return aluno != null && curso != null;
    }
    
    private boolean isCursoFinalizadoById(String alunoId, String cursoId) {
        Set<String> finalizados = cursosFinalizados.get(alunoId);
        return finalizados != null && finalizados.contains(cursoId);
    }
    
    private boolean isAlunoMatriculadoById(String alunoId, String cursoId) {
        return matriculas.containsKey(alunoId) && matriculas.get(alunoId).contains(cursoId);
    }

}
