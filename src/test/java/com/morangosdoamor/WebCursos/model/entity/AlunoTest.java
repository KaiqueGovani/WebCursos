package com.morangosdoamor.WebCursos.model.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

class AlunoTest {
	
	@Test
	void equalsAndHashCodeUseId() {
		Aluno a = new Aluno("John", "john@example.com", "123");
		Aluno b = new Aluno("John", "john@example.com", "123");
		String sameId = a.getId();
		b.setId(sameId);
		assertEquals(a, b);
		
		Aluno c = new Aluno("John", "john@example.com", "123");
		assertNotEquals(a, c);
	}
}
