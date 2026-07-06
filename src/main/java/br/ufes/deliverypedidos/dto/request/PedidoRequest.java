package br.ufes.deliverypedidos.dto.request;

import br.ufes.deliverypedidos.dto.EnderecoDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record PedidoRequest(
        @NotNull Long restauranteId,
        @Valid EnderecoDTO enderecoEntrega,
        @NotEmpty @Valid List<ItemPedidoRequest> itens) {
}
