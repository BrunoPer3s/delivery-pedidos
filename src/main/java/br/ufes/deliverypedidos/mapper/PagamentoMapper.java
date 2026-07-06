package br.ufes.deliverypedidos.mapper;

import br.ufes.deliverypedidos.domain.model.Pagamento;
import br.ufes.deliverypedidos.dto.response.PagamentoResponse;
import org.springframework.stereotype.Component;

@Component
public class PagamentoMapper {

    public PagamentoResponse toResponse(Pagamento pagamento) {
        return new PagamentoResponse(
                pagamento.getId(),
                pagamento.getPedido().getId(),
                pagamento.getForma(),
                pagamento.getValor(),
                pagamento.getDetalhe(),
                pagamento.getDataHora());
    }
}
