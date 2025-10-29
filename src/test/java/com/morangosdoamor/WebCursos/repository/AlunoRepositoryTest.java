package com.morangosdoamor.WebCursos.repository;

import com.morangosdoamor.WebCursos.domain.Aluno;
import com.morangosdoamor.WebCursos.domain.valueobject.Email;
import com.morangosdoamor.WebCursos.domain.valueobject.Matricula;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes de Repository com @DataJpaTest
 * 
 * @DataJpaTest - Importância e Características:
 * - Teste de INTEGRAÇÃO focado APENAS na camada de persistência
 * - Configura banco H2 em memória automaticamente
 * - Carrega apenas beans JPA (Repository, EntityManager)
 * - NÃO carrega toda aplicação Spring (@Service, @Controller)
 * - Transacional por padrão: rollback automático após cada teste
 * - Mais rápido que @SpringBootTest pois carrega menos contexto
 * 
 * Diferença de Teste Unitário vs @DataJpaTest:
 * - Unitário: Mock do Repository, sem banco
 * - @DataJpaTest: Banco real (H2), testa queries e mapeamento JPA
 */
@DataJpaTest
@DisplayName("AlunoRepository - Testes de Integração com @DataJpaTest")
class AlunoRepositoryTest {
    
    @Autowired
    private AlunoRepository alunoRepository;
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Test
    @DisplayName("Deve salvar e buscar aluno por ID")
    void deveSalvarEBuscarAlunoPorId() {
        // Arrange
        Aluno aluno = new Aluno(
            null,
            "João Silva",
            new Email("joao@exemplo.com"),
            new Matricula("MAT12345")
        );
        
        // Act
        Aluno salvo = alunoRepository.save(aluno);
        entityManager.flush();
        entityManager.clear();
        
        Optional<Aluno> encontrado = alunoRepository.findById(salvo.getId());
        
        // Assert
        assertTrue(encontrado.isPresent());
        assertEquals("João Silva", encontrado.get().getNome());
        assertEquals("joao@exemplo.com", encontrado.get().getEmail().getEmail());
        assertEquals("MAT12345", encontrado.get().getMatricula().getMatricula());
    }
    
    @Test
    @DisplayName("Deve buscar aluno por email")
    void deveBuscarAlunoPorEmail() {
        // Arrange
        Aluno aluno = new Aluno(
            null,
            "Maria Santos",
            new Email("maria@exemplo.com"),
            new Matricula("MAT54321")
        );
        entityManager.persist(aluno);
        entityManager.flush();
        
        // Act
        Optional<Aluno> encontrado = alunoRepository.findByEmail(new Email("maria@exemplo.com"));
        
        // Assert
        assertTrue(encontrado.isPresent());
        assertEquals("Maria Santos", encontrado.get().getNome());
    }
    
    @Test
    @DisplayName("Deve retornar empty quando email não existe")
    void deveRetornarEmptyQuandoEmailNaoExiste() {
        // Act
        Optional<Aluno> encontrado = alunoRepository.findByEmail(new Email("naoexiste@exemplo.com"));
        
        // Assert
        assertFalse(encontrado.isPresent());
    }
    
    @Test
    @DisplayName("Deve buscar aluno por matrícula")
    void deveBuscarAlunoPorMatricula() {
        // Arrange
        Aluno aluno = new Aluno(
            null,
            "Pedro Oliveira",
            new Email("pedro@exemplo.com"),
            new Matricula("MAT99999")
        );
        entityManager.persist(aluno);
        entityManager.flush();
        
        // Act
        Optional<Aluno> encontrado = alunoRepository.findByMatricula(new Matricula("MAT99999"));
        
        // Assert
        assertTrue(encontrado.isPresent());
        assertEquals("Pedro Oliveira", encontrado.get().getNome());
    }
    
    @Test
    @DisplayName("Deve verificar se email existe")
    void deveVerificarSeEmailExiste() {
        // Arrange
        Aluno aluno = new Aluno(
            null,
            "Ana Costa",
            new Email("ana@exemplo.com"),
            new Matricula("MAT11111")
        );
        entityManager.persist(aluno);
        entityManager.flush();
        
        // Act
        boolean existe = alunoRepository.existsByEmail(new Email("ana@exemplo.com"));
        boolean naoExiste = alunoRepository.existsByEmail(new Email("outro@exemplo.com"));
        
        // Assert
        assertTrue(existe);
        assertFalse(naoExiste);
    }
    
    @Test
    @DisplayName("Deve verificar se matrícula existe")
    void deveVerificarSeMatriculaExiste() {
        // Arrange
        Aluno aluno = new Aluno(
            null,
            "Carlos Lima",
            new Email("carlos@exemplo.com"),
            new Matricula("MAT22222")
        );
        entityManager.persist(aluno);
        entityManager.flush();
        
        // Act
        boolean existe = alunoRepository.existsByMatricula(new Matricula("MAT22222"));
        boolean naoExiste = alunoRepository.existsByMatricula(new Matricula("MAT00000"));
        
        // Assert
        assertTrue(existe);
        assertFalse(naoExiste);
    }
    
    @Test
    @DisplayName("Deve listar todos os alunos")
    void deveListarTodosOsAlunos() {
        // Arrange
        entityManager.persist(new Aluno(null, "Aluno 1", new Email("a1@exemplo.com"), new Matricula("MAT00001")));
        entityManager.persist(new Aluno(null, "Aluno 2", new Email("a2@exemplo.com"), new Matricula("MAT00002")));
        entityManager.persist(new Aluno(null, "Aluno 3", new Email("a3@exemplo.com"), new Matricula("MAT00003")));
        entityManager.flush();
        
        // Act
        var alunos = alunoRepository.findAll();
        
        // Assert
        assertEquals(3, alunos.size());
    }
    
    @Test
    @DisplayName("Deve deletar aluno")
    void deveDeletarAluno() {
        // Arrange
        Aluno aluno = new Aluno(
            null,
            "Aluno Para Deletar",
            new Email("deletar@exemplo.com"),
            new Matricula("MAT99999")
        );
        Aluno salvo = entityManager.persist(aluno);
        entityManager.flush();
        String id = salvo.getId();
        
        // Act
        alunoRepository.deleteById(id);
        entityManager.flush();
        
        // Assert
        Optional<Aluno> encontrado = alunoRepository.findById(id);
        assertFalse(encontrado.isPresent());
    }
    
    @Test
    @DisplayName("Deve atualizar aluno existente")
    void deveAtualizarAlunoExistente() {
        // Arrange
        Aluno aluno = new Aluno(
            null,
            "Nome Original",
            new Email("original@exemplo.com"),
            new Matricula("MAT88888")
        );
        Aluno salvo = entityManager.persist(aluno);
        entityManager.flush();
        entityManager.clear();
        
        // Act
        salvo.setNome("Nome Atualizado");
        alunoRepository.save(salvo);
        entityManager.flush();
        entityManager.clear();
        
        Optional<Aluno> atualizado = alunoRepository.findById(salvo.getId());
        
        // Assert
        assertTrue(atualizado.isPresent());
        assertEquals("Nome Atualizado", atualizado.get().getNome());
    }
}
