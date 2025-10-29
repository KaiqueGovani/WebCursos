## Testes (Resumo)

Camadas cobertas com foco em transformar integrações em unidades quando possível:

- Entidades/Value Objects (unit):
  - `EmailTest`, `NotaTest`, `AlunoTest`
  - Validação de formato/limites e igualdade por valor/ID
  - Caminho: `src/test/java/com/morangosdoamor/WebCursos/model/...`

- Repositórios (integração leve com banco em memória):
  - `@DataJpaTest` para `AlunoRepositoryTest` e `MatriculaRepositoryTest`
  - Verifica salvar/buscar e queries personalizadas (`existsByAlunoAndCurso`, `countByAlunoAndStatus`)
  - Caminho: `src/test/java/com/morangosdoamor/WebCursos/repository/...`

- Serviço (unit com mocks):
  - `CursoServiceTest` com `@ExtendWith(MockitoExtension.class)`, `@Mock`, `@InjectMocks`
  - Casos: matricular, finalizar (aprovado/reprovado), liberação múltiplos de 3
  - Sem acesso a banco; repositórios mockados

- Controllers (unit web):
  - `@WebMvcTest` + `MockMvc` para `MatriculaControllerTest` e `CursoControllerTest`
  - Dependências mockadas (`@MockBean`), valida apenas contrato HTTP (status/payload básico)

### Como rodar

```bash
./mvnw test    # roda todos os testes
```

### Ferramentas/Anotações
- `@DataJpaTest` para testes de repositório rápidos com H2
- `@ExtendWith(MockitoExtension.class)`, `@Mock`, `@InjectMocks` para service
- `@WebMvcTest`, `MockMvc`, `@MockBean` para controller

### Observações
- Preferência por testes de unidade: mais rápidos e diagnósticos mais claros
- Testes de repositório mantidos enxutos para cobrir queries específicas
- Swagger disponível para testes manuais: `http://localhost:8081/swagger-ui.html`
