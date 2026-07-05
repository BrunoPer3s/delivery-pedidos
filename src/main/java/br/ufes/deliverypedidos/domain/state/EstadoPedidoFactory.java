package br.ufes.deliverypedidos.domain.state;

import br.ufes.deliverypedidos.domain.model.Pedido;
import br.ufes.deliverypedidos.domain.model.StatusPedido;

public final class EstadoPedidoFactory {

    private EstadoPedidoFactory() {
    }

    public static EstadoPedido criar(Pedido pedido, StatusPedido status) {
        return switch (status) {
            case REALIZADO -> new PedidoRealizado(pedido);
            case CONFIRMADO -> new PedidoConfirmado(pedido);
            case EM_PREPARO -> new PedidoEmPreparo(pedido);
            case PRONTO -> new PedidoPronto(pedido);
            case EM_ROTA -> new PedidoEmRota(pedido);
            case ENTREGUE -> new PedidoEntregue(pedido);
            case CANCELADO -> new PedidoCancelado(pedido);
        };
    }
}
