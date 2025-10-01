<h1> ElysiaAPI <img src="https://github.com/user-attachments/assets/bc6d687c-dd26-4bcd-bcbf-71a8a5681bc3" width="25"/> </h1>

API RESTful desenvolvida em **Java 17** com **Spring Boot** e **JPA**, conectada ao banco de dados **Oracle**. Esta API também permite execução em ambientes conteinerizados. Esta API faz parte do projeto **Elysia: Inteligência para Gestão Inteligente de Pátios**, com o objetivo de gerenciar de forma eficiente **motos** e **vagas de estacionamento** através de tecnologia inteligente.

## ⟢ Integrantes
 
- Iris Tavares Alves 557728 </br>
- Taís Tavares Alves 557553 </br>

---

## 🎬 Vídeo

> <a href="https://youtu.be/AcaJ8uVdZ8s?si=cYHkui6XZyS9VUBf">Vídeo</a>
---

## ⚙️ Tecnologias

- Java 17
- Spring Boot 
- Spring Data JPA
- Oracle Database (ORCL)
- Spring Validation (Bean Validation)
- Swagger / OpenAPI 
- Spring Cache
- Spring Security
- Thymeleaf
- Flyway 


### 1. Clone o repositório
```text
git clone https://github.com/Irissuu/Elysia_Java.git
```

### 2. Configure o application.properties, coloque suas credenciais 
```properties
spring.application.name=2tdspm-api-elysia
api.security.token.secret=elysia-super-secret-key-2025-should-be-long-enough!

spring.datasource.url=jdbc:oracle:thin:@//oracle.fiap.com.br:1521/ORCL
                        # Coloque suas credenciais aqui
spring.datasource.username=
spring.datasource.password=
spring.datasource.driver-class-name=oracle.jdbc.OracleDriver

spring.jpa.database-platform=org.hibernate.dialect.OracleDialect
spring.jpa.open-in-view=false
spring.jpa.show-sql=false
spring.jpa.generate-ddl=false
spring.jpa.hibernate.ddl-auto=none

spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
spring.flyway.clean-disabled=true

springdoc.swagger-ui.path=/docs
springdoc.swagger-ui.use-root-path=false
```

### 3. Execute o projeto
```text
./gradlew bootRun
```

## 📅 Aplicação
### °❀⋆.ೃ࿔*:･ Tela Login
<img width="1919" height="898" alt="image" src="https://github.com/user-attachments/assets/1bc1fe3e-f732-404f-9420-de1344570a00" />

### °❀⋆.ೃ࿔*:･ Tela Cadastro
<img width="1919" height="895" alt="image" src="https://github.com/user-attachments/assets/ded551b7-9534-4d27-a937-555a71869607" />

### °❀⋆.ೃ࿔*:･ Tela Home
<img width="1919" height="893" alt="image" src="https://github.com/user-attachments/assets/b549d4c6-4330-4d4b-9e4a-81310d1fc987" />

### °❀⋆.ೃ࿔*:･ Tela Acesso Negado
<img width="1916" height="900" alt="image" src="https://github.com/user-attachments/assets/9c7c77ab-1307-4c54-8d28-abc8a590c25c" />

### °❀⋆.ೃ࿔*:･ Tela Listar Motos
<img width="1919" height="898" alt="image" src="https://github.com/user-attachments/assets/5bd09d94-3237-4caa-bc57-e21d7281afb9" />

### °❀⋆.ೃ࿔*:･ Tela Gerencia Vagas
<img width="1919" height="902" alt="image" src="https://github.com/user-attachments/assets/f2f8d775-bb42-4c24-b2b7-fcfed6da18f1" />

### °❀⋆.ೃ࿔*:･ Tela Perfil
<img width="1915" height="889" alt="image" src="https://github.com/user-attachments/assets/b9508e5a-21db-4f5c-a796-17be375f6180" />



