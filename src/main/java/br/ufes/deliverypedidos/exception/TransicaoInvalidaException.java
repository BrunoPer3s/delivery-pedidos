package br.ufes.deliverypedidos.exception;

public class TransicaoInvalidaException extends RuntimeException {

    public TransicaoInvalidaException(String mensagem) {
        super(mensagem);
    }
}
