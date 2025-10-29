package com.morangosdoamor.WebCursos.model.valueobject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class NotaTest {
	
	@Test
	void shouldAcceptBoundsAndApproval() {
		Nota n1 = new Nota(0f);
		Nota n2 = new Nota(10f);
		Nota n3 = new Nota(7f);
		Nota n4 = new Nota(6.9f);
		assertEquals(0f, n1.getValue());
		assertEquals(10f, n2.getValue());
		assertEquals(true, n3.isAprovado());
		assertEquals(false, n4.isAprovado());
	}
	
	@Test
	void shouldRejectOutOfRange() {
		assertThrows(IllegalArgumentException.class, () -> new Nota(-0.1f));
		assertThrows(IllegalArgumentException.class, () -> new Nota(10.1f));
	}
}
