INSERT INTO curso (id, codigo, nome, descricao, carga_horaria)
VALUES ('11111111-1111-1111-1111-111111111111', 'JAVA001', 'Programacao Java', 'Fundamentos da linguagem Java e orientacao a objetos', 40)
ON CONFLICT (codigo) DO UPDATE
SET nome = EXCLUDED.nome,
    descricao = EXCLUDED.descricao,
    carga_horaria = EXCLUDED.carga_horaria;

INSERT INTO curso (id, codigo, nome, descricao, carga_horaria)
VALUES ('22222222-2222-2222-2222-222222222222', 'SPRING001', 'Spring Boot', 'Construcao de APIs com Spring Boot e boas praticas', 60)
ON CONFLICT (codigo) DO UPDATE
SET nome = EXCLUDED.nome,
    descricao = EXCLUDED.descricao,
    carga_horaria = EXCLUDED.carga_horaria;

INSERT INTO curso (id, codigo, nome, descricao, carga_horaria)
VALUES ('33333333-3333-3333-3333-333333333333', 'WEB001', 'Desenvolvimento Web', 'HTML, CSS e JavaScript modernos', 50)
ON CONFLICT (codigo) DO UPDATE
SET nome = EXCLUDED.nome,
    descricao = EXCLUDED.descricao,
    carga_horaria = EXCLUDED.carga_horaria;

INSERT INTO curso (id, codigo, nome, descricao, carga_horaria)
VALUES ('44444444-4444-4444-4444-444444444444', 'REACT001', 'React.js', 'Componentizacao e hooks com React', 45)
ON CONFLICT (codigo) DO UPDATE
SET nome = EXCLUDED.nome,
    descricao = EXCLUDED.descricao,
    carga_horaria = EXCLUDED.carga_horaria;

INSERT INTO curso (id, codigo, nome, descricao, carga_horaria)
VALUES ('55555555-5555-5555-5555-555555555555', 'PYTHON001', 'Python Essentials', 'Fundamentos da linguagem Python para iniciantes', 45)
ON CONFLICT (codigo) DO UPDATE
SET nome = EXCLUDED.nome,
    descricao = EXCLUDED.descricao,
    carga_horaria = EXCLUDED.carga_horaria;

INSERT INTO curso (id, codigo, nome, descricao, carga_horaria)
VALUES ('66666666-6666-6666-6666-666666666666', 'DJANGO001', 'Django Framework', 'Desenvolvimento web com Django e ORM', 55)
ON CONFLICT (codigo) DO UPDATE
SET nome = EXCLUDED.nome,
    descricao = EXCLUDED.descricao,
    carga_horaria = EXCLUDED.carga_horaria;

INSERT INTO curso (id, codigo, nome, descricao, carga_horaria)
VALUES ('77777777-7777-7777-7777-777777777777', 'NODE001', 'Node.js', 'APIs e servicos escalaveis com Node.js', 50)
ON CONFLICT (codigo) DO UPDATE
SET nome = EXCLUDED.nome,
    descricao = EXCLUDED.descricao,
    carga_horaria = EXCLUDED.carga_horaria;

INSERT INTO curso (id, codigo, nome, descricao, carga_horaria)
VALUES ('88888888-8888-8888-8888-888888888888', 'ANGULAR001', 'Angular', 'SPA com Angular e RxJS', 60)
ON CONFLICT (codigo) DO UPDATE
SET nome = EXCLUDED.nome,
    descricao = EXCLUDED.descricao,
    carga_horaria = EXCLUDED.carga_horaria;

INSERT INTO curso (id, codigo, nome, descricao, carga_horaria)
VALUES ('99999999-9999-9999-9999-999999999999', 'VUE001', 'Vue.js', 'Fundamentos de Vue.js para front-end', 45)
ON CONFLICT (codigo) DO UPDATE
SET nome = EXCLUDED.nome,
    descricao = EXCLUDED.descricao,
    carga_horaria = EXCLUDED.carga_horaria;

INSERT INTO curso (id, codigo, nome, descricao, carga_horaria)
VALUES ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'DB001', 'Banco de Dados', 'Modelagem e SQL para sistemas relacionais', 40)
ON CONFLICT (codigo) DO UPDATE
SET nome = EXCLUDED.nome,
    descricao = EXCLUDED.descricao,
    carga_horaria = EXCLUDED.carga_horaria;

INSERT INTO curso_prerequisito (curso_id, codigo_prerequisito)
SELECT '22222222-2222-2222-2222-222222222222', 'JAVA001'
WHERE NOT EXISTS (
    SELECT 1 FROM curso_prerequisito WHERE curso_id = '22222222-2222-2222-2222-222222222222' AND codigo_prerequisito = 'JAVA001'
);

INSERT INTO curso_prerequisito (curso_id, codigo_prerequisito)
SELECT '44444444-4444-4444-4444-444444444444', 'WEB001'
WHERE NOT EXISTS (
    SELECT 1 FROM curso_prerequisito WHERE curso_id = '44444444-4444-4444-4444-444444444444' AND codigo_prerequisito = 'WEB001'
);

INSERT INTO curso_prerequisito (curso_id, codigo_prerequisito)
SELECT '66666666-6666-6666-6666-666666666666', 'PYTHON001'
WHERE NOT EXISTS (
    SELECT 1 FROM curso_prerequisito WHERE curso_id = '66666666-6666-6666-6666-666666666666' AND codigo_prerequisito = 'PYTHON001'
);
