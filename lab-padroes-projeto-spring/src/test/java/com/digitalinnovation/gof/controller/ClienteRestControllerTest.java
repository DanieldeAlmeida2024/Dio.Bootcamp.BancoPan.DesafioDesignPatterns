package com.digitalinnovation.gof.controller;

import com.digitalinnovation.gof.model.Cliente;
import com.digitalinnovation.gof.model.Endereco;
import com.digitalinnovation.gof.service.ClienteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ClienteRestControllerTest {
    private MockMvc mvc;
    private ClienteService service;

    @BeforeEach
    void preparar() {
        service = mock(ClienteService.class);
        ClienteRestController controller = new ClienteRestController();
        ReflectionTestUtils.setField(controller, "clienteService", service);
        mvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void putRetornaEntidadePersistidaEmVezDeRepetirPayloadBruto() throws Exception {
        Endereco endereco = new Endereco();
        endereco.setCep("01001000");
        Cliente persisted = new Cliente();
        persisted.setId(15L);
        persisted.setNome("Nome salvo");
        persisted.setEndereco(endereco);
        when(service.buscarPorId(15L)).thenReturn(persisted);

        mvc.perform(put("/clientes/15")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\":999,\"nome\":\" Nome salvo \",\"endereco\":{\"cep\":\"01001-000\"}}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(15))
            .andExpect(jsonPath("$.nome").value("Nome salvo"))
            .andExpect(jsonPath("$.endereco.cep").value("01001000"));

        verify(service).atualizar(eq(15L), any(Cliente.class));
        verify(service).buscarPorId(15L);
    }

    @Test
    void postMantemStatus201EPassaAoServico() throws Exception {
        mvc.perform(post("/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nome\":\"Ana\",\"endereco\":{\"cep\":\"01001000\"}}"))
            .andExpect(status().isCreated());
        verify(service).inserir(any(Cliente.class));
    }
}
