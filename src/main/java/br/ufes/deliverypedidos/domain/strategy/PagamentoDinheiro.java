package br.ufes.deliverypedidos.domain.strategy;

import br.ufes.deliverypedidos.domain.model.FormaPagamento;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.springframework.stereotype.Component;

@Component
public class PagamentoDinheiro implements EstrategiaPagamento {

    @Override
    public FormaPagamento getForma() {
        return FormaPagamento.DINHEIRO;
    }

    @Override
    public ResultadoPagamento pagar(BigDecimal valorPedido) {
        BigDecimal valorFinal = valorPedido.setScale(2, RoundingMode.HALF_UP);
        return new ResultadoPagamento(valorFinal, "Pagamento em dinheiro na entrega");
    }
}
