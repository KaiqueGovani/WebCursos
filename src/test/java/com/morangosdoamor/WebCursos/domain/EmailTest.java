package com.morangosdoamor.WebCursos.domain;

import com.morangosdoamor.WebCursos.domain.valueobject.Email;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para Value Object Email
 * 
 * Importância de Testes Unitários:
 * - Testam comportamento isolado sem dependências externas
 * - Executam rapidamente (sem banco de dados ou Spring Context)
 * - Validam regras de negócio no nível mais baixo (Value Objects)
 * - Garantem que invariantes do domínio são mantidos
 */
@DisplayName("Email Value Object - Testes Unitários")
class EmailTest {
    
    @Test
    @DisplayName("Deve criar email válido")
    void deveCriarEmailValido() {
        // Arrange & Act
        Email email = new Email("usuario@exemplo.com");
        
        // Assert
        assertNotNull(email);
        assertEquals("usuario@exemplo.com", email.getEmail());
    }
    
    @Test
    @DisplayName("Deve normalizar email para lowercase")
    void deveNormalizarEmailParaLowercase() {
        // Arrange & Act
        Email email = new Email("Usuario@EXEMPLO.COM");
        
        // Assert
        assertEquals("usuario@exemplo.com", email.getEmail());
    }
    
    @Test
    @DisplayName("Deve remover espaços em branco")
    void deveRemoverEspacosEmBranco() {
        // Arrange & Act
        Email email = new Email("  usuario@exemplo.com  ");
        
        // Assert
        assertEquals("usuario@exemplo.com", email.getEmail());
    }
    
    @Test
    @DisplayName("Deve lançar exceção para email nulo")
    void deveLancarExcecaoParaEmailNulo() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new Email(null)
        );
        
        assertEquals("Email não pode ser nulo ou vazio", exception.getMessage());
    }
    
    @Test
    @DisplayName("Deve lançar exceção para email vazio")
    void deveLancarExcecaoParaEmailVazio() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new Email("")
        );
        
        assertEquals("Email não pode ser nulo ou vazio", exception.getMessage());
    }
    
    @Test
    @DisplayName("Deve lançar exceção para email em branco")
    void deveLancarExcecaoParaEmailEmBranco() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new Email("   ")
        );
        
        assertEquals("Email não pode ser nulo ou vazio", exception.getMessage());
    }
    
    @ParameterizedTest
    @ValueSource(strings = {
        "invalido",
        "@exemplo.com",
        "usuario@",
        "usuario.exemplo.com",
        "usuario @exemplo.com",
        "usuario@exemplo",
        "usuario@@exemplo.com"
    })
    @DisplayName("Deve lançar exceção para formatos inválidos")
    void deveLancarExcecaoParaFormatosInvalidos(String emailInvalido) {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new Email(emailInvalido)
        );
        
        assertTrue(exception.getMessage().contains("Email inválido"));
    }
    
    @Test
    @DisplayName("Deve considerar emails iguais quando normalizados")
    void deveConsiderarEmailsIguaisQuandoNormalizados() {
        // Arrange
        Email email1 = new Email("usuario@exemplo.com");
        Email email2 = new Email("USUARIO@EXEMPLO.COM");
        
        // Act & Assert
        assertEquals(email1, email2);
        assertEquals(email1.hashCode(), email2.hashCode());
    }
    
    @Test
    @DisplayName("Deve aceitar email com subdominios")
    void deveAceitarEmailComSubdominios() {
        // Arrange & Act
        Email email = new Email("usuario@mail.exemplo.com.br");
        
        // Assert
        assertEquals("usuario@mail.exemplo.com.br", email.getEmail());
    }
    
    @Test
    @DisplayName("Deve aceitar email com caracteres especiais permitidos")
    void deveAceitarEmailComCaracteresEspeciaisPermitidos() {
        // Arrange & Act
        Email email = new Email("user+tag@exemplo.com");
        
        // Assert
        assertEquals("user+tag@exemplo.com", email.getEmail());
    }
}
