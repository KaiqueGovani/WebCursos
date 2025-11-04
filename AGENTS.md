# Repository Guidelines

## Project Structure & Module Organization
The Spring Boot app lives under `src/main/java/com/morangosdoamor/WebCursos`. Domain models (`Aluno`, `Curso`) sit in `domain/` and the business logic in `service/CursoService.java`. Configuration-ready folders exist in `src/main/resources/static` and `templates` for future web/UI layers. Tests reside in `src/test/java/com/morangosdoamor/WebCursos`, mirroring the main package. Use `pom.xml` and the Maven Wrapper in the repo root; anything inside `target/` is disposable.

## Build, Test, and Development Commands
Run `./mvnw compile` to verify sources against Java 17 and resolve dependencies. Execute `./mvnw test` for the JUnit 5 suite and Jacoco coverage. Use `./mvnw spring-boot:run` to start the application with the in-memory course catalog. For focused work, `./mvnw -Dtest=WebCursosApplicationTests#methodName test` reruns a single scenario.

## Coding Style & Naming Conventions
Stick to standard Java formatting: 4-space indentation, braces on the same line, and `UpperCamelCase` for classes. Packages remain all lower-case; method and variable names use `lowerCamelCase`. Favor descriptive Portuguese test names consistent with the current suite. Keep business rules encapsulated in services; prefer constructor initialization over lazy mutation when expanding domain objects.

## Testing Guidelines
JUnit 5 with `@SpringBootTest` backs the service-level specifications. Extend existing tests or add new ones under `src/test/java/...`; mirror the package of the class under test. Aim to preserve the current near-100% Jacoco coverage; inspect `target/site/jacoco/index.html` after `./mvnw test`. Each new feature should start with a failing test that captures the user story before implementing the service logic.

## Commit & Pull Request Guidelines
Follow the repo’s history: imperative, sentence-cased commit titles (`Add`, `Refactor`, `Update`) with concise summaries. Commits should stay focused on a single concern—tests and implementation together. Pull requests must describe the change, reference related user stories or issues, and attach test evidence (command output or screenshots of Jacoco reports when coverage changes). Highlight any new commands or configuration steps for reviewers.
