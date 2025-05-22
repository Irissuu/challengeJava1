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

### 1. Clone o repositório
```text
git clone https://github.com/Irissuu/challengeJava1.git
```

### 2. Configure o application.properties, coloque suas credenciais em SEU_USUARIO e SUA_SENHA
```text
spring.datasource.url=jdbc:oracle:thin:@//oracle.fiap.com.br:1521/ORCL
spring.datasource.username=SEU_USUARIO
spring.datasource.password=SUA_SENHA
spring.datasource.driver-class-name=oracle.jdbc.OracleDriver

spring.jpa.database-platform=org.hibernate.dialect.OracleDialect
spring.jpa.show-sql=true
spring.jpa.hibernate.ddl-auto=create-drop 
```

### 3. Execute o projeto
```text
./gradlew bootRun
```


