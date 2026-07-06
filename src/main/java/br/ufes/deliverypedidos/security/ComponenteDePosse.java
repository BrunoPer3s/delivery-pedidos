package br.ufes.deliverypedidos.security;

import br.ufes.deliverypedidos.domain.model.Pedido;
import br.ufes.deliverypedidos.domain.model.Usuario;
import br.ufes.deliverypedidos.repository.ClienteRepository;
import br.ufes.deliverypedidos.repository.EntregadorRepository;
import br.ufes.deliverypedidos.repository.PedidoRepository;
import br.ufes.deliverypedidos.repository.ProdutoRepository;
import br.ufes.deliverypedidos.repository.RestauranteRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Verificações de posse usadas nas expressões @PreAuthorize (ex.: um cliente só
 * mexe no próprio pedido). Exposto como bean "posse" para o SpEL: @posse.donoDoPedido(...).
 */
@Component("posse")
public class ComponenteDePosse {

    private final ClienteRepository clienteRepository;
    private final RestauranteRepository restauranteRepository;
    private final EntregadorRepository entregadorRepository;
    private final ProdutoRepository produtoRepository;
    private final PedidoRepository pedidoRepository;

    public ComponenteDePosse(ClienteRepository clienteRepository, RestauranteRepository restauranteRepository,
                             EntregadorRepository entregadorRepository, ProdutoRepository produtoRepository,
                             PedidoRepository pedidoRepository) {
        this.clienteRepository = clienteRepository;
        this.restauranteRepository = restauranteRepository;
        this.entregadorRepository = entregadorRepository;
        this.produtoRepository = produtoRepository;
        this.pedidoRepository = pedidoRepository;
    }

    @Transactional(readOnly = true)
    public boolean donoDoCliente(Long clienteId, Authentication auth) {
        return clienteRepository.findById(clienteId)
                .map(c -> ehDono(c.getUsuario(), auth))
                .orElse(false);
    }

    @Transactional(readOnly = true)
    public boolean donoDoRestaurante(Long restauranteId, Authentication auth) {
        return restauranteRepository.findById(restauranteId)
                .map(r -> ehDono(r.getUsuario(), auth))
                .orElse(false);
    }

    @Transactional(readOnly = true)
    public boolean donoDoEntregador(Long entregadorId, Authentication auth) {
        return entregadorRepository.findById(entregadorId)
                .map(e -> ehDono(e.getUsuario(), auth))
                .orElse(false);
    }

    @Transactional(readOnly = true)
    public boolean donoDoProduto(Long produtoId, Authentication auth) {
        return produtoRepository.findById(produtoId)
                .map(p -> ehDono(p.getRestaurante().getUsuario(), auth))
                .orElse(false);
    }

    @Transactional(readOnly = true)
    public boolean donoDoPedido(Long pedidoId, Authentication auth) {
        return pedidoRepository.findById(pedidoId)
                .map(p -> ehDono(p.getCliente().getUsuario(), auth))
                .orElse(false);
    }

    @Transactional(readOnly = true)
    public boolean restauranteDoPedido(Long pedidoId, Authentication auth) {
        return pedidoRepository.findById(pedidoId)
                .map(p -> ehDono(p.getRestaurante().getUsuario(), auth))
                .orElse(false);
    }

    @Transactional(readOnly = true)
    public boolean entregadorDoPedido(Long pedidoId, Authentication auth) {
        return pedidoRepository.findById(pedidoId)
                .map(p -> p.getEntregador() != null && ehDono(p.getEntregador().getUsuario(), auth))
                .orElse(false);
    }

    @Transactional(readOnly = true)
    public boolean podeVerPedido(Long pedidoId, Authentication auth) {
        return pedidoRepository.findById(pedidoId).map(p -> {
            Long usuarioId = usuarioId(auth);
            if (p.getCliente().getUsuario().getId().equals(usuarioId)) {
                return true;
            }
            if (p.getRestaurante().getUsuario().getId().equals(usuarioId)) {
                return true;
            }
            return p.getEntregador() != null && p.getEntregador().getUsuario().getId().equals(usuarioId);
        }).orElse(false);
    }

    private boolean ehDono(Usuario dono, Authentication auth) {
        return dono != null && dono.getId().equals(usuarioId(auth));
    }

    private Long usuarioId(Authentication auth) {
        return ((Usuario) auth.getPrincipal()).getId();
    }
}
