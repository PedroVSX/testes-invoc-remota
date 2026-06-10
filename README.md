# Estudo Comparativo de Desempenho de Invocação Remota: REST, GraphQL, SOAP e gRPC

Este repositório apresenta um projeto de benchmark acadêmico voltado à análise de métricas, comportamento e engenharia de desempenho de quatro tecnologias e estilos arquiteturais de comunicação em sistemas distribuídos: **REST**, **GraphQL**, **SOAP** e **gRPC**.

O domínio do problema simula um ecossistema de streaming de música, gerenciando dados integrados de **Usuários**, **Músicas** e **Playlists**. O sistema expõe as mesmas regras de negócio simultaneamente em um único servidor Spring Boot rodando localmente, e os testes de estresse computacional foram executados utilizando cenários de injeção de carga controlados através do **Locust**.

---
### Equipe:

Cainã Rocha - 2315038

Davi Silveira - 2310347

Marcos André - 2310371

Pedro Vieira - 2315708

### Universidade de Fortaleza — UNIFOR

---

## 🏗️ O Que Fizemos (Arquitetura do Sistema)

Desenvolvemos um servidor robusto em Java Spring Boot que elimina o overhead de IO de base de dados tradicional. A classe `MockRepository` utiliza a anotação `@PostConstruct` para efetuar o *parsing* eficiente do arquivo estático `dados_streaming.json` através da biblioteca Jackson para estruturas de mapas concorrentes (`ConcurrentHashMap`). Isso garante consultas instantâneas e sem gargalos de leitura em disco, isolando estritamente o tempo gasto pelo protocolo de rede.

Para fins de validação e comparação justa no benchmark, foram avaliados 5 cenários críticos baseados em perguntas de negócio (Q1 a Q5) usando os mesmos parâmetros de busca estáticos (`USER_ID = "u50"`, `PLAYLIST_ID = "p50"` e `MUSIC_ID = "m50"`) em todas as implementações do Locust:

| Cenário / Pergunta | REST (`/api/*`) | GraphQL (`/graphql`) | SOAP (`/ws/usuarios`) | gRPC (`StreamingService`) |
| --- | --- | --- | --- | --- |
| **Q1:** Listar dados de todos os usuários | `GET /users` | Query: `listAllUsers` | `listAllUsersRequest` | Método: `ListAllUsers` |
| **Q2:** Listar dados de todas as músicas | `GET /musics` | Query: `listAllMusics` | `listAllMusicsRequest` | Método: `ListAllMusics` |
| **Q3:** Listar as playlists de um usuário | `GET /users/{userId}/playlists` | Query: `listPlaylistsByUser` | `listPlaylistsByUserRequest` | Método: `ListPlaylistsByUser` |
| **Q4:** Listar as músicas de uma playlist | `GET /playlists/{playlistId}/musics` | Query: `listMusicsFromPlaylist` | `listMusicsFromPlaylistRequest` | Método: `ListMusicsFromPlaylist` |
| **Q5:** Listar as playlists que contêm uma música | `GET /musics/{musicId}/playlists` | Query: `listPlaylistsByMusic` | `listPlaylistsByMusicRequest` | Método: `ListPlaylistsByMusic` |

### Esquemas de Contrato de Dados Utilizados:

* **REST (`StreamingRestController.java`):** Payload nativo serializado via instâncias de `ResponseEntity` em formato JSON estruturado.
* **gRPC (`streaming.proto` / `StreamingGrpcService.java`):** Transmissão fortemente tipada usando codificação binária compacta através de stubs gerados nativamente.
* **GraphQL (`schema.graphqls` / `StreamingGraphQLController.java`):** Consultas com flexibilidade declarativa e seleção sob demanda de campos das entidades mapeadas.
* **SOAP (`streaming.xsd` / `StreamingSoapEndpoint.java`):** Validação estrita baseada em XML Schema e mapeamentos direcionados para o servlet configurado em `WebServiceConfig.java` na URI `/ws/*`.

---

## 🛠️ Como Fizemos (Instalação e Execução Local)

### Pré-requisitos

* **Java JDK 17** ou superior instalado.
* **Maven** instalado (ou uso do wrapper `./mvnw`).
* **Python 3.9+** com a ferramenta **Locust** e a biblioteca **grpcio** instaladas localmente:
```bash
pip install locust grpcio

```



### 1. Executando o Servidor Back-end (Spring Boot)

Navegue até a pasta `apis/springboot/` e inicialize a aplicação:

```bash
# Compilar e rodar o projeto nativamente
mvn spring-boot:run

```

O servidor estará ativo localmente escutando nas portas:

* **`8080`**: REST, GraphQL e SOAP
* **`9090`**: gRPC

### 2. Executando os Testes de Carga (Locust)

Como os scripts foram segmentados por tecnologia para garantir isolamento nas métricas, você deve rodar o Locust apontando para o arquivo do protocolo que deseja testar no momento.

A partir da raiz do projeto, execute o comando correspondente no seu terminal:

* **Para testar REST:**
```bash
locust -f load-tests/locustfile-rest.py --host=http://localhost:8080

```


* **Para testar GraphQL:**
```bash
locust -f load-tests/locustfile-graphql.py --host=http://localhost:8080

```


* **Para testar SOAP:**
```bash
locust -f load-tests/locustfile-soap.py --host=http://localhost:8080

```


* **Para testar gRPC:**
```bash
locust -f load-tests/locustfile-grpc.py

```


---

## 📊 Nossos Resultados nos Cenários de Carga (Leve, Moderada e Pesada)

Para validar a resiliência e o comportamento dinâmico de cada protocolo, submetemos a infraestrutura local a três baterias sequenciais de testes de estresse, simulando diferentes diferentes cenários através do Locust:

### A. Cenário de Carga Leve
**Total Users:** 50 users |
**Spawn Rate:** 5 users/s |
**Duração:** 3 min

![](./results/Carga%20Leve%20(Java).png)

**Comportamento Observado:** Os tempos de resposta de todos os protocolos possuem um tempo baixo. Porém, ainda sim é perceptível uma diferença entre eles. Neste cenário, o gRPC possui menor tempo de resposta, seguido por REST, GraphQL e por fim SOAP, sendo este último o que levou mais tempo.

---

### B. Cenário de Carga Moderada
**Total Users:** 500 users |
**Spawn Rate:** 20 users/s |
**Duração:** 5 min

![](./results/Carga%20Moderada%20(Java).png)

**Comportamento Observado:** Os tempos de resposta de todos os protocolos ainda possuem um tempo baixo. E mais uma vez, ainda é perceptível a diferença entre eles, perceba que REST, GraphQL e SOAP mantém uma diferença relativamente proporcional ao tempo obtido no cenário de carga leve. Neste cenário também, o gRPC possui menor tempo de resposta, só que dessa vez com um tempo bem menor em relação aos outros.

---

### C. Cenário de Carga Pesada
**Total Users:** 2500 users |
**Spawn Rate:** 100 users/s |
**Duração:** 10 min

![](./results/Carga%20Pesada%20(Java).png)

**Comportamento Observado:** Os tempos de resposta dos protocolos REST, GraphQL e SOAP tiveram um crescimento muito significativo, obtendo tempos por volta da casa dos segundos. Mas, mesmo assim, essas 3 tecnologias ainda mantém aquela certa diferença proporcional entre elas. No entanto, o gRPC mais uma vez obtém o menor tempo de resposta, com tempo abaixo de 1ms (não é possível perceber atráves do gráfico devido às proporções dos valores de tempo). 
