# Plano de testes

## Casos
- T01: instanciação e execução com dados válidos.
- T02: tratamento de valor inválido.
- T03: validação de comportamento do domínio.
- T04: documentação, escopo e arquitetura legíveis.

```bash
cd lab-padroes-projeto-spring
./mvnw test
./mvnw spring-boot:run
python -m unittest discover -s tests -p test_documentacao.py -v
```

**Importante:** os testes documentais não substituem execução do Maven, JUnit, banco de dados ou integração.

Limite: A chamada ViaCEP depende de serviço externo; a demonstração de padrões não é uma API pronta para produção.
