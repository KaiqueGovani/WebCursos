package com.morangosdoamor.WebCursos.repository;

import com.morangosdoamor.WebCursos.domain.Curso;
import com.morangosdoamor.WebCursos.domain.valueobject.CargaHoraria;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes de Repository com @DataJpaTest
 * 
 * Vantagens de @DataJpaTest sobre testes de integração completos:
 * - Isola APENAS a camada de persistência
 * - Não carrega Services, Controllers, Config desnecessários
 * - Executa mais rápido que @SpringBootTest
 * - Foca em testar queries customizadas (@Query)
 * - Valida mapeamento JPA e relacionamentos
 * 
 * Quando usar @DataJpaTest:
 * - Testar queries customizadas (JPQL, native queries)
 * - Validar mapeamento de entidades (@Entity, @Embedded)
 * - Testar relacionamentos (@OneToMany, @ManyToOne, etc)
 * - Verificar constraints e índices do banco
 */
@DataJpaTest
@DisplayName("CursoRepository - Testes de Integração com @DataJpaTest")
class CursoRepositoryTest {
    
    @Autowired
    private CursoRepository cursoRepository;
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Test
    @DisplayName("Deve salvar e buscar curso por ID")
    void deveSalvarEBuscarCursoPorId() {
        // Arrange
        Curso curso = new Curso(
            null,
            "Java Avançado",
            "Curso de Java com Spring Boot",
            new CargaHoraria(80),
            new String[]{"Java Básico", "POO"}
        );
        
        // Act
        Curso salvo = cursoRepository.save(curso);
        entityManager.flush();
        entityManager.clear();
        
        Optional<Curso> encontrado = cursoRepository.findById(salvo.getId());
        
        // Assert
        assertTrue(encontrado.isPresent());
        assertEquals("Java Avançado", encontrado.get().getNome());
        assertEquals(80, encontrado.get().getCargaHoraria().getHoras());
    }
    
    @Test
    @DisplayName("Deve buscar curso por nome")
    void deveBuscarCursoPorNome() {
        // Arrange
        Curso curso = new Curso(
            null,
            "Python para Data Science",
            "Análise de dados com Python",
            new CargaHoraria(60),
            null
        );
        entityManager.persist(curso);
        entityManager.flush();
        
        // Act
        Optional<Curso> encontrado = cursoRepository.findByNome("Python para Data Science");
        
        // Assert
        assertTrue(encontrado.isPresent());
        assertEquals("Análise de dados com Python", encontrado.get().getDescricao());
    }
    
    @Test
    @DisplayName("Deve verificar se curso existe por nome")
    void deveVerificarSeCursoExistePorNome() {
        // Arrange
        Curso curso = new Curso(
            null,
            "React Native",
            "Desenvolvimento mobile",
            new CargaHoraria(50),
            null
        );
        entityManager.persist(curso);
        entityManager.flush();
        
        // Act
        boolean existe = cursoRepository.existsByNome("React Native");
        boolean naoExiste = cursoRepository.existsByNome("Angular");
        
        // Assert
        assertTrue(existe);
        assertFalse(naoExiste);
    }
    
    @Test
    @DisplayName("Deve buscar cursos por carga horária mínima")
    void deveBuscarCursosPorCargaHorariaMinima() {
        // Arrange
        entityManager.persist(new Curso(null, "Curso Curto", null, new CargaHoraria(10), null));
        entityManager.persist(new Curso(null, "Curso Médio", null, new CargaHoraria(50), null));
        entityManager.persist(new Curso(null, "Curso Longo", null, new CargaHoraria(100), null));
        entityManager.flush();
        
        // Act
        List<Curso> cursos = cursoRepository.findByCargaHorariaMinima(50);
        
        // Assert
        assertEquals(2, cursos.size());
        assertTrue(cursos.stream().allMatch(c -> c.getCargaHoraria().getHoras() >= 50));
    }
    
    @Test
    @DisplayName("Deve buscar cursos por carga horária máxima")
    void deveBuscarCursosPorCargaHorariaMaxima() {
        // Arrange
        entityManager.persist(new Curso(null, "Curso A", null, new CargaHoraria(20), null));
        entityManager.persist(new Curso(null, "Curso B", null, new CargaHoraria(60), null));
        entityManager.persist(new Curso(null, "Curso C", null, new CargaHoraria(120), null));
        entityManager.flush();
        
        // Act
        List<Curso> cursos = cursoRepository.findByCargaHorariaMaxima(60);
        
        // Assert
        assertEquals(2, cursos.size());
        assertTrue(cursos.stream().allMatch(c -> c.getCargaHoraria().getHoras() <= 60));
    }
    
    @Test
    @DisplayName("Deve listar todos os cursos")
    void deveListarTodosOsCursos() {
        // Arrange
        entityManager.persist(new Curso(null, "Curso 1", null, new CargaHoraria(30), null));
        entityManager.persist(new Curso(null, "Curso 2", null, new CargaHoraria(40), null));
        entityManager.persist(new Curso(null, "Curso 3", null, new CargaHoraria(50), null));
        entityManager.flush();
        
        // Act
        List<Curso> cursos = cursoRepository.findAll();
        
        // Assert
        assertEquals(3, cursos.size());
    }
    
    @Test
    @DisplayName("Deve deletar curso")
    void deveDeletarCurso() {
        // Arrange
        Curso curso = new Curso(null, "Curso Para Deletar", null, new CargaHoraria(15), null);
        Curso salvo = entityManager.persist(curso);
        entityManager.flush();
        String id = salvo.getId();
        
        // Act
        cursoRepository.deleteById(id);
        entityManager.flush();
        
        // Assert
        Optional<Curso> encontrado = cursoRepository.findById(id);
        assertFalse(encontrado.isPresent());
    }
    
    @Test
    @DisplayName("Deve atualizar curso existente")
    void deveAtualizarCursoExistente() {
        // Arrange
        Curso curso = new Curso(null, "Nome Original", "Desc", new CargaHoraria(25), null);
        Curso salvo = entityManager.persist(curso);
        entityManager.flush();
        entityManager.clear();
        
        // Act
        salvo.setNome("Nome Atualizado");
        salvo.setCargaHoraria(new CargaHoraria(35));
        cursoRepository.save(salvo);
        entityManager.flush();
        entityManager.clear();
        
        Optional<Curso> atualizado = cursoRepository.findById(salvo.getId());
        
        // Assert
        assertTrue(atualizado.isPresent());
        assertEquals("Nome Atualizado", atualizado.get().getNome());
        assertEquals(35, atualizado.get().getCargaHoraria().getHoras());
    }
    
    @Test
    @DisplayName("Deve persistir e recuperar pré-requisitos corretamente")
    void devePersistirERecuperarPreRequisitosCorretamente() {
        // Arrange
        String[] prerequisitos = {"Java Básico", "SQL", "Git"};
        Curso curso = new Curso(null, "Spring Boot Avançado", null, new CargaHoraria(70), prerequisitos);
        
        // Act
        Curso salvo = cursoRepository.save(curso);
        entityManager.flush();
        entityManager.clear();
        
        Optional<Curso> encontrado = cursoRepository.findById(salvo.getId());
        
        // Assert
        assertTrue(encontrado.isPresent());
        assertNotNull(encontrado.get().getPrerequisitos());
        assertArrayEquals(prerequisitos, encontrado.get().getPrerequisitos());
    }
}
