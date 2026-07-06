package br.ufes.deliverypedidos.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.ufes.deliverypedidos.domain.model.Papel;
import br.ufes.deliverypedidos.dto.EnderecoDTO;
import br.ufes.deliverypedidos.dto.request.ClienteRequest;
import br.ufes.deliverypedidos.dto.request.ItemPedidoRequest;
import br.ufes.deliverypedidos.dto.request.LoginRequest;
import br.ufes.deliverypedidos.dto.request.PedidoRequest;
import br.ufes.deliverypedidos.dto.request.ProdutoRequest;
import br.ufes.deliverypedidos.dto.request.RegisterRequest;
import br.ufes.deliverypedidos.dto.request.RestauranteRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AutorizacaoIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private EnderecoDTO endereco() {
        return new EnderecoDTO("Rua A", "10", "Centro", "Vitória", "29000-000");
    }

    private String registrarELogar(String email, Papel papel) throws Exception {
        mockMvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content(json(new RegisterRequest("Fulano", email, "senha123", papel))))
                .andExpect(status().isCreated());
        MvcResult login = mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content(json(new LoginRequest(email, "senha123"))))
                .andExpect(status().isOk())
                .andReturn();
        String token = objectMapper.readTree(login.getResponse().getContentAsString()).get("token").asText();
        return "Bearer " + token;
    }

    private long criar(String path, String token, Object corpo) throws Exception {
        MvcResult res = mockMvc.perform(post(path).header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON).content(json(corpo)))
                .andExpect(status().isCreated())
                .andReturn();
        return objectMapper.readTree(res.getResponse().getContentAsString()).get("id").asLong();
    }

    private String json(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }

    @Test
    void semTokenRetorna401() throws Exception {
        mockMvc.perform(post("/pedidos").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void papelErradoRetorna403() throws Exception {
        String entregador = registrarELogar("entregador@x.com", Papel.ENTREGADOR);
        mockMvc.perform(post("/clientes").header("Authorization", entregador)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(new ClienteRequest("João", "joao@x.com", "279", endereco()))))
                .andExpect(status().isForbidden());
    }

    @Test
    void clienteCriaSeuPerfilRetorna201() throws Exception {
        String cliente = registrarELogar("cliente@x.com", Papel.CLIENTE);
        criar("/clientes", cliente, new ClienteRequest("João", "joao@x.com", "279", endereco()));
    }

    @Test
    void fluxoDePedidoRespeitaPosseEEstado() throws Exception {
        String clienteToken = registrarELogar("c1@x.com", Papel.CLIENTE);
        criar("/clientes", clienteToken, new ClienteRequest("João", "joao@x.com", "279", endereco()));

        String restauranteToken = registrarELogar("r1@x.com", Papel.RESTAURANTE);
        long restauranteId = criar("/restaurantes", restauranteToken,
                new RestauranteRequest("Cantina", "27", "Italiana", new BigDecimal("5.00"), endereco()));
        long produtoId = criar("/produtos", restauranteToken,
                new ProdutoRequest("Pizza", "Calabresa", new BigDecimal("40.00"), true, restauranteId));

        long pedidoId = criar("/pedidos", clienteToken,
                new PedidoRequest(restauranteId, endereco(), List.of(new ItemPedidoRequest(produtoId, 2))));

        // O cliente não pode confirmar: quem confirma é o restaurante dono.
        mockMvc.perform(patch("/pedidos/{id}/confirmar", pedidoId).header("Authorization", clienteToken))
                .andExpect(status().isForbidden());

        // O restaurante dono confirma com sucesso.
        mockMvc.perform(patch("/pedidos/{id}/confirmar", pedidoId).header("Authorization", restauranteToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONFIRMADO"));

        // Outro cliente não enxerga o pedido alheio.
        String outroCliente = registrarELogar("c2@x.com", Papel.CLIENTE);
        criar("/clientes", outroCliente, new ClienteRequest("Maria", "maria@x.com", "279", endereco()));
        mockMvc.perform(patch("/pedidos/{id}/cancelar", pedidoId).header("Authorization", outroCliente))
                .andExpect(status().isForbidden());
    }
}
