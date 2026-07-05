package br.ufes.deliverypedidos.domain.state;

import br.ufes.deliverypedidos.domain.model.Pedido;
import br.ufes.deliverypedidos.domain.model.StatusPedido;

public class PedidoPronto extends EstadoPedido {

    public PedidoPronto(Pedido pedido) {
        super(pedido);
    }

    @Override
    public void despachar() {
        pedido.setEstado(new PedidoEmRota(pedido));
    }

    @Override
    public StatusPedido getStatus() {
        return StatusPedido.PRONTO;
    }
}
