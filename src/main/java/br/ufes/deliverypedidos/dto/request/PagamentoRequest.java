package br.ufes.deliverypedidos.dto.request;

import br.ufes.deliverypedidos.domain.model.FormaPagamento;
import jakarta.validation.constraints.NotNull;

public record PagamentoRequest(@NotNull FormaPagamento forma) {
}
