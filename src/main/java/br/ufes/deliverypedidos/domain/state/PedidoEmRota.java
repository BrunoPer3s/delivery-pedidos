package br.ufes.deliverypedidos.domain.state;

import br.ufes.deliverypedidos.domain.model.Pedido;
import br.ufes.deliverypedidos.domain.model.StatusPedido;

public class PedidoEmRota extends EstadoPedido {

    public PedidoEmRota(Pedido pedido) {
        super(pedido);
    }

    @Override
    public void entregar() {
        pedido.setEstado(new PedidoEntregue(pedido));
    }

    @Override
    public StatusPedido getStatus() {
        return StatusPedido.EM_ROTA;
    }
}
