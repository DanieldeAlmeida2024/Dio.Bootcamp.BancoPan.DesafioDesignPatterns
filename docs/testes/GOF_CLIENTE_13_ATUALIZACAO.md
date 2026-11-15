# GOF-CLIENTE-13 — Atualização sem mutação parcial

**Revisão:** 15/11/2026 · Camada de domínio Spring Boot/Facade ViaCEP.

## Defeito encontrado

`ClienteServiceImpl.atualizar` modificava imediatamente o `nome` e o `endereco` da entidade já carregada, e **depois** tentava validar o CEP e consultar o ViaCEP. Caso o fornecedor falhasse, o objeto gerenciado já estava alterado, mesmo que o serviço lançasse um erro HTTP. A transação Spring pode reverter o banco, mas a mutação parcial em memória surpreende outros chamadores e testes diretos.

## Correção

- Resolver e validar CEP, cache e resposta externa **antes** de atribuir novos campos à entidade carregada.
- Extrair `resolverEndereco` para reutilizar a mesma regra em POST/PUT.
- Só aplicar nome/endereço e chamar `clienteRepository.save` depois que a preparação estiver completa.
- Testes Mockito reproduzem ViaCEP retornando `null` e CEP malformado: devem preservar o estado anterior e não chamar save.
- Mantêm-se status 400/422/502 e contratos POST/PUT vigentes, sem acessar ViaCEP real.

## Limites

A alteração não transforma a atualização em operação transacional distribuída. Concorrência de clientes, cache de CEP, circuit breaker e autenticação de usuários continuam fora do escopo de liberação de produção.
