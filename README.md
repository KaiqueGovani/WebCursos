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
- `id` (String) - Identificador único do curso
- `nome` (String) - Nome do curso
- `descricao` (String) - Descrição detalhada do conteúdo
- `cargaHoraria` (int) - Duração em horas
- `prerequisitos` (String[]) - Array de IDs dos cursos pré-requisitos

## Funcionalidades Implementadas

### CursoService
Serviço principal que gerencia toda a lógica de negócio relacionada aos cursos:

#### Gestão de Matrículas
- `adicionarCurso(Aluno, String cursoId)` - Matricula um aluno em um curso
- `getCursos(Aluno)` - Retorna os cursos em que o aluno está matriculado
- Validação automática de pré-requisitos antes da matrícula

#### Finalização de Cursos
- `finalizarCurso(Aluno, Curso, float nota)` - Finaliza um curso com nota (0-10)
- Aprovação automática com nota ≥ 7.0
- Registro de histórico acadêmico

#### Sistema de Liberação Automática
- `findLiberadosByAluno(Aluno)` - Retorna cursos liberados para matrícula
- **Regra de negócio**: Cada curso concluído com média ≥ 7.0 libera automaticamente 3 novos cursos
- Verificação inteligente de pré-requisitos

#### Consultas e Relatórios
- `getNota(Aluno, Curso)` - Consulta nota final de um curso
- `isCursoFinalizado(Aluno, Curso)` - Verifica se curso foi concluído
- `getAllCursos()` - Lista todos os cursos disponíveis

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

## Status do Desenvolvimento

✅ **Entidades de Domínio** - Implementadas (`Aluno` e `Curso`)  
✅ **Serviço de Cursos** - Implementado com funcionalidades completas  
✅ **Sistema de Matrícula** - Funcionando com validação de pré-requisitos  
✅ **Sistema de Liberação Automática** - Implementado (3 cursos por aprovação)  
✅ **Testes Unitários** - Cobrindo cenários principais BDD  
🚧 **Interface Web** - Próxima fase de desenvolvimento  
🚧 **Sistema de Notificações** - Planejado para implementação futura
