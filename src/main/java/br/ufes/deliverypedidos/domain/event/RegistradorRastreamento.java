package br.ufes.deliverypedidos.domain.event;

import br.ufes.deliverypedidos.domain.model.Pedido;
import br.ufes.deliverypedidos.domain.model.StatusPedido;
import br.ufes.deliverypedidos.repository.EventoRastreamentoRepository;
import org.springframework.stereotype.Component;

/**
 * Observador concreto que registra um evento de rastreamento a cada mudança de
 * estado do pedido. É coletado automaticamente pelo PedidoService (List injetada).
 */
@Component
public class RegistradorRastreamento implements ObservadorPedido {

    private final EventoRastreamentoRepository repository;

    public RegistradorRastreamento(EventoRastreamentoRepository repository) {
        this.repository = repository;
    }

    @Override
    public void aoMudarEstado(Pedido pedido) {
        repository.save(new EventoRastreamento(pedido, pedido.getStatus(), descricao(pedido.getStatus())));
    }

    private String descricao(StatusPedido status) {
        return switch (status) {
            case REALIZADO -> "Pedido realizado pelo cliente";
            case CONFIRMADO -> "Pedido confirmado pelo restaurante";
            case EM_PREPARO -> "Pedido em preparo";
            case PRONTO -> "Pedido pronto para retirada";
            case EM_ROTA -> "Pedido saiu para entrega";
            case ENTREGUE -> "Pedido entregue ao cliente";
            case CANCELADO -> "Pedido cancelado";
        };
    }
}
