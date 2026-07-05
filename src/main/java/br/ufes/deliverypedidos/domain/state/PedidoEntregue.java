package br.ufes.deliverypedidos.domain.state;

import br.ufes.deliverypedidos.domain.model.Pedido;
import br.ufes.deliverypedidos.domain.model.StatusPedido;

public class PedidoEntregue extends EstadoPedido {

    public PedidoEntregue(Pedido pedido) {
        super(pedido);
    }

    @Override
    public StatusPedido getStatus() {
        return StatusPedido.ENTREGUE;
    }
}
