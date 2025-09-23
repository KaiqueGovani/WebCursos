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
- `src/main/java/com/morangosdoamor/WebCursos/service/` - Serviços de negócio
- `src/test/java/` - Testes unitários e de integração

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

🚧 **Em desenvolvimento** - Implementando os cenários BDD através de TDD
