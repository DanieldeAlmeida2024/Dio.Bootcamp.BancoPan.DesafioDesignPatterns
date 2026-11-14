# Arquitetura — Facade de clientes e integração ViaCEP

Versão 1.1.0 · 14/11/2026

## Componentes existentes

O exercício demonstra **Strategy/Repository** pela interface `ClienteService` e sua implementação, **Facade** pelo `ClienteRestController` e **Singleton** pelo escopo padrão dos beans Spring. O padrão não substitui autenticação ou regras de autorização.

- `controller/ClienteRestController.java`: rotas `GET /clientes`, `GET /clientes/{id}`, `POST /clientes`, `PUT /clientes/{id}`, `DELETE /clientes/{id}`.
- `service/impl/ClienteServiceImpl.java`: busca, validação de nome/CEP, atualização preservando identidade e deleção, com transações.
- `model/ClienteRepository.java` e `EnderecoRepository.java`: persistência JPA de clientes e cache de endereços por CEP.
- `service/ViaCepService.java`: adaptador OpenFeign HTTPS para `viacep.com.br`; prazo de conexão de 3 s e resposta de 5 s.
- `model/Cliente.java` e `Endereco.java`: entidades JPA. O CEP é normalizado para oito dígitos, sem hífen.

## Fluxo de inserção e atualização

1. Validar `Cliente.nome`, `Cliente.endereco` e formato de CEP. Normalizar `01001-000` para `01001000`.
2. Verificar `EnderecoRepository.findById(cep)`. Se existir, não acessar a API externa.
3. Se não existir, buscar ViaCEP, validar resposta e gravar endereço normalizado. Erro de comunicação => HTTP 502; resposta sem CEP => HTTP 422.
4. Inserir cliente ou atualizar **a instância persistida do ID da URL**, não salvar indiscriminadamente o objeto enviado no JSON.
5. Cliente inexistente => HTTP 404. Requisição sem nome ou CEP válido => HTTP 400.

## Contratos HTTP

| Operação | Sucesso | Erros relevantes |
| --- | --- | --- |
| GET lista | 200 | — |
| GET por ID | 200 | 400 ID inválido, 404 não encontrado |
| POST | 201 | 400 dados inválidos, 422 CEP não encontrado, 502 ViaCEP indisponível |
| PUT por ID | 200 | 400, 404, 422, 502 |
| DELETE por ID | 204 | 400/404 |

## Limites de implantação

A aplicação é um laboratório com H2 e Spring. Não tem autenticação, rate limiting nem testes de carga. Os métodos na facade foram testados com repositórios simulados; a disponibilidade atual do ViaCEP depende de rede externa. Não expor publicamente sem revisar segurança e política de armazenamento de dados pessoais.

**Créditos:** laboratório original de padrões de projetos da DIO; adaptações de validação, testes e documentação realizadas nesta revisão. A atribuição original das classes foi preservada.
