package com.morangosdoamor.WebCursos.repository;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.morangosdoamor.WebCursos.model.entity.Aluno;

@DataJpaTest
class AlunoRepositoryTest {
	
	@Autowired
	private AlunoRepository alunoRepository;
	
	@Test
	void shouldSaveAndFindAluno() {
		Aluno a = new Aluno("Repo User", "repo@example.com", "123");
		alunoRepository.save(a);
		Optional<Aluno> found = alunoRepository.findById(a.getId());
		assertTrue(found.isPresent());
	}
}
