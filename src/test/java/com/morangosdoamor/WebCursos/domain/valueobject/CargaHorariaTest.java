package com.morangosdoamor.WebCursos.domain.valueobject;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class CargaHorariaTest {

    @Test
    void deveCriarCargaHorariaValida() {
        CargaHoraria cargaHoraria = new CargaHoraria(40);
        assertThat(cargaHoraria.getHoras()).isEqualTo(40);
    }

    @Test
    void deveLancarErroQuandoCargaHorariaMenorQueMinimo() {
        assertThatThrownBy(() -> new CargaHoraria(0))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("pelo menos 1 hora");
    }

    @Test
    void deveLancarErroQuandoCargaHorariaMaiorQueMaximo() {
        assertThatThrownBy(() -> new CargaHoraria(1001))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("não pode exceder 1000 horas");
    }

    @Test
    void deveConverterParaDias() {
        CargaHoraria cargaHoraria = new CargaHoraria(40);
        assertThat(cargaHoraria.emDias()).isEqualTo(5); // 40 / 8 = 5
    }

    @Test
    void deveConverterParaDiasComArredondamento() {
        CargaHoraria cargaHoraria = new CargaHoraria(45);
        assertThat(cargaHoraria.emDias()).isEqualTo(6); // 45 / 8 = 5.625, arredondado para 6
    }

    @Test
    void deveConverterParaSemanas() {
        CargaHoraria cargaHoraria = new CargaHoraria(40);
        assertThat(cargaHoraria.emSemanas()).isEqualTo(1); // 40 / 40 = 1
    }

    @Test
    void deveConverterParaSemanasComArredondamento() {
        CargaHoraria cargaHoraria = new CargaHoraria(50);
        assertThat(cargaHoraria.emSemanas()).isEqualTo(2); // 50 / 40 = 1.25, arredondado para 2
    }

    @Test
    void deveAceitarValorMinimo() {
        CargaHoraria cargaHoraria = new CargaHoraria(1);
        assertThat(cargaHoraria.getHoras()).isEqualTo(1);
    }

    @Test
    void deveAceitarValorMaximo() {
        CargaHoraria cargaHoraria = new CargaHoraria(1000);
        assertThat(cargaHoraria.getHoras()).isEqualTo(1000);
    }

    @Test
    void deveRetornarStringFormatada() {
        CargaHoraria cargaHoraria = new CargaHoraria(40);
        assertThat(cargaHoraria.toString()).isEqualTo("40 hora(s)");
    }
}

