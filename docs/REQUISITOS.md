# Requisitos — laboratório Design Patterns com clientes

Versão 1.1.0 · 14/11/2026

## Requisitos funcionais

- **RF01:** listar, consultar, inserir, atualizar e excluir `Cliente` por ID.
- **RF02:** ao atualizar, preservar o ID identificado pela URL, ignorando ID conflitante no corpo.
- **RF03:** validar nome obrigatório e CEP de oito dígitos (`00000-000` ou `00000000`).
- **RF04:** buscar endereço no repositório por CEP normalizado; consultar ViaCEP apenas em cache miss.
- **RF05:** rejeitar endereço não encontrado e distinguir falhas de integração.
- **RF06:** responder 201 ao inserir, 200 ao consultar/atualizar e 204 ao excluir.

## Requisitos não funcionais

- **RNF01:** operação de gravação transacional; exceções provocam rollback.
- **RNF02:** OpenFeign com timeout de conexão (3000 ms) e leitura (5000 ms).
- **RNF03:** validar entradas antes de persistir, sem alterar identidade dos registros.
- **RNF04:** cobrir cenários críticos com testes JUnit/Mockito no GitHub Actions.
- **RNF05:** documentação específica do código presente e atribuição ao desafio original.

## Limitações

Não há autenticação/autorização, limite de requisições, integração real de rede em CI ou demonstração de suporte a cenários concorrentes. O projeto é um exercício, não um serviço público pronto.
