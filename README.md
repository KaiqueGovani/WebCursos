# WebCursos

Este projeto está sendo desenvolvido utilizando **Test-Driven Development (TDD)** para implementar uma plataforma de cursos com sistema de liberação automática de novos cursos baseado no desempenho do aluno.

## User Story

**EU COMO** aluno da plataforma de cursos  
**PRECISO/QUERO** liberar 3 novos cursos sempre que eu concluir um curso com média final acima de 7,0  
**PARA** que eu tenha incentivo para continuar estudando e avançar no meu aprendizado  

## Cenários BDD (Behavior-Driven Development)

### Cenário 1: Liberação de cursos com média satisfatória

**Dado que** sou aluno da plataforma de cursos  
**E** concluo um curso com nota final registrada  
**Quando** minha média final for maior ou igual a 7,0  
**Então** o sistema deve liberar automaticamente 3 novos cursos para mim  
**E** exibir uma notificação informando que novos cursos foram desbloqueados  

### Cenário 2: Conclusão sem liberação de cursos

**Dado que** o aluno concluiu todas as atividades de um curso  
**E** sua média final foi inferior a 7,0  
**Quando** o sistema registrar a conclusão do curso  
**Então** nenhum novo curso será liberado  
**E** o aluno será notificado que não atingiu a média necessária  

### Cenário 3: Liberação acumulativa de cursos

**Dado que** o aluno concluiu um curso com média igual ou superior a 7,0  
**E** concluiu um segundo curso também com média igual ou superior a 7,0  
**Quando** o sistema registrar as duas conclusões  
**Então** o aluno terá 6 cursos liberados no total  
**E** será notificado sobre a liberação acumulada  

## Estrutura do Projeto

O projeto está sendo desenvolvido em Java com Spring Boot, seguindo a metodologia TDD:

- `src/main/java/com/morangosdoamor/WebCursos/domain/` - Entidades de domínio (Aluno, Curso)
- `src/main/java/com/morangosdoamor/WebCursos/service/` - Serviços de negócio (CursoService)
- `src/test/java/` - Testes unitários e de integração

## Entidades de Domínio

### Aluno
Representa um estudante na plataforma com os seguintes atributos:
- `id` (String) - Identificador único do aluno
- `nome` (String) - Nome completo do aluno
- `email` (String) - Email para comunicação
- `matricula` (String) - Número de matrícula institucional

### Curso
Representa um curso disponível na plataforma:
- `id` (UUID) - Identificador único do curso
- `codigo` (String) - Código único do curso
- `nome` (String) - Nome do curso
- `descricao` (String) - Descrição detalhada do conteúdo
- `cargaHoraria` (CargaHoraria) - Duração em horas (Value Object com validações e conversões)
- `prerequisitos` (Set<String>) - Conjunto de códigos dos cursos pré-requisitos

### CargaHoraria (Value Object)
Value Object que encapsula a carga horária de um curso com validações e conversões:
- Validação: mínimo de 1 hora, máximo de 1000 horas
- Conversões disponíveis:
  - `emDias()` - Converte para dias úteis (8 horas/dia)
  - `emSemanas()` - Converte para semanas (40 horas/semana)

## Funcionalidades Implementadas

### API REST - Endpoints Completos

#### AlunoController (`/api/v1/alunos`)
- `POST /api/v1/alunos` - Criar novo aluno
- `GET /api/v1/alunos` - Listar todos os alunos
- `GET /api/v1/alunos/{id}` - Buscar aluno por ID (retorna detalhes com matrículas)
- `GET /api/v1/alunos/email/{email}` - Buscar aluno por email
- `GET /api/v1/alunos/matricula/{matricula}` - Buscar aluno por matrícula
- `PATCH /api/v1/alunos/{id}` - Atualizar aluno (atualização parcial)
- `DELETE /api/v1/alunos/{id}` - Excluir aluno
- `GET /api/v1/alunos/{id}/matriculas` - Listar matrículas do aluno
- `POST /api/v1/alunos/{id}/matriculas` - Matricular aluno em curso
- `POST /api/v1/alunos/{id}/matriculas/{matriculaId}/conclusao` - Concluir curso
- `GET /api/v1/alunos/{id}/cursos/liberados` - Listar cursos liberados
- `GET /api/v1/alunos/{id}/matriculas/{matriculaId}/nota` - Obter nota final

#### CursoController (`/api/v1/cursos`)
- `POST /api/v1/cursos` - Criar novo curso
- `GET /api/v1/cursos` - Listar todos os cursos
- `GET /api/v1/cursos/{id}` - Buscar curso por ID (retorna detalhes com conversões de carga horária)
- `GET /api/v1/cursos/carga-horaria/minima?horas=X` - Buscar cursos por carga horária mínima
- `GET /api/v1/cursos/carga-horaria/maxima?horas=X` - Buscar cursos por carga horária máxima
- `PATCH /api/v1/cursos/{id}` - Atualizar curso (atualização parcial)
- `DELETE /api/v1/cursos/{id}` - Excluir curso

### AlunoService
Serviço que gerencia operações relacionadas a alunos:
- `criar(Aluno)` - Cria novo aluno com validação de matrícula única
- `buscarPorId(UUID)` - Busca aluno por ID
- `listarTodos()` - Lista todos os alunos ordenados por nome
- `buscarPorEmail(String)` - Busca aluno por email
- `buscarPorMatricula(String)` - Busca aluno por matrícula
- `atualizar(UUID, AlunoUpdateDTO)` - Atualiza dados do aluno (parcial)
- `excluir(UUID)` - Remove aluno do sistema

### CursoService
Serviço principal que gerencia toda a lógica de negócio relacionada aos cursos:

#### CRUD de Cursos
- `criar(CursoRequestDTO)` - Cria novo curso com validação de código único
- `listarTodos()` - Lista todos os cursos ordenados por nome
- `buscarPorId(UUID)` - Busca curso por ID
- `buscarPorCodigo(String)` - Busca curso por código
- `buscarPorCargaHorariaMinima(int)` - Filtra cursos com carga horária mínima
- `buscarPorCargaHorariaMaxima(int)` - Filtra cursos com carga horária máxima
- `atualizar(UUID, CursoUpdateDTO)` - Atualiza dados do curso (parcial)
- `excluir(UUID)` - Remove curso do sistema

#### Gestão de Matrículas
- Validação automática de pré-requisitos antes da matrícula

#### Finalização de Cursos
- Aprovação automática com nota ≥ 7.0
- Registro de histórico acadêmico

#### Sistema de Liberação Automática
- `buscarCursosLiberados(UUID alunoId)` - Retorna cursos liberados para matrícula
- **Regra de negócio**: Cada curso concluído com média ≥ 7.0 libera automaticamente 3 novos cursos
- Verificação inteligente de pré-requisitos

### Cursos Pré-configurados
O sistema inicializa com cursos de exemplo:
- **JAVA001** - Programação Java (40h, sem pré-requisitos)
- **SPRING001** - Spring Framework (60h, requer JAVA001)
- **WEB001** - Desenvolvimento Web (50h, sem pré-requisitos)
- **REACT001** - React.js (45h, requer WEB001 e JAVA001)

## Metodologia

Este projeto utiliza **TDD (Test-Driven Development)** seguindo o ciclo:

1. **Red** - Escrever um teste que falha
2. **Green** - Implementar o código mínimo para o teste passar
3. **Refactor** - Melhorar o código mantendo os testes passando

Os cenários BDD são implementados como testes automatizados que guiam o desenvolvimento das funcionalidades.

## Como executar

```bash
# Compilar o projeto
./mvnw compile

# Executar os testes
./mvnw test

# Executar a aplicação
./mvnw spring-boot:run
```

## CI/CD Pipeline (Jenkins)

O projeto utiliza Jenkins para integração e entrega contínua com os seguintes stages:

### Pipeline Principal (Jenkinsfile)

| Stage | Descrição |
|-------|-----------|
| **Pre-Build** | Limpa o projeto (`mvnw clean`) |
| **Pipeline-test-dev** | Executa testes com `mvnw verify`, gera relatórios JUnit, PMD e JaCoCo |
| **Quality Gate** | Valida cobertura mínima de 99% (parse do jacoco.xml) |
| **Image_Docker** | Build da imagem Docker (condicional ao quality gate) |
| **Push Docker Image** | Push para Docker Hub (`kaiquemgovani/kaiquemg:latest`) |
| **Staging** | Sobe container e executa smoke tests |
| **Post-Build** | Arquiva artefatos (.jar e relatórios) |

### Staging Environment

O ambiente de staging utiliza `docker-compose.staging.yml` com PostgreSQL:

| Serviço | Container | Porta | Imagem |
|---------|-----------|-------|--------|
| **Database** | `webcursos-db` | 5432 (interno) | `postgres` |
| **API** | `webcursos-staging` | 8686 → 8080 | `kaiquemgovani/kaiquemg:latest` |

**Profiles disponíveis:**
- `dev` - H2 em memória (desenvolvimento local)
- `staging` - PostgreSQL (ambiente de staging)
- `test` - H2 em memória (testes automatizados)

```bash
# Subir ambiente staging manualmente
docker-compose -f docker-compose.staging.yml up -d

# Verificar logs
docker-compose -f docker-compose.staging.yml logs

# Testar endpoint
curl http://localhost:8686

# Derrubar ambiente
docker-compose -f docker-compose.staging.yml down -v
```

## Status do Desenvolvimento

✅ **Entidades de Domínio** - Implementadas (`Aluno` e `Curso`)  
✅ **Value Objects** - `Email` e `CargaHoraria` implementados com validações  
✅ **CRUD Completo** - Endpoints REST completos para Aluno e Curso  
✅ **Serviços de Negócio** - AlunoService e CursoService com todas as operações  
✅ **Sistema de Matrícula** - Funcionando com validação de pré-requisitos  
✅ **Sistema de Liberação Automática** - Implementado (3 cursos por aprovação)  
✅ **Testes Unitários e de Integração** - Cobertura completa de todos os endpoints e serviços  
✅ **Documentação Swagger** - API documentada com OpenAPI 3  
🚧 **Interface Web** - Próxima fase de desenvolvimento  
🚧 **Sistema de Notificações** - Planejado para implementação futura
