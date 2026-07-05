package br.ufes.deliverypedidos.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import br.ufes.deliverypedidos.dto.EnderecoDTO;
import br.ufes.deliverypedidos.dto.request.ClienteRequest;
import br.ufes.deliverypedidos.dto.response.ClienteResponse;
import br.ufes.deliverypedidos.exception.RecursoNaoEncontradoException;
import br.ufes.deliverypedidos.exception.RegraDeNegocioException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class ClienteServiceTest {

    @Autowired
    private ClienteService service;

    private ClienteRequest requisicao(String email) {
        return new ClienteRequest("João", email, "27999999999",
                new EnderecoDTO("Rua A", "10", "Centro", "Vitória", "29000-000"));
    }

    @Test
    void criaEBuscaCliente() {
        ClienteResponse criado = service.criar(requisicao("joao@ufes.br"));
        assertNotNull(criado.id());
        assertEquals("joao@ufes.br", service.buscarPorId(criado.id()).email());
    }

    @Test
    void buscarInexistenteLancaNaoEncontrado() {
        assertThrows(RecursoNaoEncontradoException.class, () -> service.buscarPorId(999L));
    }

    @Test
    void emailDuplicadoLancaRegraDeNegocio() {
        service.criar(requisicao("dup@ufes.br"));
        assertThrows(RegraDeNegocioException.class, () -> service.criar(requisicao("dup@ufes.br")));
    }
}
