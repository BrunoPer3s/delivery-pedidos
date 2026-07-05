package br.ufes.deliverypedidos.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import br.ufes.deliverypedidos.domain.model.Cliente;
import br.ufes.deliverypedidos.domain.model.Endereco;
import br.ufes.deliverypedidos.domain.model.Pedido;
import br.ufes.deliverypedidos.domain.model.Restaurante;
import br.ufes.deliverypedidos.domain.model.StatusPedido;
import br.ufes.deliverypedidos.exception.TransicaoInvalidaException;
import org.junit.jupiter.api.Test;

class PedidoStateTest {

    private Pedido novoPedido() {
        return new Pedido(new Cliente(), new Restaurante(), new Endereco());
    }

    @Test
    void pedidoComecaComoRealizado() {
        assertEquals(StatusPedido.REALIZADO, novoPedido().getStatus());
    }

    @Test
    void fluxoCompletoAteEntregue() {
        Pedido pedido = novoPedido();
        pedido.confirmar();
        assertEquals(StatusPedido.CONFIRMADO, pedido.getStatus());
        pedido.iniciarPreparo();
        assertEquals(StatusPedido.EM_PREPARO, pedido.getStatus());
        pedido.marcarPronto();
        assertEquals(StatusPedido.PRONTO, pedido.getStatus());
        pedido.despachar();
        assertEquals(StatusPedido.EM_ROTA, pedido.getStatus());
        pedido.entregar();
        assertEquals(StatusPedido.ENTREGUE, pedido.getStatus());
    }

    @Test
    void podeCancelarPedidoRealizado() {
        Pedido pedido = novoPedido();
        pedido.cancelar();
        assertEquals(StatusPedido.CANCELADO, pedido.getStatus());
    }

    @Test
    void naoPodeEntregarPedidoRecemRealizado() {
        Pedido pedido = novoPedido();
        assertThrows(TransicaoInvalidaException.class, pedido::entregar);
    }

    @Test
    void naoPodeCancelarPedidoJaEntregue() {
        Pedido pedido = novoPedido();
        pedido.confirmar();
        pedido.iniciarPreparo();
        pedido.marcarPronto();
        pedido.despachar();
        pedido.entregar();
        assertThrows(TransicaoInvalidaException.class, pedido::cancelar);
    }
}
