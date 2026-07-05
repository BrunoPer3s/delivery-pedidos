package br.ufes.deliverypedidos.domain.state;

import br.ufes.deliverypedidos.domain.model.Pedido;
import br.ufes.deliverypedidos.domain.model.StatusPedido;
import br.ufes.deliverypedidos.exception.TransicaoInvalidaException;

public abstract class EstadoPedido {

    protected final Pedido pedido;

    protected EstadoPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public void confirmar() {
        proibir("confirmar");
    }

    public void iniciarPreparo() {
        proibir("iniciar o preparo de");
    }

    public void marcarPronto() {
        proibir("marcar como pronto");
    }

    public void despachar() {
        proibir("despachar");
    }

    public void entregar() {
        proibir("entregar");
    }

    public void cancelar() {
        proibir("cancelar");
    }

    public abstract StatusPedido getStatus();

    private void proibir(String acao) {
        throw new TransicaoInvalidaException(
                "Não é possível " + acao + " um pedido com status " + getStatus());
    }
}
