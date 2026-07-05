package br.ufes.deliverypedidos.service;

import br.ufes.deliverypedidos.domain.model.Cliente;
import br.ufes.deliverypedidos.domain.model.Endereco;
import br.ufes.deliverypedidos.domain.model.Entregador;
import br.ufes.deliverypedidos.domain.model.ItemPedido;
import br.ufes.deliverypedidos.domain.model.Pedido;
import br.ufes.deliverypedidos.domain.model.Produto;
import br.ufes.deliverypedidos.domain.model.Restaurante;
import br.ufes.deliverypedidos.domain.model.StatusPedido;
import br.ufes.deliverypedidos.dto.request.ItemPedidoRequest;
import br.ufes.deliverypedidos.dto.request.PedidoRequest;
import br.ufes.deliverypedidos.dto.response.PedidoResponse;
import br.ufes.deliverypedidos.exception.RecursoNaoEncontradoException;
import br.ufes.deliverypedidos.exception.RegraDeNegocioException;
import br.ufes.deliverypedidos.mapper.EnderecoMapper;
import br.ufes.deliverypedidos.mapper.PedidoMapper;
import br.ufes.deliverypedidos.repository.ClienteRepository;
import br.ufes.deliverypedidos.repository.EntregadorRepository;
import br.ufes.deliverypedidos.repository.PedidoRepository;
import br.ufes.deliverypedidos.repository.ProdutoRepository;
import br.ufes.deliverypedidos.repository.RestauranteRepository;
import java.util.function.Consumer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PedidoService {

    private final PedidoRepository repository;
    private final ClienteRepository clienteRepository;
    private final RestauranteRepository restauranteRepository;
    private final ProdutoRepository produtoRepository;
    private final EntregadorRepository entregadorRepository;
    private final EnderecoMapper enderecoMapper;
    private final PedidoMapper mapper;

    public PedidoService(PedidoRepository repository, ClienteRepository clienteRepository,
                         RestauranteRepository restauranteRepository, ProdutoRepository produtoRepository,
                         EntregadorRepository entregadorRepository, EnderecoMapper enderecoMapper,
                         PedidoMapper mapper) {
        this.repository = repository;
        this.clienteRepository = clienteRepository;
        this.restauranteRepository = restauranteRepository;
        this.produtoRepository = produtoRepository;
        this.entregadorRepository = entregadorRepository;
        this.enderecoMapper = enderecoMapper;
        this.mapper = mapper;
    }

    @Transactional
    public PedidoResponse criar(PedidoRequest req) {
        Cliente cliente = buscarCliente(req.clienteId());
        Restaurante restaurante = buscarRestaurante(req.restauranteId());

        Endereco enderecoEntrega = req.enderecoEntrega() != null
                ? enderecoMapper.toEntity(req.enderecoEntrega())
                : cliente.getEndereco();
        if (enderecoEntrega == null) {
            throw new RegraDeNegocioException("Informe o endereço de entrega: o cliente não possui endereço cadastrado");
        }

        Pedido pedido = new Pedido(cliente, restaurante, enderecoEntrega);
        for (ItemPedidoRequest itemReq : req.itens()) {
            Produto produto = buscarProduto(itemReq.produtoId());
            validarProduto(produto, restaurante);
            pedido.adicionarItem(new ItemPedido(produto, itemReq.quantidade()));
        }

        return mapper.toResponse(repository.save(pedido));
    }

    @Transactional(readOnly = true)
    public Page<PedidoResponse> listar(StatusPedido status, Pageable pageable) {
        Page<Pedido> pedidos = (status != null)
                ? repository.findByStatus(status, pageable)
                : repository.findAll(pageable);
        return pedidos.map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<PedidoResponse> listarPorCliente(Long clienteId, Pageable pageable) {
        return repository.findByClienteId(clienteId, pageable).map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<PedidoResponse> listarPorRestaurante(Long restauranteId, Pageable pageable) {
        return repository.findByRestauranteId(restauranteId, pageable).map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public PedidoResponse buscarPorId(Long id) {
        return mapper.toResponse(buscarEntidade(id));
    }

    @Transactional
    public PedidoResponse confirmar(Long id) {
        return aplicarTransicao(id, Pedido::confirmar);
    }

    @Transactional
    public PedidoResponse iniciarPreparo(Long id) {
        return aplicarTransicao(id, Pedido::iniciarPreparo);
    }

    @Transactional
    public PedidoResponse marcarPronto(Long id) {
        return aplicarTransicao(id, Pedido::marcarPronto);
    }

    @Transactional
    public PedidoResponse despachar(Long id) {
        return aplicarTransicao(id, Pedido::despachar);
    }

    @Transactional
    public PedidoResponse entregar(Long id) {
        return aplicarTransicao(id, Pedido::entregar);
    }

    @Transactional
    public PedidoResponse cancelar(Long id) {
        return aplicarTransicao(id, Pedido::cancelar);
    }

    @Transactional
    public PedidoResponse atribuirEntregador(Long pedidoId, Long entregadorId) {
        Pedido pedido = buscarEntidade(pedidoId);
        if (pedido.getStatus() == StatusPedido.ENTREGUE || pedido.getStatus() == StatusPedido.CANCELADO) {
            throw new RegraDeNegocioException(
                    "Não é possível atribuir entregador a um pedido com status " + pedido.getStatus());
        }
        Entregador entregador = buscarEntregador(entregadorId);
        pedido.setEntregador(entregador);
        return mapper.toResponse(pedido);
    }

    // Cada transição do State é disparada aqui; o gancho do Observer (rastreamento)
    // será plugado em notificarMudancaDeEstado quando o estado muda de fato.
    private PedidoResponse aplicarTransicao(Long id, Consumer<Pedido> transicao) {
        Pedido pedido = buscarEntidade(id);
        StatusPedido anterior = pedido.getStatus();
        transicao.accept(pedido);
        if (pedido.getStatus() != anterior) {
            notificarMudancaDeEstado(pedido);
        }
        return mapper.toResponse(pedido);
    }

    private void notificarMudancaDeEstado(Pedido pedido) {
        // Ponto de extensão do Observer: os observadores de rastreamento
        // serão notificados aqui a cada mudança de estado do pedido.
    }

    private void validarProduto(Produto produto, Restaurante restaurante) {
        if (!produto.getRestaurante().getId().equals(restaurante.getId())) {
            throw new RegraDeNegocioException(
                    "O produto " + produto.getId() + " não pertence ao restaurante " + restaurante.getId());
        }
        if (!produto.isDisponivel()) {
            throw new RegraDeNegocioException("Produto indisponível: " + produto.getNome());
        }
    }

    private Pedido buscarEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pedido não encontrado: " + id));
    }

    private Cliente buscarCliente(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente não encontrado: " + id));
    }

    private Restaurante buscarRestaurante(Long id) {
        return restauranteRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Restaurante não encontrado: " + id));
    }

    private Produto buscarProduto(Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto não encontrado: " + id));
    }

    private Entregador buscarEntregador(Long id) {
        return entregadorRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Entregador não encontrado: " + id));
    }
}
