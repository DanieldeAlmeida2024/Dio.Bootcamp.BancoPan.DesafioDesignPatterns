# Plano de testes — CRUD de clientes e ViaCEP

Versão 1.1.0 · 14/11/2026

## Execução em CI

```bash
mvn -B -ntp -f lab-padroes-projeto-spring/pom.xml test
```

O workflow `testes-clientes.yml` usa JDK 17 e Maven. `ClienteServiceImplTest` usa JUnit 5 e Mockito para simular repositórios e ViaCEP, sem tráfego de rede. O teste existente `LabPadroesProjetoSpringApplicationTests` verifica a inicialização do contexto Spring.

| ID | Cenário | Evidência esperada |
| --- | --- | --- |
| DP-01 | Inserir cliente e CEP `01001-000` não cacheado | OpenFeign invocado com `01001000`, resposta normalizada e cliente salvo |
| DP-02 | Inserir cliente com CEP em cache | Nenhuma consulta ViaCEP; reutiliza endereço |
| DP-03 | Atualizar ID 42 com objeto cujo corpo traz ID 999 | Persistir a entidade existente com ID 42, sem criar ID 999 |
| DP-04 | Atualizar cliente que não existe | HTTP 404, nenhuma gravação |
| DP-05 | CEP com texto não numérico | HTTP 400, sem salvar |
| DP-06 | API devolve resposta sem endereço | HTTP 422, sem salvar |
| DP-07 | Deletar cliente existente | Repositório `deleteById` executado |
| DP-08 | Deletar cliente inexistente | HTTP 404, sem remoção |
| DP-09 | Iniciar aplicação de teste | Contexto Spring sobe com H2 |
| DP-10 | POST envia ID 777, mas é criação | Serviço zera o ID antes de JPA save |
| DP-11 | PUT com ID 999 no JSON e URL ID 15 | Controller devolve ID 15 e CEP normalizado do registro salvo |
| DP-12 | POST no controller | Status 201 e invocação de inserção |

## Testes adicionais antes de disponibilização externa

- E2E com servidor HTTP, banco e ViaCEP simulado em rede; os testes MockMvc adicionados verificam apenas o controller com service mockado.
- Timeout, rate limiting, respostas inválidas do ViaCEP e integração E2E com HTTP isolado.
- Controle de acesso e proteção de dados, validação de nome/CEP detalhada, transações e concorrência no cache.
- Benchmark do serviço, migrações de banco e auditoria de dependências.

**Não confundir:** o teste unitário com mocks não comprova funcionamento do ViaCEP real ou de toda a API em produção.
