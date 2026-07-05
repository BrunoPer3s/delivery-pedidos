package br.ufes.deliverypedidos.domain.state;

import br.ufes.deliverypedidos.domain.model.Pedido;
import br.ufes.deliverypedidos.domain.model.StatusPedido;

public class PedidoEmPreparo extends EstadoPedido {

    public PedidoEmPreparo(Pedido pedido) {
        super(pedido);
    }

    @Override
    public void marcarPronto() {
        pedido.setEstado(new PedidoPronto(pedido));
    }

    @Override
    public void cancelar() {
        pedido.setEstado(new PedidoCancelado(pedido));
    }

    @Override
    public StatusPedido getStatus() {
        return StatusPedido.EM_PREPARO;
    }
}
