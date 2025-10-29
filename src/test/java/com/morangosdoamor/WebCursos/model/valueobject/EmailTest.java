package com.morangosdoamor.WebCursos.model.valueobject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class EmailTest {
	
	@Test
	void shouldAcceptValidEmail() {
		Email email = new Email("user@example.com");
		assertEquals("user@example.com", email.getValue());
	}
	
	@Test
	void shouldRejectInvalidEmail() {
		assertThrows(IllegalArgumentException.class, () -> new Email("invalid-email"));
	}
	
	@Test
	void shouldFollowValueEquality() {
		Email a = new Email("user@example.com");
		Email b = new Email("user@example.com");
		Email c = new Email("other@example.com");
		assertEquals(a, b);
		assertNotEquals(a, c);
	}
}
