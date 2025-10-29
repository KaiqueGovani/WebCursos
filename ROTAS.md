# Documentação da API - WebCursos

Este arquivo descreve os endpoints disponíveis na API do projeto WebCursos.

---

## Alunos

### 1. Criar um novo aluno

Cadastra um novo aluno na plataforma.

- **URL**: `/alunos`
- **Método**: `POST`
- **Corpo da Requisição**:
  ```json
  {
    "nome": "Nome do Aluno",
    "email": "email@exemplo.com",
    "senha": "uma_senha"
  }
  ```
- **Resposta de Sucesso (200 OK)**: Retorna o objeto do aluno criado, incluindo seu ID.
  ```json
  {
    "id": "a4b1c2d3-e4f5-g6h7-i8j9-k0l1m2n3o4p5",
    "nome": "Nome do Aluno",
    "email": {
      "value": "email@exemplo.com"
    },
    "senha": "uma_senha"
  }
  ```

---

## Cursos

### 2. Listar todos os cursos

Retorna uma lista com todos os cursos disponíveis na plataforma.

- **URL**: `/cursos`
- **Método**: `GET`
- **Corpo da Requisição**: N/A
- **Resposta de Sucesso (200 OK)**:
  ```json
  [
    {
      "id": "JAVA001",
      "nome": "Programação Java",
      "descricao": "Curso básico de Java",
      "cargaHoraria": 40,
      "preRequisitos": []
    },
    {
      "id": "WEB001",
      "nome": "Desenvolvimento Web",
      "descricao": "HTML, CSS, JavaScript",
      "cargaHoraria": 50,
      "preRequisitos": []
    }
  ]
  ```

---

## Matrículas

### 3. Matricular aluno em um curso

Realiza a matrícula de um aluno em um curso específico.

- **URL**: `/matriculas`
- **Método**: `POST`
- **Corpo da Requisição**:
  ```json
  {
    "alunoId": "string-uuid-do-aluno",
    "cursoId": "ID_DO_CURSO"
  }
  ```
- **Resposta de Sucesso (200 OK)**:
  ```
  "Matrícula realizada com sucesso!"
  ```
- **Respostas de Erro**:
  - `400 Bad Request` se o aluno ou curso não forem encontrados.
  - `500 Internal Server Error` se o aluno já estiver matriculado.

### 4. Finalizar um curso

Finaliza a matrícula de um aluno em um curso, atribuindo a nota final.

- **URL**: `/matriculas/finalizar`
- **Método**: `POST`
- **Corpo da Requisição**:
  ```json
  {
    "alunoId": "string-uuid-do-aluno",
    "cursoId": "ID_DO_CURSO",
    "nota": 9.5
  }
  ```
- **Resposta de Sucesso (200 OK)**:
  ```
  "Curso finalizado com sucesso!"
  ```
- **Respostas de Erro**:
    - `400 Bad Request` se o aluno ou curso não forem encontrados.
    - `500 Internal Server Error` se a matrícula não existir ou se o curso não estiver com o status "EM_ANDAMENTO".
