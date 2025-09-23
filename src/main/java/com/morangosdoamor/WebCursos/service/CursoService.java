package com.morangosdoamor.WebCursos.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.morangosdoamor.WebCursos.domain.Aluno;
import com.morangosdoamor.WebCursos.domain.Curso;

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
        
        // Inicializar alguns cursos de exemplo
        Curso java = new Curso("JAVA001", "Programação Java", "Curso básico de Java", 40, new String[]{});
        Curso spring = new Curso("SPRING001", "Spring Framework", "Curso de Spring Boot", 60, new String[]{"JAVA001"});
        Curso web = new Curso("WEB001", "Desenvolvimento Web", "HTML, CSS, JavaScript", 50, new String[]{});
        Curso react = new Curso("REACT001", "React.js", "Desenvolvimento com React", 45, new String[]{"WEB001", "JAVA001"});
        
        cursosDisponiveis.put(java.getId(), java);
        cursosDisponiveis.put(spring.getId(), spring);
        cursosDisponiveis.put(web.getId(), web);
        cursosDisponiveis.put(react.getId(), react);
    }

    public void adicionarCurso(Aluno aluno, String cursoId) {
        if (aluno == null || cursoId == null || cursoId.trim().isEmpty()) {
            throw new IllegalArgumentException("Aluno e ID do curso são obrigatórios");
        }
        
        if (!cursosDisponiveis.containsKey(cursoId)) {
            throw new IllegalArgumentException("Curso não encontrado: " + cursoId);
        }
        
        String alunoId = aluno.getId();
        
        // Verificar se o aluno já está matriculado no curso
        if (matriculas.containsKey(alunoId) && matriculas.get(alunoId).contains(cursoId)) {
            throw new IllegalStateException("Aluno já está matriculado neste curso");
        }
        
        // Verificar pré-requisitos
        Curso curso = cursosDisponiveis.get(cursoId);
        if (curso.getPrerequisitos() != null && curso.getPrerequisitos().length > 0) {
            Set<String> cursosFinalizadosAluno = cursosFinalizados.getOrDefault(alunoId, new HashSet<>());
            for (String prerequisito : curso.getPrerequisitos()) {
                if (!cursosFinalizadosAluno.contains(prerequisito)) {
                    throw new IllegalStateException("Pré-requisito não atendido: " + prerequisito);
                }
            }
        }
        
        // Adicionar matrícula
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
        if (aluno == null || curso == null) {
            throw new IllegalArgumentException("Aluno e curso são obrigatórios");
        }
        
        if (nota < 0 || nota > 10) {
            throw new IllegalArgumentException("Nota deve estar entre 0 e 10");
        }
        
        String alunoId = aluno.getId();
        String cursoId = curso.getId();
        
        // Verificar se o aluno está matriculado no curso
        if (!matriculas.containsKey(alunoId) || !matriculas.get(alunoId).contains(cursoId)) {
            throw new IllegalStateException("Aluno não está matriculado neste curso");
        }
        
        // Verificar se o curso já foi finalizado
        if (cursosFinalizados.containsKey(alunoId) && cursosFinalizados.get(alunoId).contains(cursoId)) {
            throw new IllegalStateException("Curso já foi finalizado");
        }
        
        // Registrar nota
        notasFinais.computeIfAbsent(alunoId, k -> new HashMap<>()).put(cursoId, nota);
        
        // Se aprovado (nota >= 7), marcar como finalizado
        if (nota >= 7.0f) {
            cursosFinalizados.computeIfAbsent(alunoId, k -> new HashSet<>()).add(cursoId);
        }
        
        // Remover da lista de matrículas ativas
        matriculas.get(alunoId).remove(cursoId);
    }

    public ArrayList<Curso> findLiberadosByAluno(Aluno aluno) {
        if (aluno == null) {
            return new ArrayList<>();
        }
        
        String alunoId = aluno.getId();
        Set<String> cursosFinalizadosAluno = cursosFinalizados.getOrDefault(alunoId, new HashSet<>());
        Set<String> cursosMatriculados = matriculas.getOrDefault(alunoId, new HashSet<>());
        
        ArrayList<Curso> cursosLiberados = new ArrayList<>();
        
        for (Curso curso : cursosDisponiveis.values()) {
            String cursoId = curso.getId();
            
            // Pular se já está matriculado ou já finalizou
            if (cursosMatriculados.contains(cursoId) || cursosFinalizadosAluno.contains(cursoId)) {
                continue;
            }
            
            // Verificar pré-requisitos
            boolean podeMatricular = true;
            if (curso.getPrerequisitos() != null && curso.getPrerequisitos().length > 0) {
                for (String prerequisito : curso.getPrerequisitos()) {
                    if (!cursosFinalizadosAluno.contains(prerequisito)) {
                        podeMatricular = false;
                        break;
                    }
                }
            }
            
            if (podeMatricular) {
                cursosLiberados.add(curso);
            }
        }
        
        return cursosLiberados;
    }
    
    // Métodos auxiliares para consultas
    public Float getNota(Aluno aluno, Curso curso) {
        if (aluno == null || curso == null) {
            return null;
        }
        
        Map<String, Float> notasAluno = notasFinais.get(aluno.getId());
        if (notasAluno == null) {
            return null;
        }
        
        return notasAluno.get(curso.getId());
    }
    
    public boolean isCursoFinalizado(Aluno aluno, Curso curso) {
        if (aluno == null || curso == null) {
            return false;
        }
        
        Set<String> finalizados = cursosFinalizados.get(aluno.getId());
        return finalizados != null && finalizados.contains(curso.getId());
    }
    
    public List<Curso> getAllCursos() {
        return new ArrayList<>(cursosDisponiveis.values());
    }

}
