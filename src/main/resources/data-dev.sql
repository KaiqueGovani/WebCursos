MERGE INTO curso (id, codigo, nome, descricao, carga_horaria) KEY (codigo) VALUES
    ('11111111-1111-1111-1111-111111111111', 'JAVA001', 'Programação Java', 'Fundamentos da linguagem Java e orientação a objetos', 40);

MERGE INTO curso (id, codigo, nome, descricao, carga_horaria) KEY (codigo) VALUES
    ('22222222-2222-2222-2222-222222222222', 'SPRING001', 'Spring Boot', 'Construção de APIs com Spring Boot e melhores práticas', 60);

MERGE INTO curso (id, codigo, nome, descricao, carga_horaria) KEY (codigo) VALUES
    ('33333333-3333-3333-3333-333333333333', 'WEB001', 'Desenvolvimento Web', 'HTML, CSS e JavaScript modernos', 50);

MERGE INTO curso (id, codigo, nome, descricao, carga_horaria) KEY (codigo) VALUES
    ('44444444-4444-4444-4444-444444444444', 'REACT001', 'React.js', 'Componentização e hooks com React', 45);

MERGE INTO curso (id, codigo, nome, descricao, carga_horaria) KEY (codigo) VALUES
    ('55555555-5555-5555-5555-555555555555', 'PYTHON001', 'Python Essentials', 'Fundamentos da linguagem Python para iniciantes', 45);

MERGE INTO curso (id, codigo, nome, descricao, carga_horaria) KEY (codigo) VALUES
    ('66666666-6666-6666-6666-666666666666', 'DJANGO001', 'Django Framework', 'Desenvolvimento web com Django e ORM', 55);

MERGE INTO curso (id, codigo, nome, descricao, carga_horaria) KEY (codigo) VALUES
    ('77777777-7777-7777-7777-777777777777', 'NODE001', 'Node.js', 'APIs e serviços escaláveis com Node.js', 50);

MERGE INTO curso (id, codigo, nome, descricao, carga_horaria) KEY (codigo) VALUES
    ('88888888-8888-8888-8888-888888888888', 'ANGULAR001', 'Angular', 'SPA com Angular e RxJS', 60);

MERGE INTO curso (id, codigo, nome, descricao, carga_horaria) KEY (codigo) VALUES
    ('99999999-9999-9999-9999-999999999999', 'VUE001', 'Vue.js', 'Fundamentos de Vue.js para front-end', 45);

MERGE INTO curso (id, codigo, nome, descricao, carga_horaria) KEY (codigo) VALUES
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'DB001', 'Banco de Dados', 'Modelagem e SQL para sistemas relacionais', 40);

MERGE INTO curso_prerequisito (curso_id, codigo_prerequisito) KEY (curso_id, codigo_prerequisito) VALUES
    ('22222222-2222-2222-2222-222222222222', 'JAVA001');

MERGE INTO curso_prerequisito (curso_id, codigo_prerequisito) KEY (curso_id, codigo_prerequisito) VALUES
    ('44444444-4444-4444-4444-444444444444', 'WEB001');

MERGE INTO curso_prerequisito (curso_id, codigo_prerequisito) KEY (curso_id, codigo_prerequisito) VALUES
    ('66666666-6666-6666-6666-666666666666', 'PYTHON001');
