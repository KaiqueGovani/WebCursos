# Instruções para Acessar o Console H2 e Gerar o Schema

## 1. Iniciar a Aplicação

Execute a aplicação em modo desenvolvimento:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

Ou pelo IDE, executando a classe `WebCursosApplication.java`

## 2. Acessar o Console H2

Abra o navegador e acesse:

**URL:** http://localhost:8080/h2-console

## 3. Configurar a Conexão

No console H2, use as seguintes configurações:

- **Driver Class:** `org.h2.Driver`
- **JDBC URL:** `jdbc:h2:mem:webcursos_dev`
- **User Name:** `sa`
- **Password:** (deixe em branco)

Clique em **Connect**

## 4. Visualizar as Tabelas

Após conectar, você verá no painel esquerdo:
- `ALUNOS`
- `CURSOS`

## 5. Gerar o Schema DDL

### Opção A - Via Console H2:

Clique em cada tabela e copie o DDL gerado automaticamente.

### Opção B - Via Query SQL:

Execute esta query no console H2:

```sql
-- Ver estrutura da tabela ALUNOS
SHOW COLUMNS FROM ALUNOS;

-- Ver estrutura da tabela CURSOS
SHOW COLUMNS FROM CURSOS;

-- Script completo do schema
SCRIPT NODATA;
```

### Opção C - Hibernate gerará automaticamente:

Com `spring.jpa.hibernate.ddl-auto=update`, o Hibernate criará o schema automaticamente ao iniciar.

Os logs mostrarão o DDL:

```sql
Hibernate: create table alunos (...)
Hibernate: create table cursos (...)
```

## 6. Schema Esperado

```sql
CREATE TABLE ALUNOS (
    ID VARCHAR(255) NOT NULL,
    NOME VARCHAR(255) NOT NULL,
    EMAIL VARCHAR(255),
    MATRICULA VARCHAR(255),
    PRIMARY KEY (ID)
);

CREATE TABLE CURSOS (
    ID VARCHAR(255) NOT NULL,
    NOME VARCHAR(255) NOT NULL,
    DESCRICAO VARCHAR(1000),
    CARGA_HORARIA INTEGER,
    PREREQUISITOS VARCHAR(500) ARRAY,
    PRIMARY KEY (ID)
);
```

## Troubleshooting

Se não conseguir acessar:
1. Verifique se a aplicação está rodando
2. Confirme a porta: http://localhost:8080/h2-console
3. Verifique os logs da aplicação
4. Confirme que o profile `dev` está ativo
