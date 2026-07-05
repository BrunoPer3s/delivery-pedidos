package br.ufes.deliverypedidos.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import br.ufes.deliverypedidos.domain.model.Papel;
import br.ufes.deliverypedidos.domain.model.Usuario;
import br.ufes.deliverypedidos.dto.EnderecoDTO;
import br.ufes.deliverypedidos.dto.request.ClienteRequest;
import br.ufes.deliverypedidos.dto.response.ClienteResponse;
import br.ufes.deliverypedidos.exception.RecursoNaoEncontradoException;
import br.ufes.deliverypedidos.exception.RegraDeNegocioException;
import br.ufes.deliverypedidos.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class ClienteServiceTest {

    @Autowired
    private ClienteService service;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private Usuario novoUsuario() {
        return usuarioRepository.save(new Usuario("Usuário", "user" + System.nanoTime() + "@ufes.br",
                "senha123", Papel.CLIENTE));
    }

    private ClienteRequest requisicao(String email) {
        return new ClienteRequest("João", email, "27999999999",
                new EnderecoDTO("Rua A", "10", "Centro", "Vitória", "29000-000"));
    }

    @Test
    void criaEBuscaCliente() {
        ClienteResponse criado = service.criar(requisicao("joao@ufes.br"), novoUsuario());
        assertNotNull(criado.id());
        assertEquals("joao@ufes.br", service.buscarPorId(criado.id()).email());
    }

    @Test
    void buscarInexistenteLancaNaoEncontrado() {
        assertThrows(RecursoNaoEncontradoException.class, () -> service.buscarPorId(999L));
    }

    @Test
    void emailDuplicadoLancaRegraDeNegocio() {
        service.criar(requisicao("dup@ufes.br"), novoUsuario());
        assertThrows(RegraDeNegocioException.class,
                () -> service.criar(requisicao("dup@ufes.br"), novoUsuario()));
    }
}
