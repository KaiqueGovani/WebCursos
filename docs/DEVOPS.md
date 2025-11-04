# Ativos DevOps do WebCursos

## Jenkinsfile
- **Pre-Build**: garante limpeza controlada do workspace e pré-carrega dependências Maven sem executar testes.
- **Build/Pipeline-test-dev**: executa `./mvnw verify`, produzindo relatórios do JaCoCo, PMD e JUnit e publicando-os via plugins Jenkins (`jacoco`, `recordIssues`, `junit`).
- **Quality Gate**: analisa `target/site/jacoco/jacoco.xml`, exige 99% de cobertura de linha e bloqueia a pipeline em caso de reprovação.
- **Image_Docker**: disparada somente quando o quality gate passa. Gera a imagem `webcursos:dev` a partir do Dockerfile.
- **Post-Build**: arquiva artefatos relevantes (`.jar` e relatórios Jacoco) e limpa o workspace para execuções subsequentes.

## Dockerfile
- Multistage: usa `maven:3.9.9-eclipse-temurin-17` para compilar o projeto e gera um *fat jar* otimizado.
- Runtime com `eclipse-temurin:17-jre`, instala `curl` para health check e define `SPRING_PROFILES_ACTIVE=dev` no entrypoint.
- Expõe a porta 8080; o container roda o jar `WebCursos-0.0.1-SNAPSHOT.jar` com H2 em memória.

## docker-compose.yml
- Serviço único `webcursos-app` que compila e sobe a aplicação em modo DEV.
- Publica a porta 8080 do container, habilita o profile `dev` e inclui health check apontando para `/actuator/health` (exposto graças ao starter Actuator).
- Facilita execução local ou em ambientes intermediários (DEV/QA) com um único comando `docker compose up --build`.

## Fluxo Operacional Recomendado (DEV)
1. Jenkins dispara pipeline DEV via `Jenkinsfile`.
2. `Pipeline-test-dev` gera relatórios de qualidade; `Quality Gate` valida o limite de 99%.
3. Em caso de aprovação, `Image_Docker` gera imagem homologada para DEV e disponibiliza via `docker-compose`.
4. Time verifica logs, cobertura e documentação Swagger (`/swagger-ui.html`).
