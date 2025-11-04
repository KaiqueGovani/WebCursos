package com.morangosdoamor.WebCursos.domain.valueobject;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class EmailTest {

    @Test
    void deveCompararEmailsPeloValor() {
        Email email1 = new Email("user@example.com");
        Email email2 = new Email("user@example.com");

        assertThat(email1).isEqualTo(email2);
        assertThat(email1.toString()).contains("user@example.com");
    }
}
