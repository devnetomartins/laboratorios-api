# laboratorios-api

> Api para gerenciamento de laboratórios

## Passos para execução
>Certifique-se de ter o Maven instalado e adicionado ao PATH de seu sistema operacional, assim como o Git e o banco de dados postgress.

``` bash

# Clone o projeto
$ git clone https://github.com/devnetomartins/laboratorios-api.git
# Entre no diretório
$ cd laboratorios-api
# Configure o seu banco de dados
$ nano src/main/resources/application.properties
# Instale as dependencias e inicie o server
$ mvn spring-boot:run
#Acesse a API
Acesse http://localhost:8080

```
## APIs endpoints

>Essa API conta com 3 rotas principais onde nelas teram diversos endpoints

- Laboratórios

    - GET http://localhost:8080/api/laboratorios [lista todos os laboratórios]
        - Success Response:
            ```javascript
            [
                {
                    "id": 0,
                    "nome": "string",
                    "rua": "string",
                    "numero": 0,
                    "bairro": "string",
                    "cidade": "string",
                    "uf": "string",
                    "cep": "string",
                    "status": true
                }
            ]
            ```
            OR
            ```javascript
            []
            ```
    - POST http://localhost:8080/api/laboratorios [cadastra um novo laboratório]
        - Required
            ```javascript
            {
                "nome": "string",
                "rua": "string",
                "numero": 0,
                "bairro": "string",
                "cidade": "string",
                "uf": "string",
                "cep": "string",
            }
            ```

    - POST http://localhost:8080/api/laboratorios/lotes [cadastra uma lista de laboratório]
        - Required
            ```javascript
            [
                {
                    "nome": "string",
                    "rua": "string",
                    "numero": 0,
                    "bairro": "string",
                    "cidade": "string",
                    "uf": "string",
                    "cep": "string",
                }
            ]
            ```
    - PUT http://localhost:8080/api/laboratorios/ [atualiza os dados de um laboratório]
        - Required
            ```javascript
            {   
                "id": 0,
                "nome": "string",
                "rua": "string",
                "numero": 0,
                "bairro": "string",
                "cidade": "string",
                "uf": "string",
                "cep": "string",
                "status": true
            }
            ```
    - PUT http://localhost:8080/api/laboratorios/lotes [atualiza uma lista laboratório]
        - Required
            ```javascript
            [
                {   
                    "id": 0,
                    "nome": "string",
                    "rua": "string",
                    "numero": 0,
                    "bairro": "string",
                    "cidade": "string",
                    "uf": "string",
                    "cep": "string",
                    "status": true
                }
            ]
            ```
    - DELETE http://localhost:8080/api/laboratorios/ [remove um laboratório]
        - Required
            ```javascript
            {   
                "id": 0,
            }
            ```
    - DELETE http://localhost:8080/api/laboratorios/lotes [remove uma lista laboratório]
        - Required
            ```javascript
            [
                {   
                    "id": 0,
                }
            ]
            ```

- Exames

    - GET http://localhost:8080/api/exames [lista todos os exames]
        - Success Response:
            ```javascript
            [
                {
                    "id": 0,
                    "nome": "string",
                    "tipo": "string",
                    "status": true
                }
            ]
            ```
            OR
            ```javascript
            []
            ```
    - GET http://localhost:8080/api/exames/{nome} [lista um exame por nome]
        - Success Response:
            ```javascript
            {   
                "id": 0,
                "nome": "string",
                "tipo": "string",
                "status": true
            }
            ```
    - POST http://localhost:8080/api/exames [cadastra um novo exame]
        - Tipos aceitos: [analise clinica, imagem]
        - Required
            ```javascript
            {
                "nome": "string",
                "tipo": "string"
            }
        ```
    - POST http://localhost:8080/api/exames/lotes [cadastra uma lista de exames]
        - Tipos aceitos: [analise clinica, imagem]
        - Required
            ```javascript
            [
                {
                    "nome": "string",
                    "tipo": "string"
                }
            ]
            ```
    - PUT http://localhost:8080/api/exames [atualiza os dados de um exame]
        - Tipos aceitos: [analise clinica, imagem]
        - Required
            ```javascript
            {   
                "id": 0,
                "nome": "string",
                "tipo": "string"
                "status": true
            }
            ```
    - PUT http://localhost:8080/api/exames/lotes [atualiza uma lista exames]
        - Tipos aceitos: [analise clinica, imagem]
        - Required
            ```javascript
            [
                {   
                    "id": 0,
                    "nome": "string",
                    "tipo": "string"
                    "status": true
                }
            ]
            ```
    - DELETE http://localhost:8080/api/exames/ [remove um exame]
        - Required
            ```javascript
            {   
                "id": 0,
            }
            ```
    - DELETE http://localhost:8080/api/exames/lotes [remove uma lista exames]
        - Required
            ```javascript
            [
                {   
                    "id": 0,
                }
            ]
            ```

- Associação (associar um exame a um ou vários laboratórios):
    - POST http://localhost:8080/api/exames/associacao [associa um exame a um laboratorio]
        - Required
            ```javascript
            {
                "idExame": 0,
                "idLaboratorio": 0
            }
            ```
    - DELETE http://localhost:8080/api/exames/associacao [remove uma associacao]
        - Required
            ```javascript
            {
                "idExame": 0,
                "idLaboratorio": 0
            }
            ```


## Arquitetura

```
O projeto funciona da seguinte maneira:
A classe ApirestApplication localizada no package com.laboratorios.apirest inicia o servidor. Apartir dela o servidor começa a funcionar. 
Temos diversos packages cada um com suas funções afim de manter uma boa arquitetura do projeto. No arquivo application.properties que fica localizado no diretorio src/main/resources/ fica a nossa conexão com o banco de dados postgres. 
No package com.laboratorios.apirest.models ficam as classes models, as entidades do nosso banco de dados onde elas tem todo os parametros a serem usados para manipulação dos dados. 
No package com.laboratorios.apirest.repository ficam as classes responsáveis na manipulação no banco de dados onde temos funções automaticas como o delete e save podendo também criar novas funções afim de manipular a sua entidade. 
No package com.laboratorios.apirest.resources fica toda a nossa lógica do funcionamento do backend. Nela estão as nossas rotas que podem ser acessadas apartir das urls e respeitando os parâmetros apresentados no doc apresentado acima. Podendo assim, implementar novas rotas e novas funcionalidades.
```
