# Dio.Bootcamp.BancoPan.DesafioDesignPatterns

Laboratório de padrões Singleton, Strategy/Repository e Facade em aplicação Spring Boot.

## Tecnologias e dependências

Java, Maven, Spring Boot e Spring Data.

JDK da versão definida no pom.xml; rede para integração ViaCEP.

## Como executar

```bash
cd lab-padroes-projeto-spring
./mvnw test
./mvnw spring-boot:run
```

## Contrato HTTP

O cadastro `POST /clientes` é criação, não atualização: qualquer `id` enviado pelo cliente é ignorado antes da persistência. O `PUT /clientes/{id}` responde com o registro **relido da persistência**, contendo o CEP normalizado e o ID realmente gravado, e não devolve simplesmente o JSON recebido. Esses dois casos são testados com Mockito e MockMvc sem consulta ao ViaCEP real.

## Mapa do projeto

- [Requisitos](docs/REQUISITOS.md)
- [Arquitetura](docs/ARQUITETURA.md)
- [UML](docs/UML.puml)
- [Testes](docs/PLANO_TESTES.md)
- [Orientações para ferramentas de IA](AGENTS.md)

**Condição:** projeto didático. A chamada ViaCEP depende de serviço externo; a demonstração de padrões não é uma API pronta para produção.

---

## Referência do desafio: padrões de projeto em Java

Repositório com as implementações dos padrões de projeto explorados no Lab "Explorando Padrões de Projetos na Prática com Java". Especificamente, este projeto explorou alguns padrões usando o Spring Framework, são eles:
- Singleton
- Strategy/Repository
- Facade

## Contrato de nome no cadastro de clientes

O serviço valida nomes com **3 a 100 caracteres após remover espaços nas extremidades**, rejeitando entradas vazias, muito curtas ou extensas antes de consultar ViaCEP ou acessar repositórios. A mesma regra funciona quando o serviço é chamado sem passar pelo controlador REST. As regressões com Mockito verificam que nenhuma operação externa ocorre para um nome inválido.

## Documentação específica do domínio

[Especificação da implementação](docs/projeto/ESPECIFICACAO.md) · [Modelo e sequências UML](docs/uml/README.md) · [Matriz de testes histórica](docs/testes/PLANO_DE_TESTES.md) · [Histórico documental](docs/controle/VERSIONAMENTO.md).

Esses arquivos preservam a modelagem original do exercício; as verificações executadas e os requisitos atuais estão no [plano de testes vigente](docs/PLANO_TESTES.md) e nas execuções do GitHub Actions.
