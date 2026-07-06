package br.ufes.deliverypedidos.dto.response;

import br.ufes.deliverypedidos.domain.model.FormaPagamento;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PagamentoResponse(
        Long id,
        Long pedidoId,
        FormaPagamento forma,
        BigDecimal valor,
        String detalhe,
        LocalDateTime dataHora) {
}
