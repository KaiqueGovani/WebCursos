package com.morangosdoamor.WebCursos.domain;

import com.morangosdoamor.WebCursos.domain.valueobject.CargaHoraria;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para Value Object CargaHoraria
 * 
 * Por que Testes Unitários são importantes:
 * - Testam regras de negócio isoladamente (cálculos, conversões)
 * - Não dependem de infraestrutura (banco, rede, filesystem)
 * - Executam em milissegundos, ideais para TDD
 * - Facilitam refatoração com segurança
 */
@DisplayName("CargaHoraria Value Object - Testes Unitários")
class CargaHorariaTest {
    
    @Test
    @DisplayName("Deve criar carga horária válida")
    void deveCriarCargaHorariaValida() {
        // Arrange & Act
        CargaHoraria carga = new CargaHoraria(40);
        
        // Assert
        assertNotNull(carga);
        assertEquals(40, carga.getHoras());
        assertEquals(40, carga.getCargaHoraria());
    }
    
    @Test
    @DisplayName("Deve aceitar carga horária mínima")
    void deveAceitarCargaHorariaMinima() {
        // Arrange & Act
        CargaHoraria carga = new CargaHoraria(1);
        
        // Assert
        assertEquals(1, carga.getHoras());
    }
    
    @Test
    @DisplayName("Deve aceitar carga horária máxima")
    void deveAceitarCargaHorariaMaxima() {
        // Arrange & Act
        CargaHoraria carga = new CargaHoraria(1000);
        
        // Assert
        assertEquals(1000, carga.getHoras());
    }
    
    @Test
    @DisplayName("Deve lançar exceção para carga horária zero")
    void deveLancarExcecaoParaCargaHorariaZero() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new CargaHoraria(0)
        );
        
        assertTrue(exception.getMessage().contains("pelo menos 1 hora"));
    }
    
    @Test
    @DisplayName("Deve lançar exceção para carga horária negativa")
    void deveLancarExcecaoParaCargaHorariaNegativa() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new CargaHoraria(-10)
        );
        
        assertTrue(exception.getMessage().contains("pelo menos 1 hora"));
    }
    
    @Test
    @DisplayName("Deve lançar exceção para carga horária acima do máximo")
    void deveLancarExcecaoParaCargaHorariaAcimaDoMaximo() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new CargaHoraria(1001)
        );
        
        assertTrue(exception.getMessage().contains("não pode exceder 1000 horas"));
    }
    
    @ParameterizedTest
    @CsvSource({
        "8, 1",      // 8 horas = 1 dia
        "16, 2",     // 16 horas = 2 dias
        "40, 5",     // 40 horas = 5 dias
        "9, 2",      // 9 horas = 2 dias (arredonda pra cima)
        "1, 1"       // 1 hora = 1 dia
    })
    @DisplayName("Deve converter horas para dias corretamente")
    void deveConverterHorasParaDiasCorretamente(int horas, int diasEsperados) {
        // Arrange
        CargaHoraria carga = new CargaHoraria(horas);
        
        // Act
        int dias = carga.emDias();
        
        // Assert
        assertEquals(diasEsperados, dias);
    }
    
    @ParameterizedTest
    @CsvSource({
        "40, 1",     // 40 horas = 1 semana
        "80, 2",     // 80 horas = 2 semanas
        "160, 4",    // 160 horas = 4 semanas
        "41, 2",     // 41 horas = 2 semanas (arredonda pra cima)
        "1, 1"       // 1 hora = 1 semana
    })
    @DisplayName("Deve converter horas para semanas corretamente")
    void deveConverterHorasParaSemanasCorretamente(int horas, int semanasEsperadas) {
        // Arrange
        CargaHoraria carga = new CargaHoraria(horas);
        
        // Act
        int semanas = carga.emSemanas();
        
        // Assert
        assertEquals(semanasEsperadas, semanas);
    }
    
    @Test
    @DisplayName("Deve formatar toString corretamente")
    void deveFormatarToStringCorretamente() {
        // Arrange
        CargaHoraria carga = new CargaHoraria(120);
        
        // Act
        String texto = carga.toString();
        
        // Assert
        assertEquals("120 hora(s)", texto);
    }
    
    @Test
    @DisplayName("Deve considerar cargas horárias iguais")
    void deveConsiderarCargasHorariasIguais() {
        // Arrange
        CargaHoraria carga1 = new CargaHoraria(40);
        CargaHoraria carga2 = new CargaHoraria(40);
        
        // Act & Assert
        assertEquals(carga1, carga2);
        assertEquals(carga1.hashCode(), carga2.hashCode());
    }
    
    @Test
    @DisplayName("Deve considerar cargas horárias diferentes")
    void deveConsiderarCargasHorariasDiferentes() {
        // Arrange
        CargaHoraria carga1 = new CargaHoraria(40);
        CargaHoraria carga2 = new CargaHoraria(80);
        
        // Act & Assert
        assertNotEquals(carga1, carga2);
    }
}
