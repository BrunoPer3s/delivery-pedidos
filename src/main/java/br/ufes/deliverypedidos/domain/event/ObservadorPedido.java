package br.ufes.deliverypedidos.domain.event;

import br.ufes.deliverypedidos.domain.model.Pedido;

/**
 * Observer do pedido: implementações são notificadas a cada mudança de estado.
 * O PedidoService (sujeito) coleta os observadores via injeção do Spring e
 * percorre a lista notificando cada um após uma transição.
 */
public interface ObservadorPedido {

    void aoMudarEstado(Pedido pedido);
}
