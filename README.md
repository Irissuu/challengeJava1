# ElysiaAPI ☁📱🏍️

API RESTful desenvolvida em **Java 17** com **Spring Boot** e **JPA**, conectada ao banco de dados **Oracle**. Esta API faz parte do projeto **Elysia: Inteligência para Gestão Inteligente de Pátios**, criado para a empresa **Mottu**, com o objetivo de gerenciar de forma eficiente **motos** e **vagas de estacionamento** através de tecnologia inteligente.

## 👥 Integrantes

- **Iris Tavares Alves** - 557728 - 2TDSPM  
- **Taís Tavares Alves** - 557553 - 2TDSPM

---

## ⚙️ Tecnologias Utilizadas

```text
- Java 17
- Spring Boot 
- Spring Data JPA
- Oracle Database (ORCL)
- Spring Validation (Bean Validation)
- Swagger / OpenAPI 
- Spring Cache
```

1. Clone o repositório
```text
git clone https://github.com/Irissuu/challengeJava1.git
```

2. Configure o application.properties
```text
spring.datasource.url=jdbc:oracle:thin:@//oracle.fiap.com.br:1521/ORCL
spring.datasource.username=SEU_USUARIO
spring.datasource.password=SUA_SENHA
spring.datasource.driver-class-name=oracle.jdbc.OracleDriver

spring.jpa.database-platform=org.hibernate.dialect.OracleDialect
spring.jpa.show-sql=true
spring.jpa.hibernate.ddl-auto=create-drop 
```

3. Execute o projeto
```text
./gradlew bootRun
```

## 🔁 Rotas Disponíveis (via Swagger)


### 🔹 MotoController

| Método | Rota                            | Descrição                          |
|--------|----------------------------------|-------------------------------------|
| GET    | `/api/moto`                     | Lista todas as motos                |
| GET    | `/api/moto/{id}`                | Busca uma moto por ID               |
| GET    | `/api/moto/search?placa=XXX`    | Busca motos por placa (parcial)     |
| POST   | `/api/moto`                     | Cadastra uma nova moto              |
| PUT    | `/api/moto/{id}`                | Atualiza uma moto existente         |
| DELETE | `/api/moto/{id}`                | Remove uma moto                     |

### 🔹 VagaController

| Método | Rota                                | Descrição                           |
|--------|-------------------------------------|--------------------------------------|
| GET    | `/api/vaga`                         | Lista todas as vagas                 |
| GET    | `/api/vaga/{id}`                    | Busca uma vaga por ID                |
| GET    | `/api/vaga/patio?patio=XYZ`         | Lista vagas por pátio                |
| POST   | `/api/vaga`                         | Cadastra uma nova vaga               |
| PUT    | `/api/vaga/{id}`                    | Atualiza uma vaga existente          |
| DELETE | `/api/vaga/{id}`                    | Remove uma vaga                      |

---

## 🧾 Consulta no banco Oracle

Para visualizar os dados diretamente no Oracle SQL Developer, use **aspas nos nomes das tabelas**:

```sql
SELECT * FROM "Moto";
SELECT * FROM "Vaga";

