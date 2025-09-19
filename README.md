<h1> ElysiaAPI <img src="https://github.com/user-attachments/assets/bc6d687c-dd26-4bcd-bcbf-71a8a5681bc3" width="25"/> </h1>

API RESTful desenvolvida em **Java 17** com **Spring Boot** e **JPA**, conectada ao banco de dados **Oracle**. Esta API também permite execução em ambientes conteinerizados. Esta API faz parte do projeto **Elysia: Inteligência para Gestão Inteligente de Pátios**, com o objetivo de gerenciar de forma eficiente **motos** e **vagas de estacionamento** através de tecnologia inteligente.

## ⟢ Integrantes
 
- Iris Tavares Alves 557728 </br>
- Taís Tavares Alves 557553 </br>

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
git clone https://github.com/Irissuu/challengeJava1.git
```

### 2. Configure o application.properties, coloque suas credenciais em SEU_USUARIO e SUA_SENHA
```properties
spring.application.name=2tdspm-api-elysia
api.security.token.secret=my-secret-key

spring.datasource.url=jdbc:oracle:thin:@//oracle.fiap.com.br:1521/ORCL
                          # Coloque suas crendenciais aqui
spring.datasource.username=SEU_USUARIO_AQUI
spring.datasource.password=SUA_SENHA_AQUI
spring.datasource.driver-class-name=oracle.jdbc.OracleDriver

spring.jpa.database-platform=org.hibernate.dialect.OracleDialect
spring.jpa.show-sql=true
spring.jpa.generate-ddl=true
spring.jpa.hibernate.ddl-auto=create-drop
```

### 3. Execute o projeto
```text
./gradlew bootRun
```

---

<img alt="DevOps Tools and Cloud" src="https://img.shields.io/badge/DevOps%20Tools%20and%20Cloud%20Computing-white?style=for-the-badge">

- <a href="https://github.com/Irissuu/challengeJava1/tree/f883980123d4484351c955fb592a655b4f07ebd7/CLI">CLI</a> utilizado para criar a VM

## ⚙️ Tecnologias

- Docker
- Dockerfile  
- Oracle XE
- Java 17
- Gradle
- Azure CLI
- Swagger

<h2> Pré-requisitos para VM (Debian) <img src="https://raw.githubusercontent.com/devicons/devicon/master/icons/debian/debian-original.svg" width="27"/> </h2>

- Docker: `sudo apt install docker.io -y`
- Java 17: `sudo apt install openjdk-17-jdk -y`
- Use o Docker sem _sudo_: `sudo usermod -aG docker <user>`
- O projeto já possui _Gradle_ [instalar o Gradle manualmente](#como-instalar-o-gradle)

#

### 1. Clone o repositório
```text
git clone https://github.com/Irissuu/challengeJava1.git
cd challengeJava1
```

### 2. Configure o application.properties, para Oracle XE com variáveis de ambiente
```text
spring.datasource.url=jdbc:oracle:thin:@${DB_HOST}:${DB_PORT}/${DB_NAME}
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PASS}
spring.datasource.driver-class-name=oracle.jdbc.OracleDriver

spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.OracleDialect
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
```

- #### 2.1. (Opcional) Gerar `.jar` manualmente
```text
./gradlew clean bootJar
```
<p >Recompilado do zero e gera o arquivo .jar em build/libs. </br>
Notas: Para isso precisa do Java JDK 17 e do Gradle.</p> 

### 3. Dockerfile
```text
FROM gradle:8.5-jdk17 AS builder
WORKDIR /app
COPY . .
RUN ./gradlew bootJar

FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar

ENV DB_HOST=localhost \
    DB_PORT=1521 \
    DB_NAME=XEPDB1 \
    DB_USER=system \
    DB_PASS=orclvk3

EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
```

### 3. Rodar Oracle XE
```text
docker login container-registry.oracle.com
```
<p> Precisa de uma conta https://container-registry.oracle.com/ords/f?p=113:10. </br>
- Username: SSO Username </br>
- Password: Token gerado no `Auth Token`
</p>

```text
docker run -d --name oracle-db \
  -p 1521:1521 -e ORACLE_PWD=orclvk3 \
  container-registry.oracle.com/database/express:21.3.0-xe
```

### 4. Acessar pelo Swagger
```text
http://<IP_VM>:8080/swagger-ui/index.html
```


### 5. Verificar dados no Oracle via terminal
```text
docker exec -it oracle-db bash
```

```text
sqlplus system/orclvk3@localhost:1521/XEPDB1
```
<p> Utilize `SELECT * FROM MOTO_JAVA;` `SELECT * FROM VAGA_JAVA;` para verificar</p>

- ### 6. (Opcional) Segurança
<p> Adicione isso ao .gitignore para não subir tokens: </p>

```text
echo ".docker/" >> .gitignore
```

---

## Como instalar o Gradle

> ```bash
> wget https://services.gradle.org/distributions/gradle-8.5-bin.zip -P /tmp
> sudo unzip -d /opt/gradle /tmp/gradle-8.5-bin.zip
> echo 'export PATH=$PATH:/opt/gradle/gradle-8.5/bin' >> ~/.bashrc
> source ~/.bashrc
> ```
