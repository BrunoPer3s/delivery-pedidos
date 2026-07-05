package br.ufes.deliverypedidos.domain.state;

import br.ufes.deliverypedidos.domain.model.Pedido;
import br.ufes.deliverypedidos.domain.model.StatusPedido;

public class PedidoConfirmado extends EstadoPedido {

    public PedidoConfirmado(Pedido pedido) {
        super(pedido);
    }

    @Override
    public void iniciarPreparo() {
        pedido.setEstado(new PedidoEmPreparo(pedido));
    }

    @Override
    public void cancelar() {
        pedido.setEstado(new PedidoCancelado(pedido));
    }

    @Override
    public StatusPedido getStatus() {
        return StatusPedido.CONFIRMADO;
    }
}
