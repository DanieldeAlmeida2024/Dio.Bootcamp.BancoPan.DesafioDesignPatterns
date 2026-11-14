package com.digitalinnovation.gof.service.impl;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.digitalinnovation.gof.model.Cliente;
import com.digitalinnovation.gof.model.ClienteRepository;
import com.digitalinnovation.gof.model.Endereco;
import com.digitalinnovation.gof.model.EnderecoRepository;
import com.digitalinnovation.gof.service.ClienteService;
import com.digitalinnovation.gof.service.ViaCepService;

/**
 * Implementação didática de Strategy/Repository e Facade da integração ViaCEP.
 * Mantém a autoria do exercício original; correções de integridade e erros HTTP.
 */
@Service
@Transactional
public class ClienteServiceImpl implements ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private EnderecoRepository enderecoRepository;

    @Autowired
    private ViaCepService viaCepService;

    @Override
    @Transactional(readOnly = true)
    public Iterable<Cliente> buscarTodos() {
        return clienteRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Cliente buscarPorId(Long id) {
        if (id == null || id <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID do cliente inválido");
        }
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Cliente não encontrado"));
    }

    @Override
    public void inserir(Cliente cliente) {
        validarCliente(cliente);
        // POST representa criação: ignorar IDs enviados pelo cliente para impedir
        // que save(id existente) sobrescreva um cadastro anterior.
        cliente.setId(null);
        salvarClienteComCep(cliente);
    }

    @Override
    public void atualizar(Long id, Cliente alteracoes) {
        // Atualiza o registro existente em vez de criar outro com id vindo do JSON.
        Cliente existente = buscarPorId(id);
        validarCliente(alteracoes);
        existente.setNome(alteracoes.getNome().trim());
        existente.setEndereco(alteracoes.getEndereco());
        salvarClienteComCep(existente);
    }

    @Override
    public void deletar(Long id) {
        buscarPorId(id);
        clienteRepository.deleteById(id);
    }

    private void salvarClienteComCep(Cliente cliente) {
        validarCliente(cliente);
        String cep = normalizarCep(cliente.getEndereco().getCep());

        Endereco endereco = enderecoRepository.findById(cep).orElseGet(() -> {
            Endereco consultado;
            try {
                consultado = viaCepService.consultarCep(cep);
            } catch (RuntimeException erro) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                        "Falha ao consultar o ViaCEP", erro);
            }
            if (consultado == null || consultado.getCep() == null ||
                !cep.equals(consultado.getCep().replace("-", ""))) {
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                        "CEP não encontrado ou resposta inválida do ViaCEP");
            }
            // ViaCEP pode responder com hífen no CEP; usar um único formato no banco.
            consultado.setCep(cep);
            return enderecoRepository.save(consultado);
        });

        cliente.setNome(cliente.getNome().trim());
        cliente.setEndereco(endereco);
        clienteRepository.save(cliente);
    }

    private void validarCliente(Cliente cliente) {
        if (cliente == null || cliente.getNome() == null || cliente.getNome().isBlank() ||
            cliente.getEndereco() == null || cliente.getEndereco().getCep() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Nome e endereço/CEP são obrigatórios");
        }
    }

    private String normalizarCep(String cep) {
        if (cep == null || !cep.matches("\\d{5}-?\\d{3}")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "CEP deve conter 8 dígitos");
        }
        return cep.replace("-", "");
    }
}
