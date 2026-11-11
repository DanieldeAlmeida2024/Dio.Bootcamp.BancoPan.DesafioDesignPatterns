# API de clientes com padrões Strategy, Singleton e Facade

v1.0.0 (11/11/2026), base `ed0bf2c7f1ec2ecc9abd3d8340aee88cdcb8359f`. Inventário de código; casos de teste são critérios futuros, não resultados PASS.

## Arquitetura, contratos e regras reais
O módulo Java real fica em `lab-padroes-projeto-spring/` e é uma API Spring com `ClienteRestController`, `ClienteService`, `ClienteServiceImpl`, entidades `Cliente` e `Endereco`, dois repositórios Spring Data e cliente declarativo `ViaCepService`. A implementação marca Strategy na interface de serviço, Singleton no bean Spring e Facade na unificação entre H2/JPA e ViaCEP.

`GET /clientes` lista registros, `GET /clientes/{id}` usa `findById(id).get()`, `POST /clientes` salva, `PUT /clientes/{id}` atualiza se encontrar ID e `DELETE /clientes/{id}` remove pelo ID. O método `salvarClienteComCep` procura `Endereco` pelo CEP; só consulta ViaCEP quando não há endereço existente. `clienteRepository.save(cliente)` persiste a associação. O código não mostra tratamento seguro do Optional vazio em buscarPorId; `get()` pode lançar exceção, e a atualização reutiliza o objeto recebido sem garantir identidade do ID original. Revisar antes de produção.

Os comentários originais atribuem autoria a falvojr; documentar como laboratório baseado em exercício, sem reivindicar criação original do exemplo. Não assumir o sucesso de chamadas externas sem execução.

## Comando de validação
```bash
cd lab-padroes-projeto-spring && ./mvnw test
```

## Riscos e limites
Os testes existentes do Maven incluem teste de inicialização, não prova de todos os casos de serviço. Stub de ViaCEP em CI e teste de persistência com banco isolado são necessários.

Documentos vinculados: [UML](../uml/README.md), [plano de testes](../testes/PLANO_DE_TESTES.md) e [controle documental](../controle/VERSIONAMENTO.md).
