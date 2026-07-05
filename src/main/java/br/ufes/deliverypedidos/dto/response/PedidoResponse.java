package br.ufes.deliverypedidos.dto.response;

import br.ufes.deliverypedidos.domain.model.StatusPedido;
import br.ufes.deliverypedidos.dto.EnderecoDTO;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record PedidoResponse(
        Long id,
        Long clienteId,
        Long restauranteId,
        Long entregadorId,
        StatusPedido status,
        BigDecimal valorTotal,
        LocalDateTime dataHora,
        EnderecoDTO enderecoEntrega,
        List<ItemPedidoResponse> itens) {
}
