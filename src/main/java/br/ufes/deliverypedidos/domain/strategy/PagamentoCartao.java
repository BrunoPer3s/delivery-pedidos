package br.ufes.deliverypedidos.domain.strategy;

import br.ufes.deliverypedidos.domain.model.FormaPagamento;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.springframework.stereotype.Component;

@Component
public class PagamentoCartao implements EstrategiaPagamento {

    private static final BigDecimal ACRESCIMO = new BigDecimal("1.05");

    @Override
    public FormaPagamento getForma() {
        return FormaPagamento.CARTAO;
    }

    @Override
    public ResultadoPagamento pagar(BigDecimal valorPedido) {
        BigDecimal valorFinal = valorPedido.multiply(ACRESCIMO).setScale(2, RoundingMode.HALF_UP);
        return new ResultadoPagamento(valorFinal, "Pagamento no cartão aprovado com 5% de acréscimo");
    }
}
