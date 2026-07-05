package br.ufes.deliverypedidos.domain.state;

import br.ufes.deliverypedidos.domain.model.Pedido;
import br.ufes.deliverypedidos.domain.model.StatusPedido;

public class PedidoRealizado extends EstadoPedido {

    public PedidoRealizado(Pedido pedido) {
        super(pedido);
    }

    @Override
    public void confirmar() {
        pedido.setEstado(new PedidoConfirmado(pedido));
    }

    @Override
    public void cancelar() {
        pedido.setEstado(new PedidoCancelado(pedido));
    }

    @Override
    public StatusPedido getStatus() {
        return StatusPedido.REALIZADO;
    }
}
