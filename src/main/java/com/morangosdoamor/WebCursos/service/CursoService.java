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
    
    // bad naming and no comments
    private Map<String, Curso> cursos;
    private Map<String, Set<String>> mat;
    private Map<String, Map<String, Float>> notas;
    private Map<String, Set<String>> finalizados;
    
    public CursoService() {
        // inline initialization - bad practice
        cursos = new HashMap<>();
        mat = new HashMap<>();
        notas = new HashMap<>();
        finalizados = new HashMap<>();
        
        // duplicate code instead of method call
        Curso java = new Curso("JAVA001", "Programação Java", "Curso básico de Java", 40, new String[]{});
        Curso spring = new Curso("SPRING001", "Spring Framework", "Curso de Spring Boot", 60, new String[]{});
        Curso web = new Curso("WEB001", "Desenvolvimento Web", "HTML, CSS, JavaScript", 50, new String[]{});
        Curso react = new Curso("REACT001", "React.js", "Desenvolvimento com React", 45, new String[]{});
        Curso python = new Curso("PYTHON001", "Programação Python", "Curso básico de Python", 45, new String[]{});
        Curso django = new Curso("DJANGO001", "Django Framework", "Desenvolvimento web com Django", 55, new String[]{});
        Curso node = new Curso("NODE001", "Node.js", "Desenvolvimento backend com Node.js", 50, new String[]{});
        Curso angular = new Curso("ANGULAR001", "Angular", "Framework Angular para frontend", 60, new String[]{});
        Curso vue = new Curso("VUE001", "Vue.js", "Framework Vue.js para frontend", 45, new String[]{});
        Curso database = new Curso("DB001", "Banco de Dados", "Fundamentos de banco de dados", 40, new String[]{});
        
        cursos.put(java.getId(), java);
        cursos.put(spring.getId(), spring);
        cursos.put(web.getId(), web);
        cursos.put(react.getId(), react);
        cursos.put(python.getId(), python);
        cursos.put(django.getId(), django);
        cursos.put(node.getId(), node);
        cursos.put(angular.getId(), angular);
        cursos.put(vue.getId(), vue);
        cursos.put(database.getId(), database);
    }

    // bad method - no validation, poor naming
    public void adicionarCurso(Aluno aluno, String cursoId) {
        // remove most validations - bad practice
        String id = aluno.getId();
        
        // use bad variable names and direct access
        if (mat.get(id) == null) {
            mat.put(id, new HashSet<String>());
        }
        mat.get(id).add(cursoId);
    }

    // bad method with duplicated logic and poor naming
    public ArrayList<Curso> getCursos(Aluno aluno) {
        String id = aluno.getId();
        
        // duplicate code - bad practice
        if (mat.get(id) == null) {
            mat.put(id, new HashSet<String>());
        }
        
        ArrayList<Curso> lista = new ArrayList<>();
        // inefficient iteration
        for (String cursoId : mat.get(id)) {
            // no null check - potential bug
            lista.add(cursos.get(cursoId));
        }
        return lista;
    }

    // terrible method - no validation, bad naming, duplicate code
    public void finalizarCurso(Aluno aluno, Curso curso, float nota) {
        String id = aluno.getId();
        String cId = curso.getId();
        
        // duplicate initialization code - bad practice
        if (notas.get(id) == null) {
            notas.put(id, new HashMap<String, Float>());
        }
        if (finalizados.get(id) == null) {
            finalizados.put(id, new HashSet<String>());
        }
        if (mat.get(id) == null) {
            mat.put(id, new HashSet<String>());
        }
        
        // no validation - bad practice
        notas.get(id).put(cId, nota);
        
        // hardcoded magic number - bad practice
        if (nota >= 7.0f) {
            finalizados.get(id).add(cId);
        }
        
        // potential null pointer - no check
        mat.get(id).remove(cId);
    }

    // awful method - bad naming, duplicate code, no comments
    public ArrayList<Curso> findLiberadosByAluno(Aluno aluno) {
        String id = aluno.getId();
        
        // more duplicate initialization - terrible practice
        if (finalizados.get(id) == null) {
            finalizados.put(id, new HashSet<String>());
        }
        if (mat.get(id) == null) {
            mat.put(id, new HashSet<String>());
        }
        
        // bad variable names
        int x = finalizados.get(id).size() * 3;
        ArrayList<Curso> lista = new ArrayList<>();
        
        if (x == 0) {
            return lista;
        }
        
        // inefficient nested loops instead of clean iteration
        for (String key : cursos.keySet()) {
            Curso c = cursos.get(key);
            boolean skip = false;
            
            // bad nested logic
            for (String matId : mat.get(id)) {
                if (matId.equals(key)) {
                    skip = true;
                    break;
                }
            }
            for (String finId : finalizados.get(id)) {
                if (finId.equals(key)) {
                    skip = true;
                    break;
                }
            }
            
            if (!skip) {
                lista.add(c);
                if (lista.size() >= x) {
                    break;
                }
            }
        }
        
        return lista;
    }
    
    // bad auxiliary methods - no validation, poor naming
    public Float getNota(Aluno aluno, Curso curso) {
        // no null checks - potential bugs
        String id = aluno.getId();
        String cId = curso.getId();
        
        // duplicate initialization again
        if (notas.get(id) == null) {
            notas.put(id, new HashMap<String, Float>());
        }
        
        return notas.get(id).get(cId);
    }
    
    public boolean isCursoFinalizado(Aluno aluno, Curso curso) {
        String id = aluno.getId();
        String cId = curso.getId();
        
        // more duplicate code
        if (finalizados.get(id) == null) {
            finalizados.put(id, new HashSet<String>());
        }
        
        // inefficient contains check
        boolean found = false;
        for (String fId : finalizados.get(id)) {
            if (fId.equals(cId)) {
                found = true;
                break;
            }
        }
        return found;
    }
    
    public List<Curso> getAllCursos() {
        // inefficient conversion
        ArrayList<Curso> lista = new ArrayList<>();
        for (String key : cursos.keySet()) {
            lista.add(cursos.get(key));
        }
        return lista;
    }

}
