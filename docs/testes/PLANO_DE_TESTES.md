> **Modelagem histórica de 11/11/2026.** Critérios executados e regras vigentes estão no README principal e em `docs/PLANO_TESTES.md`; não declarar estes cenários como homologados sem CI.

# Matriz de testes — API de clientes com padrões Strategy, Singleton e Facade

v1.0.0 (11/11/2026), base `ed0bf2c7f1ec2ecc9abd3d8340aee88cdcb8359f`. Inventário de código; casos de teste são critérios futuros, não resultados PASS.

| ID | Cenário | Resultado esperado | Situação |
|---|---|---|---|
| DP-01 | POST /clientes com CEP novo | Consulta ViaCEP e grava Endereco/Cliente | Pendente de execução específica |
| DP-02 | POST com CEP já gravado | Reusa Endereco e não chama ViaCEP novamente | Pendente de execução específica |
| DP-03 | GET /clientes/{id} válido | Retorna Cliente e Endereco corretos | Pendente de execução específica |
| DP-04 | GET com ID inexistente | Comportamento de Optional.get() deve ser tratado | Pendente de execução específica |
| DP-05 | PUT com ID não encontrado | Não criar cliente inesperado | Pendente de execução específica |
| DP-06 | PUT com ID encontrado | Verificar que identidade e CEP ficam consistentes | Pendente de execução específica |
| DP-07 | DELETE com ID existente | Registro removido sem dados órfãos | Pendente de execução específica |
| DP-08 | ViaCEP fora do ar | Retorno HTTP controlado, sem perda de dados | Pendente de execução específica |
| DP-09 | CEP inválido | Validação e erro contratual | Pendente de execução específica |
| DP-10 | Clientes compartilham CEP | Persistência não duplica endereço | Pendente de execução específica |

## Estratégia
Testes JUnit para métodos puros e service mocks; Spring Boot Test/MockMvc para status/DTO; JPA em banco de teste e casos de falha para integrações. 

```bash
cd lab-padroes-projeto-spring && ./mvnw test
```

Os testes existentes do Maven incluem teste de inicialização, não prova de todos os casos de serviço. Stub de ViaCEP em CI e teste de persistência com banco isolado são necessários.

Cada execução deverá registrar JDK, versão Maven/Spring, commit, fixtures, status, logs sem segredos e defeitos. A existência de contexto Spring testado não equivale a conformidade de todos os IDs.
