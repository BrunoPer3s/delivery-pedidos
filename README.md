# Delivery de Pedidos — API REST

API REST de um sistema de delivery, desenvolvida com Java 21 e Spring Boot. Este documento
traz apenas as instruções para configurar e executar o projeto em ambiente local, sem depender
de nenhuma IDE.

## Pré-requisitos

- **JDK 21** instalado e disponível no `PATH` (`java -version` deve mostrar a versão 21).
- **Não é preciso instalar o Maven**: o projeto já inclui o Maven Wrapper (`mvnw`), que baixa a
  versão correta automaticamente.
- Conexão com a internet no primeiro build (para baixar as dependências).

## Como obter o projeto

```
git clone https://github.com/BrunoPer3s/delivery-pedidos.git
cd delivery-pedidos
```

## Como executar

Na raiz do projeto, rode:

- **Windows (PowerShell ou Prompt de Comando):**
  ```
  .\mvnw.cmd spring-boot:run
  ```
- **Linux / macOS / Git Bash:**
  ```
  ./mvnw spring-boot:run
  ```

A aplicação sobe em **http://localhost:8080**. Para parar, use `Ctrl + C`.

## Gerar o pacote e rodar os testes

- Compilar e empacotar (gera o `.jar` em `target/`):
  ```
  ./mvnw clean package
  ```
- Executar o `.jar` gerado:
  ```
  java -jar target/deliverypedidos-0.0.1-SNAPSHOT.jar
  ```
- Rodar os testes automatizados:
  ```
  ./mvnw test
  ```

> No Windows, use `mvnw.cmd` no lugar de `./mvnw` nos comandos acima.

## Banco de dados

O projeto usa **H2 em modo arquivo**, então **não é necessário instalar nenhum banco**. Os dados
são gravados na pasta `data/` (criada automaticamente na primeira execução) e sobrevivem a
reinícios da aplicação.

O console web do H2 fica disponível em **http://localhost:8080/h2-console**, com os seguintes
dados de conexão:

| Campo     | Valor                          |
|-----------|--------------------------------|
| JDBC URL  | `jdbc:h2:file:./data/deliverydb` |
| User Name | `sa`                           |
| Password  | *(em branco)*                  |

## Documentação da API (Swagger)

Com a aplicação rodando, a documentação interativa (Swagger UI) fica em:

- **http://localhost:8080/swagger-ui.html**

As rotas protegidas exigem um token JWT: gere um token em `POST /auth/login`, clique no botão
**Authorize** e informe o token para testar os endpoints pela própria interface.

## Testando com o Postman

A pasta `postman/` contém dois arquivos prontos para importar:

- `delivery-api.postman_collection.json` — as requisições organizadas por etapa;
- `delivery-api.postman_environment.json` — as variáveis de ambiente (URL base, tokens e ids).

Passos:

1. No Postman, clique em **Import** e selecione os dois arquivos.
2. No seletor de ambiente (canto superior direito), escolha **"Delivery API - Local"**.
3. Deixe a aplicação rodando (`spring-boot:run`) e execute as requisições — os tokens e ids são
   preenchidos automaticamente conforme as requisições são disparadas.
