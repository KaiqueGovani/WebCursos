#!/bin/bash

echo "Adicionando cursos..."

# Curso 1: Java
curl -X POST http://localhost:8080/api/cursos \
  -H "Content-Type: application/json" \
  -d '{"id":"JAVA001","nome":"Programação Java","descricao":"Curso básico de Java","cargaHoraria":40,"prerequisitos":[]}'
echo ""

# Curso 2: Spring
curl -X POST http://localhost:8080/api/cursos \
  -H "Content-Type: application/json" \
  -d '{"id":"SPRING001","nome":"Spring Framework","descricao":"Curso de Spring Boot","cargaHoraria":60,"prerequisitos":[]}'
echo ""

# Curso 3: Web
curl -X POST http://localhost:8080/api/cursos \
  -H "Content-Type: application/json" \
  -d '{"id":"WEB001","nome":"Desenvolvimento Web","descricao":"HTML, CSS, JavaScript","cargaHoraria":50,"prerequisitos":[]}'
echo ""

# Curso 4: React
curl -X POST http://localhost:8080/api/cursos \
  -H "Content-Type: application/json" \
  -d '{"id":"REACT001","nome":"React.js","descricao":"Desenvolvimento com React","cargaHoraria":45,"prerequisitos":[]}'
echo ""

# Curso 5: Python
curl -X POST http://localhost:8080/api/cursos \
  -H "Content-Type: application/json" \
  -d '{"id":"PYTHON001","nome":"Programação Python","descricao":"Curso básico de Python","cargaHoraria":45,"prerequisitos":[]}'
echo ""

# Curso 6: Django
curl -X POST http://localhost:8080/api/cursos \
  -H "Content-Type: application/json" \
  -d '{"id":"DJANGO001","nome":"Django Framework","descricao":"Desenvolvimento web com Django","cargaHoraria":55,"prerequisitos":[]}'
echo ""

# Curso 7: Node
curl -X POST http://localhost:8080/api/cursos \
  -H "Content-Type: application/json" \
  -d '{"id":"NODE001","nome":"Node.js","descricao":"Desenvolvimento backend com Node.js","cargaHoraria":50,"prerequisitos":[]}'
echo ""

# Curso 8: Angular
curl -X POST http://localhost:8080/api/cursos \
  -H "Content-Type: application/json" \
  -d '{"id":"ANGULAR001","nome":"Angular","descricao":"Framework Angular para frontend","cargaHoraria":60,"prerequisitos":[]}'
echo ""

# Curso 9: Vue
curl -X POST http://localhost:8080/api/cursos \
  -H "Content-Type: application/json" \
  -d '{"id":"VUE001","nome":"Vue.js","descricao":"Framework Vue.js para frontend","cargaHoraria":45,"prerequisitos":[]}'
echo ""

# Curso 10: Database
curl -X POST http://localhost:8080/api/cursos \
  -H "Content-Type: application/json" \
  -d '{"id":"DB001","nome":"Banco de Dados","descricao":"Fundamentos de banco de dados","cargaHoraria":40,"prerequisitos":[]}'
echo ""

echo "Todos os cursos foram adicionados!"
