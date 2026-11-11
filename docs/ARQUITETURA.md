# Arquitetura

Laboratório de padrões Singleton, Strategy/Repository e Facade em aplicação Spring Boot.

**Stack:** Java, Maven, Spring Boot e Spring Data.

**Camadas conceituais:** apresentação / entrada; serviços / casos de uso; domínio / entidades; adaptadores de infraestrutura (quando existem no módulo). A disposição real é a das classes do repositório, e o diagrama não presume pacotes novos.

## Decisões
- Preservar os exercícios de origem e rastrear adaptações realizadas.
- Empregar DTOs e validação em fronteiras HTTP, quando presentes.
- Em Java, preferir nomes de domínio coerentes, testes JUnit e inversão de dependências.
- Evitar alteração de framework sem testes de compatibilidade.

## Limitações
A chamada ViaCEP depende de serviço externo; a demonstração de padrões não é uma API pronta para produção.
