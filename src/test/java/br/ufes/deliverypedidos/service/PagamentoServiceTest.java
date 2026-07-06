package br.ufes.deliverypedidos.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import br.ufes.deliverypedidos.domain.model.FormaPagamento;
import br.ufes.deliverypedidos.domain.model.Papel;
import br.ufes.deliverypedidos.domain.model.Usuario;
import br.ufes.deliverypedidos.dto.EnderecoDTO;
import br.ufes.deliverypedidos.dto.request.ClienteRequest;
import br.ufes.deliverypedidos.dto.request.ItemPedidoRequest;
import br.ufes.deliverypedidos.dto.request.PedidoRequest;
import br.ufes.deliverypedidos.dto.request.ProdutoRequest;
import br.ufes.deliverypedidos.dto.request.RestauranteRequest;
import br.ufes.deliverypedidos.dto.response.PagamentoResponse;
import br.ufes.deliverypedidos.dto.response.PedidoResponse;
import br.ufes.deliverypedidos.dto.response.ProdutoResponse;
import br.ufes.deliverypedidos.dto.response.RestauranteResponse;
import br.ufes.deliverypedidos.exception.RegraDeNegocioException;
import br.ufes.deliverypedidos.repository.UsuarioRepository;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class PagamentoServiceTest {

    @Autowired
    private PagamentoService pagamentoService;

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private RestauranteService restauranteService;

    @Autowired
    private ProdutoService produtoService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private EnderecoDTO endereco() {
        return new EnderecoDTO("Rua A", "10", "Centro", "Vitória", "29000-000");
    }

    private Usuario novoUsuario(Papel papel) {
        return usuarioRepository.save(new Usuario("Usuário", papel.name() + System.nanoTime() + "@x.com",
                "senha123", papel));
    }

    // Cria um pedido de valor total conhecido (2 x 40.00 + taxa 5.00 = 85.00) e o devolve.
    private PedidoResponse pedidoDe85() {
        RestauranteResponse restaurante = restauranteService.criar(new RestauranteRequest("Cantina", "27",
                "Italiana", new BigDecimal("5.00"), endereco()), novoUsuario(Papel.RESTAURANTE));
        ProdutoResponse produto = produtoService.criar(
                new ProdutoRequest("Pizza", "Calabresa", new BigDecimal("40.00"), true, restaurante.id()));
        Usuario clienteUsuario = novoUsuario(Papel.CLIENTE);
        clienteService.criar(new ClienteRequest("Bruno", "bruno" + System.nanoTime() + "@x.com",
                "279", endereco()), clienteUsuario);
        return pedidoService.criar(new PedidoRequest(restaurante.id(), endereco(),
                List.of(new ItemPedidoRequest(produto.id(), 2))), clienteUsuario);
    }

    @Test
    void pagamentoNoPixAplicaDesconto() {
        PedidoResponse pedido = pedidoDe85();
        PagamentoResponse pagamento = pagamentoService.pagar(pedido.id(), FormaPagamento.PIX);
        // 85.00 x 0.95 = 80.75
        assertEquals(0, new BigDecimal("80.75").compareTo(pagamento.valor()));
        assertEquals(FormaPagamento.PIX, pagamento.forma());
    }

    @Test
    void pagamentoEmDinheiroMantemValor() {
        PedidoResponse pedido = pedidoDe85();
        PagamentoResponse pagamento = pagamentoService.pagar(pedido.id(), FormaPagamento.DINHEIRO);
        assertEquals(0, new BigDecimal("85.00").compareTo(pagamento.valor()));
    }

    @Test
    void naoPodePagarDuasVezes() {
        PedidoResponse pedido = pedidoDe85();
        pagamentoService.pagar(pedido.id(), FormaPagamento.PIX);
        assertThrows(RegraDeNegocioException.class,
                () -> pagamentoService.pagar(pedido.id(), FormaPagamento.CARTAO));
    }

    @Test
    void naoPodePagarPedidoCancelado() {
        PedidoResponse pedido = pedidoDe85();
        pedidoService.cancelar(pedido.id());
        assertThrows(RegraDeNegocioException.class,
                () -> pagamentoService.pagar(pedido.id(), FormaPagamento.PIX));
    }
}
