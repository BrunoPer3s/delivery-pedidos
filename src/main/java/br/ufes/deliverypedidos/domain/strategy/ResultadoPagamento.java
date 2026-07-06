package br.ufes.deliverypedidos.domain.strategy;

import java.math.BigDecimal;

/**
 * Resultado do processamento de um pagamento: o valor efetivamente cobrado (que
 * pode variar conforme a forma, ex.: desconto no PIX) e um detalhe descritivo.
 */
public record ResultadoPagamento(BigDecimal valorFinal, String detalhe) {
}
