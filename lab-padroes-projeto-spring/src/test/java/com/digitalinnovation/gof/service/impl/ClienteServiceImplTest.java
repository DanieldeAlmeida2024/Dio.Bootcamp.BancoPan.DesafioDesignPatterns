package com.digitalinnovation.gof.service.impl;

import com.digitalinnovation.gof.model.Cliente;
import com.digitalinnovation.gof.model.ClienteRepository;
import com.digitalinnovation.gof.model.Endereco;
import com.digitalinnovation.gof.model.EnderecoRepository;
import com.digitalinnovation.gof.service.ViaCepService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteServiceImplTest {

    @Mock private ClienteRepository clienteRepository;
    @Mock private EnderecoRepository enderecoRepository;
    @Mock private ViaCepService viaCepService;
    @InjectMocks private ClienteServiceImpl service;

    private Cliente cliente(String nome, String cep) {
        Endereco endereco = new Endereco();
        endereco.setCep(cep);
        Cliente cliente = new Cliente();
        cliente.setNome(nome);
        cliente.setEndereco(endereco);
        return cliente;
    }

    @Test
    void inserirComCepConsultadoNormalizaEReutilizaEndereco() {
        Cliente novo = cliente("  Ana  ", "01001-000");
        Endereco resposta = new Endereco();
        resposta.setCep("01001-000");
        when(enderecoRepository.findById("01001000")).thenReturn(Optional.empty());
        when(viaCepService.consultarCep("01001000")).thenReturn(resposta);
        when(enderecoRepository.save(any(Endereco.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        service.inserir(novo);

        assertEquals("Ana", novo.getNome());
        assertEquals("01001000", novo.getEndereco().getCep());
        verify(clienteRepository).save(same(novo));
        verify(viaCepService).consultarCep("01001000");
    }

    @Test
    void enderecoEmCacheNaoFazRequisicaoViaCep() {
        Endereco cache = new Endereco();
        cache.setCep("01001000");
        when(enderecoRepository.findById("01001000")).thenReturn(Optional.of(cache));

        Cliente novo = cliente("Carlos", "01001-000");
        service.inserir(novo);

        assertSame(cache, novo.getEndereco());
        verifyNoInteractions(viaCepService);
        verify(clienteRepository).save(same(novo));
    }

    @Test
    void atualizarPreservaIdentidadeDaLinhaExistente() {
        Cliente registrado = cliente("Nome antigo", "01001000");
        registrado.setId(42L);
        Endereco cache = new Endereco();
        cache.setCep("22222222");
        when(clienteRepository.findById(42L)).thenReturn(Optional.of(registrado));
        when(enderecoRepository.findById("22222222")).thenReturn(Optional.of(cache));
        Cliente recebido = cliente("  Nome novo ", "22222-222");
        recebido.setId(999L);

        service.atualizar(42L, recebido);

        assertEquals(42L, registrado.getId());
        assertEquals("Nome novo", registrado.getNome());
        assertSame(cache, registrado.getEndereco());
        verify(clienteRepository).save(same(registrado));
        verify(clienteRepository, never()).save(same(recebido));
    }

    @Test
    void postComIdInformadoPeloClienteNaoSobrescreveOutroCadastro() {
        Cliente novo = cliente("Ana", "01001-000");
        novo.setId(777L); // Não autorizar PUT disfarçado de POST.
        Endereco cache = new Endereco();
        cache.setCep("01001000");
        when(enderecoRepository.findById("01001000")).thenReturn(Optional.of(cache));

        service.inserir(novo);

        assertNull(novo.getId(), "O serviço precisa descartar identificador de criação recebido do cliente");
        verify(clienteRepository).save(same(novo));
        verifyNoInteractions(viaCepService);
    }

    @Test
    void clienteInexistenteRetorna404EnaoSalva() {
        when(clienteRepository.findById(19L)).thenReturn(Optional.empty());
        ResponseStatusException erro = assertThrows(ResponseStatusException.class,
                () -> service.atualizar(19L, cliente("Ana", "01001000")));
        assertEquals(HttpStatus.NOT_FOUND, erro.getStatusCode());
        verify(clienteRepository, never()).save(any());
    }

    @Test
    void cepMalformadoERespotaNaoEncontradaSaoRejeitados() {
        ResponseStatusException invalido = assertThrows(ResponseStatusException.class,
                () -> service.inserir(cliente("Ana", "../etc/passwd")));
        assertEquals(HttpStatus.BAD_REQUEST, invalido.getStatusCode());
        when(enderecoRepository.findById("01001000")).thenReturn(Optional.empty());
        when(viaCepService.consultarCep("01001000")).thenReturn(null);
        ResponseStatusException ausente = assertThrows(ResponseStatusException.class,
                () -> service.inserir(cliente("Ana", "01001-000")));
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, ausente.getStatusCode());
        verify(clienteRepository, never()).save(any());
    }

    @Test
    void deleteExistenteEFalhaParaIdAusente() {
        when(clienteRepository.findById(10L)).thenReturn(Optional.of(cliente("Ana", "01001000")));
        service.deletar(10L);
        verify(clienteRepository).deleteById(10L);

        when(clienteRepository.findById(11L)).thenReturn(Optional.empty());
        ResponseStatusException erro = assertThrows(ResponseStatusException.class,
                () -> service.deletar(11L));
        assertEquals(HttpStatus.NOT_FOUND, erro.getStatusCode());
        verify(clienteRepository, never()).deleteById(11L);
    }
}
