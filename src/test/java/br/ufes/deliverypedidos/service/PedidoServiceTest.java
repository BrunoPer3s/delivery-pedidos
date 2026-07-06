package br.ufes.deliverypedidos.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.ufes.deliverypedidos.domain.model.Papel;
import br.ufes.deliverypedidos.domain.model.StatusPedido;
import br.ufes.deliverypedidos.domain.model.Usuario;
import br.ufes.deliverypedidos.dto.EnderecoDTO;
import br.ufes.deliverypedidos.dto.request.ClienteRequest;
import br.ufes.deliverypedidos.dto.request.EntregadorRequest;
import br.ufes.deliverypedidos.dto.request.ItemPedidoRequest;
import br.ufes.deliverypedidos.dto.request.PedidoRequest;
import br.ufes.deliverypedidos.dto.request.ProdutoRequest;
import br.ufes.deliverypedidos.dto.request.RestauranteRequest;
import br.ufes.deliverypedidos.dto.response.EntregadorResponse;
import br.ufes.deliverypedidos.dto.response.EventoRastreamentoResponse;
import br.ufes.deliverypedidos.dto.response.PedidoResponse;
import br.ufes.deliverypedidos.dto.response.ProdutoResponse;
import br.ufes.deliverypedidos.dto.response.RestauranteResponse;
import br.ufes.deliverypedidos.exception.RegraDeNegocioException;
import br.ufes.deliverypedidos.exception.TransicaoInvalidaException;
import br.ufes.deliverypedidos.repository.UsuarioRepository;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class PedidoServiceTest {

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private RestauranteService restauranteService;

    @Autowired
    private ProdutoService produtoService;

    @Autowired
    private EntregadorService entregadorService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RastreamentoService rastreamentoService;

    private EnderecoDTO endereco() {
        return new EnderecoDTO("Rua A", "10", "Centro", "Vitória", "29000-000");
    }

    private Usuario novoUsuario(Papel papel) {
        return usuarioRepository.save(new Usuario("Usuário", papel.name() + System.nanoTime() + "@x.com",
                "senha123", papel));
    }

    private RestauranteResponse novoRestaurante(String nome, BigDecimal taxa) {
        return restauranteService.criar(new RestauranteRequest(nome, "27000000000", "Italiana",
                taxa, endereco()), novoUsuario(Papel.RESTAURANTE));
    }

    // Cria um cliente e devolve o usuário dono, usado para autenticar a criação do pedido.
    private Usuario novoClienteUsuario() {
        Usuario dono = novoUsuario(Papel.CLIENTE);
        clienteService.criar(new ClienteRequest("Bruno", "bruno" + System.nanoTime() + "@x.com",
                "27999999999", endereco()), dono);
        return dono;
    }

    private ProdutoResponse novoProduto(Long restauranteId, BigDecimal preco, boolean disponivel) {
        return produtoService.criar(
                new ProdutoRequest("Pizza", "Calabresa", preco, disponivel, restauranteId));
    }

    private PedidoResponse pedidoBasico() {
        RestauranteResponse restaurante = novoRestaurante("Cantina", new BigDecimal("5.00"));
        ProdutoResponse produto = novoProduto(restaurante.id(), new BigDecimal("40.00"), true);
        return pedidoService.criar(new PedidoRequest(restaurante.id(), endereco(),
                List.of(new ItemPedidoRequest(produto.id(), 2))), novoClienteUsuario());
    }

    @Test
    void criaPedidoCalculandoTotalComTaxaDeEntrega() {
        PedidoResponse pedido = pedidoBasico();
        // 2 x 40.00 + taxa 5.00
        assertEquals(0, new BigDecimal("85.00").compareTo(pedido.valorTotal()));
        assertEquals(StatusPedido.REALIZADO, pedido.status());
        assertEquals(1, pedido.itens().size());
    }

    @Test
    void confirmarPedidoAvancaEstado() {
        PedidoResponse pedido = pedidoBasico();
        PedidoResponse confirmado = pedidoService.confirmar(pedido.id());
        assertEquals(StatusPedido.CONFIRMADO, confirmado.status());
    }

    @Test
    void transicaoInvalidaLancaConflito() {
        PedidoResponse pedido = pedidoBasico();
        assertThrows(TransicaoInvalidaException.class, () -> pedidoService.entregar(pedido.id()));
    }

    @Test
    void produtoDeOutroRestauranteLancaRegraDeNegocio() {
        RestauranteResponse restauranteA = novoRestaurante("A", new BigDecimal("5.00"));
        RestauranteResponse restauranteB = novoRestaurante("B", new BigDecimal("5.00"));
        ProdutoResponse produtoB = novoProduto(restauranteB.id(), new BigDecimal("10.00"), true);
        Usuario clienteUsuario = novoClienteUsuario();
        PedidoRequest req = new PedidoRequest(restauranteA.id(), endereco(),
                List.of(new ItemPedidoRequest(produtoB.id(), 1)));
        assertThrows(RegraDeNegocioException.class, () -> pedidoService.criar(req, clienteUsuario));
    }

    @Test
    void produtoIndisponivelLancaRegraDeNegocio() {
        RestauranteResponse restaurante = novoRestaurante("Cantina", new BigDecimal("5.00"));
        ProdutoResponse produto = novoProduto(restaurante.id(), new BigDecimal("10.00"), false);
        Usuario clienteUsuario = novoClienteUsuario();
        PedidoRequest req = new PedidoRequest(restaurante.id(), endereco(),
                List.of(new ItemPedidoRequest(produto.id(), 1)));
        assertThrows(RegraDeNegocioException.class, () -> pedidoService.criar(req, clienteUsuario));
    }

    @Test
    void atribuirEntregadorVinculaAoPedido() {
        PedidoResponse pedido = pedidoBasico();
        EntregadorResponse entregador = entregadorService.criar(
                new EntregadorRequest("Carlos", "27988887777", "ABC1D23"), novoUsuario(Papel.ENTREGADOR));
        PedidoResponse atualizado = pedidoService.atribuirEntregador(pedido.id(), entregador.id());
        assertEquals(entregador.id(), atualizado.entregadorId());
    }

    @Test
    void listaFiltradaPorStatusRetornaOPedidoConfirmado() {
        PedidoResponse pedido = pedidoBasico();
        pedidoService.confirmar(pedido.id());
        boolean encontrado = pedidoService.listar(StatusPedido.CONFIRMADO, PageRequest.of(0, 10))
                .stream().anyMatch(p -> p.id().equals(pedido.id()));
        assertTrue(encontrado);
    }

    @Test
    void rastreamentoRegistraCadaMudancaDeEstado() {
        PedidoResponse pedido = pedidoBasico();
        pedidoService.confirmar(pedido.id());
        pedidoService.iniciarPreparo(pedido.id());

        List<EventoRastreamentoResponse> eventos = rastreamentoService.doPedido(pedido.id());
        assertEquals(3, eventos.size());
        assertEquals(StatusPedido.REALIZADO, eventos.get(0).status());
        assertEquals(StatusPedido.CONFIRMADO, eventos.get(1).status());
        assertEquals(StatusPedido.EM_PREPARO, eventos.get(2).status());
    }
}
