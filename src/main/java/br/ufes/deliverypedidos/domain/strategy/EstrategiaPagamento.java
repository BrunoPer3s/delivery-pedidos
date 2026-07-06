package br.ufes.deliverypedidos.domain.strategy;

import br.ufes.deliverypedidos.domain.model.FormaPagamento;
import java.math.BigDecimal;

/**
 * Strategy de pagamento: cada forma encapsula seu próprio algoritmo de cobrança,
 * intercambiável em tempo de execução. O PagamentoService (contexto) seleciona a
 * estratégia pela FormaPagamento e delega o processamento.
 */
public interface EstrategiaPagamento {

    FormaPagamento getForma();

    ResultadoPagamento pagar(BigDecimal valorPedido);
}
