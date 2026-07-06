package br.ufes.deliverypedidos.domain.strategy;

import br.ufes.deliverypedidos.domain.model.FormaPagamento;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.springframework.stereotype.Component;

@Component
public class PagamentoPix implements EstrategiaPagamento {

    private static final BigDecimal DESCONTO = new BigDecimal("0.95");

    @Override
    public FormaPagamento getForma() {
        return FormaPagamento.PIX;
    }

    @Override
    public ResultadoPagamento pagar(BigDecimal valorPedido) {
        BigDecimal valorFinal = valorPedido.multiply(DESCONTO).setScale(2, RoundingMode.HALF_UP);
        return new ResultadoPagamento(valorFinal, "Pagamento via PIX aprovado com 5% de desconto");
    }
}
